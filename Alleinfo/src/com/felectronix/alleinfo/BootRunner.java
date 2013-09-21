package com.felectronix.alleinfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BootRunner extends Activity {

	String pin;
	SharedPreferences SP;
	SecurePreferences secPref;
	private String number;
	Context c;
	DialogCodes returnCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SP = this.getPreferences(MODE_PRIVATE);
		c = this;
		returnCode = DialogCodes.noError;
		if (SP.getBoolean("RequirePIN", false)) {
			InputPinDialog();
		} else {
			requestPersNumString();
		}
	}

	public void requestPersNumString() {
		final Dialog dialog = new Dialog(c);
		dialog.setContentView(R.layout.requestnum);
		dialog.setTitle("Ange personnummer...");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		Button gotoPin = (Button) dialog.findViewById(R.id.quickPIN);
		if (SP.getBoolean("RequirePIN", false)) {
			gotoPin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
						InputPinDialog();
						dialog.dismiss();
				}

			});
		} else {
			gotoPin.setVisibility(View.INVISIBLE);
		}
		Button cont = (Button) dialog.findViewById(R.id.button_ok);
		cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText inBox = (EditText) dialog.findViewById(R.id.inBox);
				String persnum = inBox.getText().toString();
				if (persnum.length() == 10 || persnum.length() == 12) {
					if (persnum.length() == 12) {
						persnum = persnum.substring(2);
					}
					persnum = persnum.substring(0, 6) + "-"
							+ persnum.substring(6);
					number = persnum;
					CheckBox cb = (CheckBox) dialog.findViewById(R.id.saveCred);
					if (cb.isChecked()) {
						SaveCredDialog();
					} else {
						handleResponse();
					}
					dialog.dismiss();
				} else {
					Toast.makeText(c, "Felaktigt format", Toast.LENGTH_LONG)
							.show();
					return;
				}
			}
		});
		Button abor = (Button) dialog.findViewById(R.id.button_abort);
		abor.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (number == null) {
					returnCode = DialogCodes.abort;
				}
				handleResponse();
				dialog.dismiss();
			}
		});

		dialog.show();
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

	}

	public void SaveCredDialog() {
		final Dialog dialog = new Dialog(c);
		dialog.setContentView(R.layout.requestpin);
		dialog.setTitle("Ange pin...");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		Button cont = (Button) dialog.findViewById(R.id.button_ok1);
		cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				EditText pinBox = (EditText) dialog.findViewById(R.id.pinBox);
				pin = pinBox.getText().toString();
				if (pin.length() >= 4) {
					String tester = "abcd1234";
					try {
						secPref = new SecurePreferences(c, "Creds", pin, true);
						secPref.clear();
						secPref.put("persNum", number);
						secPref.put("compar", tester);
						SP.edit().putBoolean("RequirePIN", true).commit();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(c, "Något gick fel", Toast.LENGTH_SHORT)
								.show();
						returnCode = DialogCodes.noError;
						handleResponse();
						dialog.dismiss();
						return;
					}
					returnCode = DialogCodes.noError;
					handleResponse();
					dialog.dismiss();
				} else {
					Toast.makeText(c, "PIN-koden måste minst vara på 4 tecken",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		Button abor = (Button) dialog.findViewById(R.id.button_abort1);
		abor.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(c, "Personnumret sparades inte",
						Toast.LENGTH_LONG).show();
				returnCode = DialogCodes.noError;
				handleResponse();
				dialog.dismiss();
			}
		});

		dialog.show();
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public void InputPinDialog() {
		final Dialog dialog = new Dialog(c);
		dialog.setContentView(R.layout.requestpintodecrypt);
		dialog.setTitle("Ange pin...");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		Button cont = (Button) dialog.findViewById(R.id.button_ok2);
		cont.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText pinBox = (EditText) dialog.findViewById(R.id.pinBox1);
				pin = pinBox.getText().toString();
				if (pin.length() >= 4) {
					String persona = null;
					try {
						secPref = new SecurePreferences(c, "Creds", pin, true);
						String tester = secPref.getString("compar");
						if (tester == null || tester.length() == 0) {
							Toast.makeText(c, "Lösenordet stämmer inte",
									Toast.LENGTH_SHORT).show();
							return;
						}

						if (tester.equals("abcd1234")) {
							persona = secPref.getString("persNum");
						} else {
							Toast.makeText(c, "Lösenordet stämmer inte",
									Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(c, "Något gick fel", Toast.LENGTH_SHORT).show();
						returnCode = DialogCodes.Error;
						handleResponse();
						dialog.dismiss();
						return;
					}
					number = persona;
					returnCode = DialogCodes.noError;
					handleResponse();
					dialog.dismiss();
				} else {
					Toast.makeText(c, "PIN-koden måste minst vara på 4 tecken",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		Button chngUsr = (Button) dialog.findViewById(R.id.button_otherUser);
		chngUsr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				requestPersNumString();
				dialog.dismiss();
			}			
		});
		Button abor = (Button) dialog.findViewById(R.id.button_abort2);
		abor.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				returnCode = DialogCodes.exit;
				handleResponse();
				dialog.dismiss();
			}
		});

		dialog.show();
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	public void handleResponse() {
		switch(returnCode)
		{
		case abort:
		case exit:
		case Error:
			finish();
			break;
		case noError:
			startApp();
			break;
		default:
			Toast.makeText(getApplicationContext(), "Något gick fel", Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
	}
	
	public void startApp()
	{
		Intent intent = new Intent("com.felectronix.alleinfo.HOME");  
		intent.putExtra("number", number);
		startActivity(intent);
		finish();
	}

}
