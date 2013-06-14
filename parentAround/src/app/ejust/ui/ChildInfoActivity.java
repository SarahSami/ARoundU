package app.ejust.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import app.ejust.R;
import app.ejust.model.Child;
import app.ejust.model.Child.Route;

public class ChildInfoActivity extends Activity implements OnItemClickListener{
	
	// view elements
	WebView childCurrentLocation;
	LinearLayout routesList;
	
	Child child;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.child);
		
		// get child id from bundle object
		//int childId = getIntent().getExtras().getInt("CHILD_ID");
		int childId = 0;
		child = Child.findChildById(childId);
		
		// getting access to view elements
		childCurrentLocation = (WebView) findViewById(R.id.childCurrentMapLocation);
		routesList = (LinearLayout) findViewById(R.id.routesList);
		
		// setting routes adapter to routes list
//		RoutesListAdapter adapter = new RoutesListAdapter(this, R.id.routesList, R.layout.route_list_item, child.routes);
//		routesList.setAdapter(adapter);
		for (int i = 0; i < child.routes.size(); i++) {
			Child.Route route = child.routes.get(i);
			routesList.addView(buildRouteView(route));
		}
		
		// load child information into view
		displayCurrentLocation(child.lat, child.lng);
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
}
