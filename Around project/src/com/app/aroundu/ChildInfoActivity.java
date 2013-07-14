package com.app.aroundu;

import static com.app.aroundu.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.app.aroundu.Child;
import com.app.aroundu.GCMIntentService;
import com.app.aroundu.ServerMsgParent;
import com.app.aroundu.WizardActivity;
import com.app.aroundu.Child.Route;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ToggleButton;


public class ChildInfoActivity extends Activity implements OnItemClickListener{
	
	// view elements
	WebView childCurrentLocation;
	LinearLayout routesList;
	private TextView viewName;
	private TextView lastLoc;
	private ToggleButton activationToggle; 
	private Context context;
	private QuickContactBadge childIcon;
	public ReceiveLoc locReceiver;
	public final static String ACTION = "action!";
	
    public static String preferences = "pref";
	
	private Child child;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.child);
		locReceiver = new ReceiveLoc();
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(locReceiver, intentFilter);
        
        
        // get child id from bundle object
        //int childId = getIntent().getExtras().getInt("CHILD_ID");
		child = AccountMenu.childs.get(AccountMenu.childPosition);//Child.findChildById(childId);
		
		// getting access to view elements
		viewName = (TextView) findViewById(R.id.childname);
		lastLoc = (TextView) findViewById(R.id.lastUpdateTimestamp);
		activationToggle = (ToggleButton) findViewById(R.id.activationToggleButton);
		childIcon = (QuickContactBadge) findViewById(R.id.childIcon);
		
		activationToggle.setChecked(child.activationFlag);
		viewName.setText(child.name);
		lastLoc.setText(child.name+" was here");
		
		Bitmap icon = Child.loadChildIconBitmap(this, child.name);
		if (icon != null) {
			childIcon.setMode(ContactsContract.QuickContact.MODE_SMALL);
			childIcon.setImageBitmap(icon);
		}
		
		activationToggle.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View arg0) {
	            if(activationToggle.isChecked()){
	            	child.activationFlag = true;
	        		Log.d("toggle chaged",""+child.activationFlag);
	        		
	            }
	            else   
	            	child.activationFlag = false;
	            updateChildsList();
	        }
	    });
		
		childCurrentLocation = (WebView) findViewById(R.id.childCurrentMapLocation);
		routesList = (LinearLayout) findViewById(R.id.routesList);
		Button call = (Button) findViewById(R.id.childCallButton);
		call.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
				callChild();
			}

			
		});
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String value = extras.getString("route");
		    String title = extras.getString("title");
		    if(value.compareTo("") != 0){
		    	String [] points = value.split("/");
		    	Vector<Pair<String, String>> route = new Vector<Pair<String,String>>();
		    	for(int i=0;i<points.length;i++){
		    		String [] start = points[i].split(",");
		    		route.add(new Pair<String, String>(start[0], start[1]));
		    	}
				child.routes.add(new Route(route,title));
				updateChildsList();
				
		    }
		}
		
		// setting routes adapter to routes list
//		RoutesListAdapter adapter = new RoutesListAdapter(this, R.id.routesList, R.layout.route_list_item, child.routes);
//		routesList.setAdapter(adapter);
		for (int i = 0; i < child.routes.size(); i++) {
			Child.Route route = child.routes.get(i);
			routesList.addView(buildRouteView(route));
		}
		
		
	}
	
	private void updateChildsList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				AccountMenu.childs.remove(AccountMenu.childPosition);
				AccountMenu.childs.add(AccountMenu.childPosition, child);
				String tmp = "";
				Gson gson = new Gson();
				for(int i=0;i<AccountMenu.childs.size();i++){
					String json = gson.toJson(AccountMenu.childs.get(i));
					tmp = tmp+"/"+json;
				}
				if(tmp.compareTo("") != 0)
					AccountMenu.prefs.edit().putString("child",tmp).commit();
				Log.d("new childs list",tmp);
			}
		}).start();

	}

	private void displayCurrentLocation(double lattitude, double longitude,boolean b){
		final String centerURL  = "javascript:init(" +lattitude + "," +longitude+ ","+b+")";
		Log.d("url",centerURL);
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
		finish();
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
        
		final LinearLayout routeView = (LinearLayout) inflater.inflate(R.layout.route_list_item, null);
		
		// getting access to route views
		TextView routeName = (TextView) routeView.findViewById(R.id.routeName);
		final WebView routeSteps = (WebView) routeView.findViewById(R.id.routeSteps);
		final LinearLayout routeActions = (LinearLayout) routeView.findViewById(R.id.routeActions);
		
		Button send = (Button) routeActions.findViewById(R.id.routeSendButton);
		Button remove = (Button) routeActions.findViewById(R.id.routeDeleteButton);
		
		remove.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				new AlertDialog.Builder(context)
	            .setTitle("Delete Route")
	            .setIcon(R.drawable.icon)
	            .setMessage("Are you sure you want to delete this route?")
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) { 
	                    // continue with delete
	                	routesList.removeView(routeView);
	    				child.routes.remove(route);
	    				updateChildsList();
	                }
	             })
	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) { 
	                    // do nothing
	                }
	             })
	             .show();
				
				
			}
		});
		
		send.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
	    		String points = route.steps.get(0).first+","+route.steps.get(0).second;
	    		for(int i=1;i<route.steps.size();i++)
	    			points = points+"/"+route.steps.get(i).first+","+route.steps.get(i).second;
				AccountMenu.server.gcmServer("new route:"+route.name+":"+points);
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
		Pair<String, String> start = route.steps.get(0);
		Pair<String, String> end = route.steps.get(route.steps.size()-1);
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

	
	
	
	private void showLoc(double lat,double lng,boolean b){
		child.lat = lat;
		child.lng = lng;
		if(b)
			displayCurrentLocation(child.lat, child.lng,b);
		else
			displayCurrentLocation(child.lat, child.lng,b);
		
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
				if(loc.contains(",")){
		    		String [] msg = loc.split(",");
		   		    double lat = Double.parseDouble(msg[0]);
		  			double longt = Double.parseDouble(msg[1]);
		  			showLoc(lat,longt,true); 
				}else
					showLoc(0,0,false); 
	}
	}
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.child_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.delete_child:
	        	new AlertDialog.Builder(this)
	            .setTitle("Delete Child")
	            .setIcon(R.drawable.icon)
	            .setMessage("Are you sure you want to delete this child?")
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) { 
	                    // continue with delete
	                	saveToFile();
	                	finish();
	                }
	             })
	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) { 
	                    // do nothing
	                }
	             })
	             .show();
	            return true;
	        case R.id.child_help:
	            showHelp();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void saveToFile() {
		AccountMenu.prefs.edit().remove("child").commit();
		String tmp = "";
		Gson gson = new Gson();
		AccountMenu.childs.remove(AccountMenu.childPosition);
		
		for(int i=0;i<AccountMenu.childs.size();i++){ 
			String json = gson.toJson(AccountMenu.childs.get(i));
			tmp = tmp+"/"+json;
    	}
		Log.d("saved after remove is ",tmp);
		if(tmp.compareTo("") != 0)
			AccountMenu.prefs.edit().putString("child",tmp).commit();
	}
	
	private void showHelp() {
		finish();
		Intent i = new Intent(this,TutorialActivity.class);
		startActivity(i);
	}
	
	
}
