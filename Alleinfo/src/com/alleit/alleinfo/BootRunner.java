package com.alleit.alleinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BootRunner extends Activity {

	String pin;
	SharedPreferences SP;
	String MY_PREFS = "";
	SecurePreferences secPref;
	private String number;
	Context c;
	DialogCodes returnCode;
	Boolean isPlayingSport = false;
	ViewGroup viewGroup;
	Boolean Stored = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		viewGroup = (ViewGroup) findViewById(R.id.content);
		SP = this.getPreferences(MODE_PRIVATE);

		isPlayingSport = SP.getBoolean("playSports", false);

		c = this;
		returnCode = DialogCodes.noError;
		if (SP.getBoolean("RequirePIN", false)) {
			// asks the user to write his/her pin code to get access to the
			// application
			InputPinDialog();
		} else {
			// Request personal number on start of the application
			requestPersNumString();
		}

	}

	public void requestPersNumString() {
		viewGroup.removeAllViews();
		viewGroup.addView(View.inflate(c, R.layout.requestnum, null));

		// checkbox rig/niu
		final CheckBox SS_CHECKBOX = (CheckBox) findViewById(R.id.saveSport);

		final Button teach = (Button) findViewById(R.id.teacher);
		teach.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText inBox = (EditText) findViewById(R.id.numbox);
				TextView prompt = (TextView) findViewById(R.id.prompttext);
				if (teach.getText() != "Elev") {
					inBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
					teach.setText("Elev");
					SS_CHECKBOX.setVisibility(View.INVISIBLE);
					prompt.setText(Html.fromHtml("Ange L&auml;rarID (\"BcA\")"));
				} else {
					// checked with System.out.println, consider changing to
					// InputType.XXXXX
					inBox.setInputType(2);
					teach.setText(Html.fromHtml("L&auml;rare"));
					SS_CHECKBOX.setVisibility(View.VISIBLE);
					prompt.setText(R.string.angeDittPers);
				}
			}

		});

		Button gotoPin = (Button) findViewById(R.id.angepin);
		// check if pin code is saved
		if (SP.getBoolean("RequirePIN", false)) {

			gotoPin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					InputPinDialog();

				}

			});
		} else {
			gotoPin.setVisibility(View.INVISIBLE);
		}

		// check if the personal number has the right length

		Button cont = (Button) findViewById(R.id.submit);
		cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText inBox = (EditText) findViewById(R.id.numbox);
				String persnum = inBox.getText().toString();
				if (inBox.getInputType() != 2) {
					CheckBox cb = (CheckBox) findViewById(R.id.saveCreds);
					isPlayingSport = true;
					number = persnum;

					if (cb.isChecked()) {
						// if user clicks the "save button", save it.
						SaveCredDialog();
					} else {
						handleResponse();
					}
				} else if (persnum.length() == 10 || persnum.length() == 12) {
					if (persnum.length() == 12) {
						persnum = persnum.substring(2);
					}
					persnum = persnum.substring(0, 6) + "-"
							+ persnum.substring(6);
					number = persnum;
					CheckBox cb = (CheckBox) findViewById(R.id.saveCreds);

					if (cb.isChecked()) {
						isPlayingSport = SS_CHECKBOX.isChecked();
						// if user clicks the "save button", save it.
						SaveCredDialog();
					} else {
						handleResponse();
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

	// save cred
	public void SaveCredDialog() {
		viewGroup.removeAllViews();
		viewGroup.addView(View.inflate(c, R.layout.createpin, null));

		Button cont = (Button) findViewById(R.id.submit);
		cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				EditText pinBox = (EditText) findViewById(R.id.numbox);
				pin = pinBox.getText().toString();
				if (pin.length() >= 4) {
					String tester = "abcd1234";
					try {
						secPref = new SecurePreferences(c, "Creds", pin, true);
						secPref.clear();
						secPref.put("persNum", number);
						secPref.put("compar", tester);
						SP.edit().putBoolean("RequirePIN", true).commit();
						SP.edit().putBoolean("playSports", isPlayingSport).commit();
						Stored = true;

					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(c, "N&aring;got gick fel",
								Toast.LENGTH_SHORT).show();

						returnCode = DialogCodes.noError;
						handleResponse();
						return;
					}
					returnCode = DialogCodes.noError;
					handleResponse();
				} else {
					Toast.makeText(
							c,
							"PIN-koden m&aring;ste minst vara p&aring; 4 tecken",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

		});

		// if the user abort the saving operation

		Button abor = (Button) findViewById(R.id.abort);
		abor.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(c, "Personnumret sparades inte",
						Toast.LENGTH_LONG).show();
				returnCode = DialogCodes.noError;
				handleResponse();
			}
		});

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	// input pin dialog
	public void InputPinDialog() {
		viewGroup.removeAllViews();
		viewGroup.addView(View.inflate(c, R.layout.requestpin, null));

		Button cont = (Button) findViewById(R.id.submit);
		cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				System.out.println("so far so good...");
				EditText pinBox = (EditText) findViewById(R.id.numbox);
				pin = pinBox.getText().toString();
				if (pin.length() >= 4) {
					String persona = null;

					secPref = new SecurePreferences(c, "Creds", pin, true);
					String tester = secPref.getString("compar");
					if (tester == null || tester.length() == 0) {
						Toast.makeText(
								c,
								Html.fromHtml("L&ouml;senordet st&auml;mmer inte"),
								Toast.LENGTH_SHORT).show();
						return;
					}

					if (tester.equals("abcd1234")) {
						persona = secPref.getString("persNum");
					} else {
						Toast.makeText(
								c,
								Html.fromHtml("L&ouml;senordet st&auml;mmer inte"),
								Toast.LENGTH_SHORT).show();
						return;
					}
					number = persona;
					Stored = true;
					returnCode = DialogCodes.noError;
					handleResponse();
				} else {
					Toast.makeText(
							c,
							Html.fromHtml("PIN-koden m&aring;ste minst vara p&aring; 4 tecken"),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});

		// change user

		Button chngUsr = (Button) findViewById(R.id.chuser);
		chngUsr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				requestPersNumString();
			}
		});
	}

	// prepare for launch

	public void handleResponse() {
		switch (returnCode) {
		case abort:
		case exit:
		case Error:
			finish();
			break;
		case noError:
			startApp();
			break;
		default:
			Toast.makeText(getApplicationContext(),
					Html.fromHtml("N&aring;got gick fel"), Toast.LENGTH_SHORT)
					.show();
			finish();
			break;
		}
	}

	// start the application
	public void startApp() {
		Intent intent = new Intent("com.alleit.alleinfo.HOME");
		intent.putExtra("number", number);
		intent.putExtra("playSport", isPlayingSport);
		intent.putExtra("Stored", Stored);
		startActivity(intent);
		finish();
	}

}
