package app.ejust;


import com.google.android.gcm.GCMRegistrar;
import static app.ejust.CommonUtilities.SENDER_ID;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import app.ejust.ui.AccountMenu;
import app.ejust.ui.GreenZone;
import app.ejust.ui.LocationView;


public class HomeActivity extends Activity {

	Button buttonMap, buttonLoc; 
	public static ServerMsg server;
	public ReceiveLoc locReceiver;
	public final static String ACTION = "action!";
	public static SharedPreferences prefs;
    public static String preferences = "pref";
	public static String needLocation = "";

	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        locReceiver = new ReceiveLoc();
        prefs = getSharedPreferences(preferences,MODE_APPEND);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(locReceiver, intentFilter);
        buttonMap = (Button) findViewById(R.id.buttonMap);
        buttonLoc = (Button) findViewById(R.id.buttonLoc);
        server = new ServerMsg(this);
        
        checkNotNull(SENDER_ID, "SENDER_ID");

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
			regId = GCMRegistrar.getRegistrationId(this);
		}
        server.postMsg("add_user?account="+GCMIntentService.account+"&id="+GCMIntentService.regId);
	    getChild();
	    buttonMap.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
	            map();
	    	}
	    });
	    buttonLoc.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
	            getLocation();
	    	}
	    });
	    
		
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		try{
	    	unregisterReceiver(locReceiver);
	    	}catch(IllegalArgumentException e){
	    		
	    	}
		super.onPause();
	}


	private void map(){
		Intent i = new Intent(this, GreenZone.class);
	    startActivity(i);
	}
	
	private void getLocation(){
		server.gcmServer("getLocation");
	}
	
	private void showLoc(double lat,double lng){
		Intent myIntent = new Intent(this, LocationView.class);
		myIntent.putExtra("lat",lat+"");
		myIntent.putExtra("lng",lng+"");
		startActivity(myIntent);
		
	}
	
	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			Log.d("debug","errorrr");
		}
	}

	
	private void getChild(){
		 String ch = AccountMenu.child;
		    
		    if(!ch.contains("@gmail"))
		    	ch+="@gmail.com";
		    Log.d("chillddd",ch);
			server.postMsg("/get_id?account="+ch);
		
			
	}

	
	public class ReceiveLoc extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent in) {
			String loc = in.getStringExtra("loc");
			if(loc != null){
				needLocation = loc;
	    		String tmp = needLocation;
	    		String [] mssg = tmp.split(",");
	   		    double lat = Double.parseDouble(mssg[0]);
	  			double longt = Double.parseDouble(mssg[1]);
	  			showLoc(lat,longt);
    
	}
	}
}
}