package com.alleit.alleinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alleit.Alleinfo_Android.R;

public class BootRunner extends Activity {

	String pin;
	SharedPreferences SP;
	String MY_PREFS = "";
	private String number;
	Context c;
	Boolean shouldExtendScheduleTime = false;
	Boolean Stored = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SP = getApplicationContext().getSharedPreferences(
				PreferenceInfo.Preference_Name, MODE_PRIVATE);

		int currentVersion = SP.getInt(
				PreferenceInfo.Current_Version_Code_Name, -1);
		number = SP.getString(PreferenceInfo.Pers_Num_Name, "");

		if (currentVersion != PreferenceInfo.currentVersionCode) {
			if (!number.equals("")) {

				SP.edit().clear().apply();

				number = "";

				AlertDialog.Builder deleted_app_data_notice = new AlertDialog.Builder(
						this);

				deleted_app_data_notice.setMessage(this
						.getString(R.string.dataEraseNote));
				deleted_app_data_notice.setTitle("Ny uppdatering");
				deleted_app_data_notice.setPositiveButton(
						this.getString(android.R.string.ok), null);
				deleted_app_data_notice.setCancelable(false);
				deleted_app_data_notice.create().show();
			}
		}

		number = SP.getString(PreferenceInfo.Pers_Num_Name, "");
		shouldExtendScheduleTime = SP.getBoolean(
				PreferenceInfo.Extended_Schedule_Display_Name, false);

		c = this;
		if (number.equals("")) {
			setContentView(R.layout.requestnum);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				ColorDrawable statusBarColor = new ColorDrawable(getResources().getColor(R.color.col_login));
				float[] hsv = new float[3];
				Color.colorToHSV(statusBarColor.getColor(), hsv);
				hsv[2] *= .8;
				getWindow().setStatusBarColor(Color.HSVToColor(hsv));
			}
			requestPersNumString();
		} else
			startApp();

	}

	public void requestPersNumString() {

		// checkbox rig/niu
		final CheckBox RIGbox = (CheckBox) findViewById(R.id.saveSport);
		((CheckBox) (findViewById(R.id.saveCreds)))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							AlertDialog.Builder PULNotice = new AlertDialog.Builder(
									c);

							PULNotice.setMessage(c.getString(R.string.PULText));
							PULNotice.setTitle("Hantering av personuppgifter");
							PULNotice.setPositiveButton("OK", null);
							PULNotice.setCancelable(false);
							PULNotice.create().show();
						}
					}

				});

		final Button teach = (Button) findViewById(R.id.teacher);
		teach.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText inBox = (EditText) findViewById(R.id.numbox);
				TextView prompt = (TextView) findViewById(R.id.prompttext);

				inBox.setText("");

				if (teach.getText() != "Elev") {
					inBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
					teach.setText("Elev");
					RIGbox.setVisibility(View.INVISIBLE);
					prompt.setText(Html.fromHtml("Ange L&auml;rarID (\"BcA\")"));
				} else {

					inBox.setInputType(2);
					teach.setText(Html.fromHtml("L&auml;rare"));
					RIGbox.setVisibility(View.VISIBLE);
					prompt.setText(R.string.angeDittPers);
				}
			}

		});

		Button cont = (Button) findViewById(R.id.submit);
		cont.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				EditText inBox = (EditText) findViewById(R.id.numbox);
				String persnum = inBox.getText().toString();

				// If user is a teacher
				if (inBox.getInputType() != 2 && persnum.length() >= 3) {
					CheckBox saveBox = (CheckBox) findViewById(R.id.saveCreds);
					shouldExtendScheduleTime = true;
					number = persnum;

					if (saveBox.isChecked()) {
						SP.edit()
								.putString(PreferenceInfo.Pers_Num_Name, number)
								.commit();
						SP.edit()
								.putBoolean(
										PreferenceInfo.Extended_Schedule_Display_Name,
										shouldExtendScheduleTime).commit();
						SP.edit()
								.putInt(PreferenceInfo.Current_Version_Code_Name,
										PreferenceInfo.currentVersionCode)
								.commit();

						Stored = true;
					} else {
						startApp();
					}

					// else if user is a student
				} else if (persnum.length() == 10 || persnum.length() == 12) {
					if (persnum.length() == 12) {
						persnum = persnum.substring(2);
					}
					persnum = persnum.substring(0, 6) + "-"
							+ persnum.substring(6);
					number = persnum;
					CheckBox saveBox = (CheckBox) findViewById(R.id.saveCreds);

					// if user wish to save their login information
					if (saveBox.isChecked()) {
						shouldExtendScheduleTime = RIGbox.isChecked();

						SP.edit()
								.putString(PreferenceInfo.Pers_Num_Name, number)
								.commit();
						SP.edit()
								.putBoolean(
										PreferenceInfo.Extended_Schedule_Display_Name,
										shouldExtendScheduleTime).commit();
						SP.edit()
								.putInt(PreferenceInfo.Current_Version_Code_Name,
										PreferenceInfo.currentVersionCode)
								.commit();

						Stored = true;

						startApp();
					} else {
						startApp();
					}

				} else {
					// if the input has the wrong format
					Toast.makeText(c, "Felaktigt format", Toast.LENGTH_LONG)
							.show();
					return;
				}
			}
		});

	}

	// start the application
	public void startApp() {
		Intent intent = new Intent("com.alleit.alleinfo.HOME");
		intent.putExtra(PreferenceInfo.Pers_Num_Name, number);
		intent.putExtra(PreferenceInfo.Extended_Schedule_Display_Name,
				shouldExtendScheduleTime);
		intent.putExtra("Stored", Stored);
		startActivity(intent);
		finish();
	}

}
