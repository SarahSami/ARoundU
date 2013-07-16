package com.app.aroundu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class DialogActivity extends Activity{
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String rid = getIntent().getExtras().getString("id");
		String ac = getIntent().getExtras().getString("account");
		
		Builder d = new AlertDialog.Builder(this);
		d.setIcon(R.drawable.icon).setTitle("ARoundU");
		d.setMessage(ac+" wants to add you to his contact list");
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		d.setNeutralButton("Accept", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
			if (!prefs.contains("id"))
				prefs.edit().putString("id", "").commit();

			String rt = prefs.getString("id", "");
			if (!rt.contains(rid))
				prefs.edit().putString("id", rt + "/" + rid).commit();
			
			ChildActivity.server.gcmServer("ACCEPT");
			finish();
		}
		});
		d.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
			ChildActivity.server.gcmServer("DECLINE");
			finish();
			
		};
		});
		d.create();
		d.show();
		
		
	
	}
        
	

}
