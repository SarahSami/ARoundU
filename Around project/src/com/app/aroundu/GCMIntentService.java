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
		
		// solving the issue of initializing AsyncTasks with wrong loopers
		// REF: https://code.google.com/p/android/issues/detail?id=20915
		try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
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
			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("id", registrationId).commit();
			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("account", account).commit();
			AccountMenu.server.postMsg("add_user?account="+ GCMIntentService.account + "&id="+ GCMIntentService.regId);
		} else {
			ChildActivity.account = account;
			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("account", ChildActivity.account).commit();
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
				if(child == null){
					child = AccountMenu.map.get(params[1]+"@gmail.com");
					if(child == null){
						String m = params[1].substring(0,params[1].indexOf('@'));
						child = AccountMenu.map.get(m);
					}
						
				}

			}
			if (message.contains(",") || message.contains("location is unknown")) {
				Intent intn = new Intent();
				intn.setAction(ChildInfoActivity.ACTION);
				intn.putExtra("loc", "" + message);
				context.sendBroadcast(intn);

			}else if (message.contains("ACCEPT")){
				notification(context,child,"accepted your connection request.");
				
			}else if (message.contains("DECLINE")){
				deleteChild(child);
				notification(context,child,"declined your connection request.");
				
			}
			else {
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
					String [] params = message.split("&");
					String rid = params[0].substring(7);
					String ac = params[1].substring(8);
					showDialog(context,ac,rid);
					Log.d("parent id", rid);
					Log.d("parent account",ac);
					
				} else if(message.contains("new route")){
					message = message.substring(10);
					String name = message.substring(0,message.indexOf(':'));
					message = message.substring(5);
					Log.d("name of route",name);
					Log.d("the route is ",message);
					notification(context,"New route received from parent.",name);
					RouteService.points.clear();
					ChildActivity.prefs.edit().putString("route",message).commit();
				} else if(message.contains("remove")){
					String id = message.substring(message.indexOf(':')+1);
					Log.d("id >> ",id);
					removeParentId(id,context);
				}

			}
		}
	}

	private void removeParentId(String id,Context cnt){
		String parentIds = PreferenceManager.getDefaultSharedPreferences(cnt.getApplicationContext()).getString("id", "");
		if(parentIds.charAt(0) == '/')
			parentIds = parentIds.substring(1);
		
		String [] ids = parentIds.split("/");
		String tmp = "";
		for(int i=0;i<ids.length;i++){
			if(!ids[i].equals(id))
				tmp = tmp+"/"+ids[i];
		}
		
		PreferenceManager.getDefaultSharedPreferences(cnt.getApplicationContext()).edit().putString("id",tmp).commit();	
	}
	
	private void showDialog(Context context,String ac,final String rid) {
		Intent i = new Intent(this,DialogActivity.class);
		i.putExtra("id", rid);
		i.putExtra("account", ac);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		
		
		
	}

	private void deleteChild(String ch){
		String childs = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("child","");
		if(childs.charAt(0) == '/')
			childs = childs.substring(1);
		
		String []childsArray = childs.split("/");
		String []tmp = new String[childsArray.length];
		Gson gson = new Gson();

		for (int i = 0; i < childsArray.length; i++) {
			Child c = gson.fromJson(childsArray[i], Child.class);
			if(!c.name.equals(ch))
				tmp[i] = childsArray[i];
				
		}
		String final_childs = "";
		for (int i = 0; i < tmp.length; i++) {
			if(tmp[i] != null){
				final_childs = final_childs +"/"+tmp[i];
			}
				
		}
		PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("child",final_childs).commit();
			
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
		    if(c.name.compareTo(child) == 0) //null pointer exception here
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

	public void notification(Context cntx,String name,String msg) {
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
		notification.contentView.setTextViewText(R.id.status_text, msg);
		notification.contentView.setTextViewText(R.id.name,name);
		int notificationRef = 1;
		notificationManager.notify(notificationRef, notification);
	}
}
