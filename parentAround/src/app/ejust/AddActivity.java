package app.ejust;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
				String l = ((TextView) findViewById(R.id.lname)).getText()
						.toString();
				try {
					register(mail, f, l);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

	private void register(String mail, String fname, String lname)	
			throws IOException {
		if(fname.compareTo("") == 0)
			Toast.makeText(this, "Add first name", Toast.LENGTH_LONG).show();
		if(mail.compareTo("") == 0)
			Toast.makeText(this, "Add google mail", Toast.LENGTH_LONG).show();
		else{
			if(mail.compareTo("")!=0 && fname.compareTo("")!=0){
				String FILENAME = "users";
				FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
				OutputStreamWriter isw = new OutputStreamWriter(fos);
				BufferedWriter buffw = new BufferedWriter(isw);
				buffw.write(mail + "," + fname + "," + lname);
				buffw.write("/");
				buffw.close();
			}
			finish();
		}
	}
}
