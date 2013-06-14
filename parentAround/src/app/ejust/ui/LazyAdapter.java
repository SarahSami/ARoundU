package app.ejust.ui;


 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import app.ejust.R;
import app.ejust.R.drawable;
import app.ejust.R.id;
import app.ejust.R.layout;
 
public class LazyAdapter extends BaseAdapter {
 
    private Activity activity;
    private static LayoutInflater inflater=null;
    private String []users;
    private View vi;
    public LazyAdapter(Activity a, String []usr) {
        activity = a;
        users = usr;
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

        cnt.setText(users[position]);
        thumb_image.setImageResource(R.drawable.contact);
        return vi;
    }
   
}


