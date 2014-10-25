package com.alleit.alleinfo;

import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.alleit.Alleinfo_Android.R;

public class NewsFeedAdapter extends ArrayAdapter<NewsData> {
	private Activity c;
	private NewsData[] datas;

	public NewsFeedAdapter(Activity context, int textViewResourceId,
			NewsData[] objects) {
		super(context, textViewResourceId, objects);
		c = context;
		datas = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = c.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.newsroll, parent, false);
		
		TextView desc = (TextView) rowView.findViewById(R.id.description);
		desc.setText(datas[position].headline);
		desc.setSelected(true);

		TextView type = (TextView) rowView.findViewById(R.id.type);
		type.setText(datas[position].type.toUpperCase(Locale.ENGLISH));

		TextView handler = (TextView) rowView.findViewById(R.id.handler);
		handler.setText(datas[position].handler.toUpperCase(Locale.ENGLISH));
		
		handler.setTextColor(Color.parseColor(datas[position].color));

		return rowView;
	}
}