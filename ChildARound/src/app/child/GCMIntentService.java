package app.child;

import static app.child.CommonUtilities.SENDER_ID;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
		
		Log.d("message in child ",message);
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
    		  }
    		  else if(message.contains(" ")){
    			  String[] sp = message.split(" ");
    			  if(sp[0].compareTo("no") == 0)
    				  RouteService.dangerousRoutes.remove(sp[1]);
    			  
    		  }
    		  else if(message.contains("parent=")){
    			  String [] params = message.split("&");
    			  String rid=params[0].substring(7);
    			  ChildActivity.server.parentId = rid;
    			  Log.d("parent id",rid);
    			  ChildActivity.prefs.edit().putString("id",rid).commit();
    		  }
    		  else{
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
}
