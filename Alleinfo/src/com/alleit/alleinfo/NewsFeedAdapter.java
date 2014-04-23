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
import com.alleit.Alleinfo_Android.R;

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
		desc.setText(Html.fromHtml(datas[position].headline));

		TextView type = (TextView) rowView.findViewById(R.id.type);
		type.setText(Html.fromHtml(datas[position].type).toString().toUpperCase(Locale.ENGLISH));

		TextView handler = (TextView) rowView.findViewById(R.id.handler);
		handler.setText(Html.fromHtml(datas[position].handler).toString().toUpperCase(Locale.ENGLISH));

		if (datas[position].handler != null) {
			checkColor(handler, position);
		} else {
			handler.setTextColor(Color.BLACK);
		}

		return rowView;
	}

	private void checkColor(TextView HandTXT, int position) {
		
		String handler = String.valueOf(Html.fromHtml(datas[position].handler)).toUpperCase(Locale.ENGLISH);
				
		if (handler.contains(Html.fromHtml(Posterlist.Ename)
				.toString().toUpperCase(Locale.ENGLISH))) {
			HandTXT.setTextColor(Color.parseColor(Posterlist.Ecolor));
		} else if (handler.contains(Html
				.fromHtml(Posterlist.PRname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			HandTXT.setTextColor(Color.parseColor(Posterlist.PRcolor));

		} else if (handler.contains(Html
				.fromHtml(Posterlist.Fname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			HandTXT.setTextColor(Color.parseColor(Posterlist.Fcolor));

		} else if (handler.contains(Html
				.fromHtml(Posterlist.spexname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			HandTXT.setTextColor(Color.parseColor(Posterlist.spexcolor));

		} else if (handler.contains(Html
				.fromHtml(Posterlist.IFname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			HandTXT.setTextColor(Color.parseColor(Posterlist.IFcolor));

		} else if (handler.contains(Html
				.fromHtml(Posterlist.ITname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			HandTXT.setTextColor(Color.parseColor(Posterlist.ITcolor));

		} else if (handler.contains(Html
				.fromHtml(Posterlist.Skolaname).toString()
				.toUpperCase(Locale.ENGLISH))) {
			HandTXT.setTextColor(Color.parseColor(Posterlist.Skolacolor));

		} else {
			HandTXT.setTextColor(Color.BLACK);
		}
	}
}