package app.ui;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import app.aroundu.R;

public class Adapter extends BaseAdapter {
	 
    private Activity activity;
    private static LayoutInflater inflater=null;
    private String []users;
    public List<Integer> checks;
    
    private View vi;
    public Adapter(Activity a, String []usr) {
        activity = a;
        users = usr;
        checks = new LinkedList<Integer>();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return users.length;
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);
 
        TextView cnt = (TextView)vi.findViewById(R.id.contact); 
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); 

        CheckBox ch  = (CheckBox)vi.findViewById(R.id.checkBox1);
    	ch.setVisibility(View.VISIBLE);
    	final int pos = position;
    	ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            	if(isChecked)
            		checks.add(pos);
            	else
            		checks.remove((Object)pos);
            }
          });
    
        cnt.setText(users[position]);
        thumb_image.setImageResource(R.drawable.contact);
        return vi;
    }
}
