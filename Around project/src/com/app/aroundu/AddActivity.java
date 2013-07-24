package com.app.aroundu;

import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends Activity {
	
	TextView nameView;
	TextView emailView;
	TextView numberView;
	
	Bitmap childIcon = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);
		
		emailView = ((TextView) findViewById(R.id.mail));
		nameView = ((TextView) findViewById(R.id.fname));
		numberView = ((TextView) findViewById(R.id.number));
		
		Intent i = getIntent();
		emailView.setText(i.getStringExtra("email"));
		nameView.setText(i.getStringExtra("name"));
		numberView.setText(i.getStringExtra("phone"));
		
		if (i.getStringExtra("uri") != null) {
			Uri contactUri = Uri.parse(i.getStringExtra("uri"));
			
			InputStream childIconStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri);
			
			if(childIconStream != null){
				childIcon = BitmapFactory.decodeStream(childIconStream);
				QuickContactBadge b = (QuickContactBadge) findViewById(R.id.contactIcon);
				b.setMode(ContactsContract.QuickContact.MODE_SMALL);
				b.setImageBitmap(childIcon);
			}
		}
	}
	
	public void addChild(View view){
		String mail = emailView.getText().toString();
		String f = nameView.getText().toString();
		String number = numberView.getText().toString();
		
		try {
			register(mail, f, number);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void register(String mail, String fname, String num) throws IOException {
		if(fname.compareTo("") == 0)
			Toast.makeText(this, "Add first name", Toast.LENGTH_LONG).show();
		if(mail.compareTo("") == 0)
			Toast.makeText(this, "Add google mail", Toast.LENGTH_LONG).show();
		else{
			if(mail.compareTo("")!=0 && fname.compareTo("")!=0){
				String acc = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("account","");
				if(acc.compareTo(mail+"@gmail.com") == 0){
					Toast.makeText(this, "Please add child's gmail not your gmail.", Toast.LENGTH_LONG).show();
					
				}else if(acc.compareTo(mail) == 0){
					Toast.makeText(this, "Please add child's gmail not your gmail.", Toast.LENGTH_LONG).show();
				}else{

				if(!mail.contains("@"))
					AccountMenu.server.postMsg("/get_id_first_register?account="+mail+"@gmail.com");
				else
					AccountMenu.server.postMsg("/get_id_first_register?account="+mail);
				Child c = new Child();
				c.mail = mail;
				c.name = fname;
				c.number = num;
				
				// save child icon if exists
				if (childIcon != null) {
					Child.saveChildIcon(this, c.name, childIcon);
				}
				
				saveChild(c);
			}
			}
			finish();
		}
	}

	private void saveChild(Child c) {
		Gson gson = new Gson();
		String json = gson.toJson(c);
		if(AccountMenu.prefs.contains("child")){
			String tmp = AccountMenu.prefs.getString("child", "");
			AccountMenu.prefs.edit().putString("child", tmp+"/"+json).commit();
			
		}
		else
		{
			AccountMenu.prefs.edit().putString("child", json).commit();
		}
	    	
	}
	
}
