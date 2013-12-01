package com.alleit.alleinfo;

import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/* news feed
 *
 *	Visa senaste nyheterna fr�n elevkaren
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
		View rowView = inflater.inflate(R.layout.newsroll, null);

		TextView desc = (TextView) rowView.findViewById(R.id.description);
		desc.setText(datas[position].headline);

		TextView type = (TextView) rowView.findViewById(R.id.type);
		type.setText(datas[position].type);

		TextView handler = (TextView) rowView.findViewById(R.id.handler);
		handler.setText(datas[position].handler);

		if (datas[position].handler != null) {
			checkColor(handler, position);
		} else {
			handler.setTextColor(Color.BLACK);
		}

		return rowView;
	}

	private void checkColor(TextView handler, int position) {
		if (datas[position].handler.contains(Html.fromHtml(Karlista.Ename)
				.toString().toUpperCase(Locale.ENGLISH))) {
			handler.setTextColor(Color.parseColor(Karlista.Ecolor));
		} else if (datas[position].handler.contains(Html
				.fromHtml(Karlista.PRname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			handler.setTextColor(Color.parseColor(Karlista.PRcolor));

		} else if (datas[position].handler.contains(Html
				.fromHtml(Karlista.Fname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			handler.setTextColor(Color.parseColor(Karlista.Fcolor));

		} else if (datas[position].handler.contains(Html
				.fromHtml(Karlista.spexname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			handler.setTextColor(Color.parseColor(Karlista.spexcolor));

		} else if (datas[position].handler.contains(Html
				.fromHtml(Karlista.IFname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			handler.setTextColor(Color.parseColor(Karlista.IFcolor));

		} else if (datas[position].handler.contains(Html
				.fromHtml(Karlista.ITname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			handler.setTextColor(Color.parseColor(Karlista.ITcolor));

		} else {
			handler.setTextColor(Color.BLACK);
		}
	}
}