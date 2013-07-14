package com.app.aroundu;

import static com.app.aroundu.CommonUtilities.SENDER_ID;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.app.aroundu.AccountMenu;
import com.app.aroundu.ChildActivity;
import com.app.aroundu.ChildInfoActivity;
import com.app.aroundu.RouteService;
import com.app.aroundu.WizardActivity;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SENDER_ID);
	}

	private static final String TAG = "GCMIntentService===";
	private int id = 0;
	public static String account;
	public static String regId;
	private String type;

	@Override
	public void onCreate() {
		super.onCreate();
		type = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("user", "");
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.d("registered", "on Registered");
		
		AccountManager manager = (AccountManager) context.getSystemService(context.ACCOUNT_SERVICE);
		Account[] list = manager.getAccountsByType("com.google");
		if (list.length != 0) {
			account = list[0].name;
			//account = account.substring(0,account.indexOf('@'));
			Log.d("account",account);
		}

		if (type.equals("parent")) {
			regId = registrationId;
			AccountMenu.prefs.edit().putString("id", registrationId).commit();
			AccountMenu.prefs.edit().putString("account", account).commit();
			AccountMenu.server.postMsg("add_user?account="+ GCMIntentService.account + "&id="+ GCMIntentService.regId);
		} else {
			ChildActivity.account = account;
			ChildActivity.prefs.edit().putString("account", ChildActivity.account).commit();
			ChildActivity.server.postMsg("add_user?account=" + account + "&id="+ registrationId);
		}

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "unregistered = " + arg1);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		String message = intent.getStringExtra("data");
		if (type.equals("parent")) {
			Log.d("msg  in parent", message);
			String child = "";
			if (message.contains(":")) {
				String[] params = message.split(":");
				message = params[0];
				child = AccountMenu.map.get(params[1]);
				if(child == null)
					child = AccountMenu.map.get(params[1]+"@gmail.com");

			}
			if (message.contains(",") || message.contains("location is unknown")) {
				Intent intn = new Intent();
				intn.setAction(ChildInfoActivity.ACTION);
				intn.putExtra("loc", "" + message);
				context.sendBroadcast(intn);

			} else {
				if (onlineChild(child)) {
					if (message.contains("battery")) {

						String svcName = Context.NOTIFICATION_SERVICE;
						message += " %";
						NotificationManager notificationManager;
						notificationManager = (NotificationManager) context.getSystemService(svcName);

						int icon = R.drawable.pch;
						long when = System.currentTimeMillis();
						Notification notification = new Notification(icon,message, when);
						notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						notification.defaults = Notification.DEFAULT_SOUND;
						Intent intnt = new Intent(context,GCMIntentService.class);
						PendingIntent launchIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intnt, 0);
						notification.contentIntent = launchIntent;
						notification.contentView.setTextViewText(R.id.status_text, message);
						notification.contentView.setTextViewText(R.id.name,child);
						int notificationRef = 1;
						notificationManager.notify(notificationRef,notification);
					} else {
						if (id == 0)
							id = 1;
						else
							id = 0;

						String svcName = Context.NOTIFICATION_SERVICE;
						NotificationManager notificationManager;
						notificationManager = (NotificationManager) context.getSystemService(svcName);

						int icon = R.drawable.pch;
						long when = System.currentTimeMillis();
						Notification notification = new Notification(icon,message, when);
						notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						Intent intnt = new Intent(context,GCMIntentService.class);
						PendingIntent launchIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intnt, 0);
						notification.contentIntent = launchIntent;
						notification.contentView.setTextViewText(R.id.status_text, message);
						notification.contentView.setTextViewText(R.id.name,child);
						int notificationRef = 1;
						notificationManager.notify(notificationRef,notification);
					}
				}
			}
		} else {
			if (ChildActivity.server == null) {
				ChildActivity.server = new ServerMsgChild(context);
			}
			
			Log.d("child :: ", "msg in child " + message);
			if (message.equals("getLocation")) {

				new RouteService(context);
				if (RouteService.lat != 0 && RouteService.lng != 0)
					ChildActivity.server.gcmServer(RouteService.lat + ","+ RouteService.lng);
			} else {
				if (message.contains("greenz")) {
					if (message.compareTo("greenz") != 0)
						RouteService.greenZone = Integer.parseInt(message.substring(6));
					RouteService.points.clear();
					ChildActivity.prefs.edit().putString("route", "").commit();
				}
				else if (message.contains("parent=")) {
					String rid = message.substring(7);
					Log.d("parent id", rid);
					if (!ChildActivity.prefs.contains("id"))
						ChildActivity.prefs.edit().putString("id", "").commit();

					String rt = ChildActivity.prefs.getString("id", "");
					if (!rt.contains(rid))
						ChildActivity.prefs.edit().putString("id", rt + "/" + rid).commit();
				} else if(message.contains("new route")){
					message = message.substring(10);
					String name = message.substring(0,message.indexOf(':'));
					message = message.substring(5);
					Log.d("name of route",name);
					Log.d("the route is ",message);
					notification(context,name);
					ChildActivity.prefs.edit().putString("route",message).commit();
				}

			}
		}
	}

	private boolean onlineChild(String child) {
		String chs = AccountMenu.prefs.getString("child", "");
		Gson gson = new Gson();
		String [] jsons = null;
		
		if(chs.charAt(0) == '/')
			chs = chs.substring(1);
		
		if(chs.contains("/")){
			jsons = chs.split("/");
		}
		else if(chs.compareTo("") != 0){
			jsons = new String[1];
			jsons[0] = chs;
		}
		
		for(int i=0;i<jsons.length;i++){
		    Child c = gson.fromJson(jsons[i], Child.class);
		    if(c.name.compareTo(child) == 0)
		    	return c.activationFlag;
		}
		return false;
	}

	
	@Override
	protected void onError(Context arg0, String errorId) {
		Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}

	public void notification(Context cntx,String name) {
		String msg = "New route received from parent.";
		String svcName = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager;
		notificationManager = (NotificationManager) cntx
				.getSystemService(svcName);
		int icon = R.drawable.pch;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, msg, when);
		notification.contentView = new RemoteViews(cntx.getPackageName(),
				R.layout.notification);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND;
		Intent intnt = new Intent(cntx, GCMIntentService.class);
		PendingIntent launchIntent = PendingIntent.getActivity(
				cntx.getApplicationContext(), 0, intnt, 0);
		notification.contentIntent = launchIntent;
		notification.contentView.setTextViewText(R.id.status_text, name);
		notification.contentView.setTextViewText(R.id.name,msg);
		int notificationRef = 1;
		notificationManager.notify(notificationRef, notification);
	}
}
