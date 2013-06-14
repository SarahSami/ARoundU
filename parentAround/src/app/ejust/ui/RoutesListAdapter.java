package app.ejust.ui;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.ejust.R;
import app.ejust.model.Child;
import app.ejust.model.Child.Route;

public class RoutesListAdapter extends ArrayAdapter<Child.Route> {


	public RoutesListAdapter(Context context, int resource,
			int textViewResourceId, List<Route> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return setViewValue(inflater.inflate(R.layout.route_list_item, null), position);
	}
	
	private View setViewValue(View view, int position) {
		Child.Route route = getItem(position);
		LinearLayout listItem = (LinearLayout) view;
		
		// getting access to route views
		TextView routeName = (TextView) listItem.findViewById(R.id.routeName);
		WebView routeSteps = (WebView) listItem.findViewById(R.id.routeSteps);
		// setting route actions (send, edit and delete) and routeSteps viewer to be invisible
		// it will be displayed only when user hits the name of the route
		listItem.findViewById(R.id.routeActions).setVisibility(View.GONE);
		routeSteps.setVisibility(View.GONE);
		
		// display only view name
		routeName.setText(route.name);
		
		Log.d("ROUTE", ""+position+":"+route.name);
		return listItem;
	}

}
