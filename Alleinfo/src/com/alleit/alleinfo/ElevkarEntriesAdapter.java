package com.alleit.alleinfo;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alleit.Alleinfo_Android.R;

public class ElevkarEntriesAdapter extends ArrayAdapter<PosterData> {
	private Activity c;
	private PosterData[] datas;

	public ElevkarEntriesAdapter(Activity context, int textViewResourceId,
			PosterData[] objects) {
		super(context, textViewResourceId, objects);
		c = context;
		datas = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = c.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.elevkar_entry, parent, false);
		
		TextView name = (TextView) rowView.findViewById(R.id.KarName);
		name.setText(datas[position].name);
		
		ImageView logo = (ImageView) rowView.findViewById(R.id.KarLogo);
		logo.setImageDrawable(datas[position].logo);

		LinearLayout bg = (LinearLayout) rowView.findViewById(R.id.KarBackground);
		bg.setBackgroundColor(Color.parseColor(datas[position].color));
		return rowView;
	}
}