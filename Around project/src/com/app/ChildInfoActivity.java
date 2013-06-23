package com.app;

import static com.app.CommonUtilities.SENDER_ID;

import com.app.Child;
import com.app.Child.Route;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.app.GCMIntentService;
import com.app.ServerMsgParent;
import com.app.WizardActivity;

public class ChildInfoActivity extends Activity implements OnItemClickListener{
	
	// view elements
	WebView childCurrentLocation;
	LinearLayout routesList;
	private TextView viewName;
	private TextView lastLoc;
	
	public ReceiveLoc locReceiver;
	public final static String ACTION = "action!";
	
    public static String preferences = "pref";
	public static String needLocation = "";
	
	private Child child;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.child);
		locReceiver = new ReceiveLoc();
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(locReceiver, intentFilter);
        
        
        // get child id from bundle object
        //int childId = getIntent().getExtras().getInt("CHILD_ID");
		child = AccountMenu.child;//Child.findChildById(childId);
		
		// getting access to view elements
		viewName = (TextView) findViewById(R.id.childname);
		lastLoc = (TextView) findViewById(R.id.lastUpdateTimestamp);
		
		viewName.setText(child.name);
		lastLoc.setText(child.name+" was here");
		
		childCurrentLocation = (WebView) findViewById(R.id.childCurrentMapLocation);
		routesList = (LinearLayout) findViewById(R.id.routesList);
		Button call = (Button) findViewById(R.id.childCallButton);
		call.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
				callChild();
			}

			
		});
		
		// setting routes adapter to routes list
//		RoutesListAdapter adapter = new RoutesListAdapter(this, R.id.routesList, R.layout.route_list_item, child.routes);
//		routesList.setAdapter(adapter);
		for (int i = 0; i < child.routes.size(); i++) {
			Child.Route route = child.routes.get(i);
			routesList.addView(buildRouteView(route));
		}
		
		
	}
	
	private void displayCurrentLocation(double lattitude, double longitude){
		final String centerURL = "javascript:init(" +lattitude + "," +longitude+ ")";
		childCurrentLocation.getSettings().setJavaScriptEnabled(true);
		WebSettings webSettings = childCurrentLocation.getSettings();
		webSettings.setJavaScriptEnabled(true);
	  	  
		childCurrentLocation.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url){
				childCurrentLocation.loadUrl(centerURL);
				}
			}
		);
		childCurrentLocation.loadUrl("file:///android_asset/location.html");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void addNewRoute(View view){
    	Intent i = new Intent(this, MapActivity.class);
	    startActivityForResult(i, 20);
	}

	@Override
	public void onItemClick(AdapterView<?> listItem, View view, int position, long arg3) {
//		WebView routeSteps = (WebView) listItem.findViewById(R.id.routeSteps);
		
//		listItem.findViewById(R.id.routeActions).setVisibility(View.VISIBLE);
//		routeSteps.setVisibility(View.VISIBLE);
	}
	
	private View buildRouteView(final Child.Route route) {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
		LinearLayout routeView = (LinearLayout) inflater.inflate(R.layout.route_list_item, null);
		
		// getting access to route views
		TextView routeName = (TextView) routeView.findViewById(R.id.routeName);
		final WebView routeSteps = (WebView) routeView.findViewById(R.id.routeSteps);
		final LinearLayout routeActions = (LinearLayout) routeView.findViewById(R.id.routeActions);
		
		Button send = (Button) routeActions.findViewById(R.id.routeSendButton);
		final ToggleButton toggle = (ToggleButton) findViewById(R.id.activationToggleButton);
		
		toggle.setOnClickListener(new OnClickListener() {
			@Override
	        public void onClick(View arg0) {
				//TODO :: put in on-off list
	            if(toggle.isChecked()){
	            	AccountMenu.server.gcmServer("parent="+AccountMenu.prefs.getString("id",""));
	            	// AccountMenu.onlineChilds.add(object);
	            }else{
	            	// AccountMenu.onlineChilds.remove(object);
	            }
	        }
				
		});
		
		send.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
				//TODO :: send route here
				AccountMenu.server.gcmServer("new msg to child");
			}
		});
		// setting route actions (send, edit and delete) and routeSteps viewer to be invisible
		// it will be displayed only when user hits the name of the route
		routeActions.setVisibility(View.GONE);
		routeSteps.setVisibility(View.GONE);
		
		// display only view name
		routeName.setText(route.name);
		
		final ScrollView sc = (ScrollView)findViewById(R.id.scrollView1);
		
		routeView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(routeActions.getVisibility() == View.VISIBLE){
					routeActions.setVisibility(View.GONE);
					routeSteps.setVisibility(View.GONE);
				}else{
					showMap(route,routeSteps);
					routeActions.setVisibility(View.VISIBLE);
					routeSteps.setVisibility(View.VISIBLE);
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							sc.scrollBy(0, routeActions.getBottom());
						}
					});
				}
			}
		});
		
		return routeView;
	}
	
	
	private void callChild() {
		String num = child.number;
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:"+num));
		startActivity(callIntent);
		
	}

	private void showMap(Route route, final WebView webview) {
		Pair<Double, Double> start = route.steps.get(0);
		Pair<Double, Double> end = route.steps.get(route.steps.size()-1);
		final String centerURL = "javascript:init(" +start.first + "," +start.second+ ","+
													end.first+","+end.second+")";
		webview.getSettings().setJavaScriptEnabled(true);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
	  	  
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url){
				webview.loadUrl(centerURL);
				}
			}
		);
		webview.loadUrl("file:///android_asset/route.html");
		
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

	
	
	
	private void showLoc(double lat,double lng){
		child.lat = lat;
		child.lng = lng;
		displayCurrentLocation(child.lat, child.lng);
		
	}
	
	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			Log.d("debug","errorrr");
		}
	}
	
	public class ReceiveLoc extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent in) {
			String loc = in.getStringExtra("loc");
			if(loc != null){
				needLocation = loc;
	    		String tmp = needLocation;
	    		String [] msg = tmp.split(",");
	   		    double lat = Double.parseDouble(msg[0]);
	  			double longt = Double.parseDouble(msg[1]);
	  			showLoc(lat,longt);
    
	}
	}
}
	
}
