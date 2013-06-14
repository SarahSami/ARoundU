package app.child;

import static app.child.CommonUtilities.SENDER_ID;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import app.aroundu.R;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SENDER_ID);
	}

	private static final String TAG = "GCMIntentService===";

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		AccountManager manager = (AccountManager) context.getSystemService(context.ACCOUNT_SERVICE);
		Account[] list = manager.getAccountsByType("com.google");
		String account = "";
		if(list.length != 0){
			account = list[0].name;
			ChildActivity.account = account.substring(0,account.indexOf("@"));
			ChildActivity.prefs.edit().putString("account",ChildActivity.account).commit();
		}
		ChildActivity.server.postMsg("add_user?account="+account+"&id="+registrationId);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "unregistered = " + arg1);
	}

	@Override
	protected void onMessage(Context context, Intent arg1) {
		String message = arg1.getStringExtra("data");
		if(ChildActivity.prefs == null)
			ChildActivity.prefs = getSharedPreferences(ChildActivity.preferences,MODE_APPEND);

		Log.d("message in child >>",message);
        if(message.equals("getLocation"))
		{
        	
        	new RouteService();
        	if(RouteService.lat != 0 && RouteService.lng != 0)
        		ChildActivity.server.gcmServer(RouteService.lat+","+RouteService.lng);
		}
        else{
    		  if(message.contains("greenz")){
    			  if(message.compareTo("greenz") != 0)
    				  RouteService.greenZone=Integer.parseInt(message.substring(6));
    			  RouteService.points.clear();
    			  ChildActivity.prefs.edit().putString("route","").commit();
    		  }
    		  else if(message.contains(" ")){
    			  String[] sp = message.split(" ");
    			  if(sp[0].compareTo("no") == 0)
    				  RouteService.dangerousRoutes.remove(sp[1]);
    			  
    		  }
    		  else if(message.contains("parent=")){
    			  String [] params = message.split("&");
    			  String rid=params[0].substring(7);
    			  //ChildActivity.server.parentId = rid;
    			  Log.d("parent id",rid);
    			  if(!ChildActivity.prefs.contains("id"))
    				  ChildActivity.prefs.edit().putString("id","").commit();
    			  
    			  String rt = ChildActivity.prefs.getString("id","");
    			  ChildActivity.prefs.edit().putString("id",rt+"/"+rid).commit();
    		  }
    		  else{
    			  notification(context);
    			  if(!ChildActivity.prefs.contains("route"))
    				  ChildActivity.prefs.edit().putString("route","").commit();
    			  String rt = ChildActivity.prefs.getString("route","");
    			  ChildActivity.prefs.edit().putString("route",rt+"/"+message).commit();
    		  }
        			
        }
		
	}

	@Override
	protected void onError(Context arg0, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
	
	
	public void notification(Context cntx){
		String msg =  "New route received from parent.";
		String svcName = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager)cntx.getSystemService(svcName);        
        int icon = R.drawable.pch;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon,msg, when);
        notification.contentView = new RemoteViews(cntx.getPackageName(), R.layout.notification);
		notification.flags |= Notification.FLAG_AUTO_CANCEL; 
		notification.defaults = Notification.DEFAULT_SOUND;
		Intent intnt = new Intent(cntx, GCMIntentService.class);
        PendingIntent launchIntent = PendingIntent.getActivity(cntx.getApplicationContext(), 0, intnt, 0);
        notification.contentIntent = launchIntent;
        notification.contentView.setTextViewText(R.id.status_text,msg);
        int notificationRef = 1;
        notificationManager.notify(notificationRef, notification);
	}
}
