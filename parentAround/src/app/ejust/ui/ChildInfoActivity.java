package app.ejust.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import app.ejust.R;
import app.ejust.model.Child;

public class ChildInfoActivity extends Activity {
	
	WebView childCurrentLocation;
	Child child;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.child);
		
		// get child id from bundle object
		child = Child.findChildById(0);
		
		childCurrentLocation = (WebView) findViewById(R.id.childCurrentMapLocation);
		
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
	
	public void addNewRoute(View view){
		
	}
}
