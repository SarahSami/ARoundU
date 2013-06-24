package com.app;

import java.io.IOException;

import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends Activity {

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add);
		Button ok = (Button) findViewById(R.id.done);
		ok.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				String mail = ((TextView) findViewById(R.id.mail)).getText()
						.toString();
				String f = ((TextView) findViewById(R.id.fname)).getText()
						.toString();
				String number = ((TextView) findViewById(R.id.number)).getText()
						.toString();
				try {
					register(mail, f, number);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

	private void register(String mail, String fname, String num) throws IOException {
		if(fname.compareTo("") == 0)
			Toast.makeText(this, "Add first name", Toast.LENGTH_LONG).show();
		if(mail.compareTo("") == 0)
			Toast.makeText(this, "Add google mail", Toast.LENGTH_LONG).show();
		else{
			if(mail.compareTo("")!=0 && fname.compareTo("")!=0){
				AccountMenu.server.postMsg("/get_id_first_register?account="+mail);
				Child c = new Child();
				c.mail = mail;
				c.name = fname;
				c.number = num;
				saveChild(c);
//				String FILENAME = "users";
//				FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
//				OutputStreamWriter isw = new OutputStreamWriter(fos);
//				BufferedWriter buffw = new BufferedWriter(isw);
//				buffw.write(mail + "," + fname + "," + num);
//				buffw.write("/");
//				buffw.close();
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
