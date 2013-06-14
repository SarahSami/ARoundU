package app.ejust;

import static app.ejust.CommonUtilities.SENDER_ID;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import app.ejust.ui.AccountMenu;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SENDER_ID);
	}

	private static final String TAG = "GCMIntentService===";
	private int id = 0;
	public static String account;
	public static String regId;
	
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.d("registered","on Registered");
		regId = registrationId;
		AccountManager manager = (AccountManager) context.getSystemService(context.ACCOUNT_SERVICE);
		Account[] list = manager.getAccountsByType("com.google");
		if(list.length != 0){
			account = list[0].name;
			account = account.substring(0,account.indexOf("@"));
		}
		HomeActivity.prefs.edit().putString("id",registrationId).commit();
		HomeActivity.prefs.edit().putString("account",account).commit();
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "unregistered = " + arg1);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		
		String message = intent.getStringExtra("data");
		Log.d("msg  in parent",message);
		String child = "";
		if(message.contains(":")){
			String [] params = message.split(":");
			message = params[0];
			child = AccountMenu.map.get(params[1]);
			
		}
		if(message.contains(",")){
	        Intent intn = new Intent();
	        intn.setAction(HomeActivity.ACTION);  
	        intn.putExtra("loc", ""+message);
			context.sendBroadcast(intn);

		}
		else if(message.contains("battery")){
			 	String svcName = Context.NOTIFICATION_SERVICE;
		        message +=" %";
		        NotificationManager notificationManager;
		        notificationManager = (NotificationManager)context.getSystemService(svcName);
		        
		        int icon = R.drawable.pch;
		        long when = System.currentTimeMillis();
		        Notification notification = new Notification(icon, message, when);
		        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
				notification.flags |= Notification.FLAG_AUTO_CANCEL; 
				notification.defaults = Notification.DEFAULT_SOUND;
				Intent intnt = new Intent(context, GCMIntentService.class);
		        PendingIntent launchIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intnt, 0);
		        notification.contentIntent = launchIntent;
		        notification.contentView.setTextViewText(R.id.status_text, message);
		        notification.contentView.setTextViewText(R.id.name, child);
		        int notificationRef = 1;
		        notificationManager.notify(notificationRef, notification);
		}
		else{
		    if(id == 0)
		    	id = 1;
		    else 
		    	id = 0;

		    String svcName = Context.NOTIFICATION_SERVICE;
	        NotificationManager notificationManager;
	        notificationManager = (NotificationManager)context.getSystemService(svcName);
	        
	        int icon = R.drawable.pch;
	        long when = System.currentTimeMillis();
	        Notification notification = new Notification(icon, message, when);
	        notification.contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
			notification.flags |= Notification.FLAG_AUTO_CANCEL; 
			Intent intnt = new Intent(context, GCMIntentService.class);
	        PendingIntent launchIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intnt, 0);
	        notification.contentIntent = launchIntent;
	        notification.contentView.setTextViewText(R.id.status_text, message);
	        notification.contentView.setTextViewText(R.id.name,child);
	        int notificationRef = 1;
	        notificationManager.notify(notificationRef, notification);
		}
		
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
}
