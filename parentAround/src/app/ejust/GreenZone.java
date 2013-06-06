package app.ejust;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GreenZone extends Activity{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greenz);
        Button button = (Button) findViewById(R.id.button1);
	    button.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
	    		String inp =  ((EditText) findViewById(R.id.editText1)).getText().toString();
	    		HomeActivity.server.gcmServer("greenz"+inp);
	    		map();
	    	
	    	}
	    });
    }

    private void map(){
    	Intent i = new Intent(this, MapActivity.class);
	    startActivity(i);
    }
}
