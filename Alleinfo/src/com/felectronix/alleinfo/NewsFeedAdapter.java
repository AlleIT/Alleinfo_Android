package com.felectronix.alleinfo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/* news feed
*
*	Visa senaste nyheterna från elevkaren
*
*/

public class NewsFeedAdapter extends ArrayAdapter<NewsInfo> {
	private Activity c;
	private NewsInfo[] datas;

	public NewsFeedAdapter(Activity context, int textViewResourceId,
			NewsInfo[] objects) {
		super(context, textViewResourceId, objects);
		c = context;
		datas = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = c.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.elevkar_previewrow, null);

		TextView desc = (TextView) rowView.findViewById(R.id.description);
		desc.setText(datas[position].description);

		TextView type = (TextView) rowView.findViewById(R.id.type);
		type.setText(datas[position].type);

		TextView handler = (TextView) rowView.findViewById(R.id.handler);
		handler.setText(datas[position].handler);

		return rowView;
	}
}