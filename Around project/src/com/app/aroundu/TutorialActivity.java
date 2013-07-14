package com.app.aroundu;


import android.app.Activity;
import android.os.Bundle;

public class TutorialActivity extends Activity {
	
	private String type;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = WizardActivity.prefs.getString("user", "");
		if(type.equals("parent"))
			setContentView(R.layout.tutorial_parent);
		else
			setContentView(R.layout.tutorial_child);
		
		
	}
	
	
}
