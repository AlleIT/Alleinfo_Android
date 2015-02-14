package com.alleit.alleinfo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alleit.Alleinfo_Android.R;

public class LeftBarAdapter extends ArrayAdapter<String> {

	public int selectedPos = 0;

	public LeftBarAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		if (position == selectedPos)
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				view.setBackgroundDrawable(new ColorDrawable(super.getContext().getResources().getColor(R.color.selection_overlay)));
			else
				view.setBackground(new ColorDrawable(super.getContext().getResources().getColor(R.color.selection_overlay)));
		else
		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		else
			view.setBackground(new ColorDrawable(Color.TRANSPARENT));


		return view;
	}
}