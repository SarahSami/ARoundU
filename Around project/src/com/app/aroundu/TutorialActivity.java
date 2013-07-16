package com.app.aroundu;


import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class TutorialActivity extends Activity {
	
	private String type;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("user", "");
		if(type.equals("parent"))
			setContentView(R.layout.tutorial_parent);
		else
			setContentView(R.layout.tutorial_child);
		
		
	}
	
	
}
