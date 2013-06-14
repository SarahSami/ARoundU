package app.model;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import app.aroundu.R;
import app.child.ChildActivity;
import app.ui.ChildInfoActivity;

public class WizardActivity extends Activity {

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String first = prefs.getString("user", "");
		
		if (first.equals("")) {
			Log.d("first status","in first");
			setContentView(R.layout.wizard);
			final RadioButton child = (RadioButton) findViewById(R.id.child);
			final RadioButton parent = (RadioButton) findViewById(R.id.parent);
			Button ok = (Button) findViewById(R.id.ok);

			ok.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View arg0) {
					if (child.isChecked()) {
						prefs.edit().putString("user","child").commit();
					} else if (parent.isChecked()) {
						prefs.edit().putString("user","parent").commit();
					}
					launch();
				}
			});

		}else
			launch();
		
		

	}

	private void launch() {
		String user = prefs.getString("user","");
		Log.d("status user ::: ",user);
		Intent i = null;
		
		if (user.equals("child")) {
			i = new Intent(this, ChildActivity.class);

		} else if (user.equals("parent")) {
			i = new Intent(this, ChildInfoActivity.class);
		}
		finish();
		startActivity(i);
		
	}

}
