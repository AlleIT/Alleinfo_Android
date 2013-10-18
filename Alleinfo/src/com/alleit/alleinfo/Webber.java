package com.alleit.alleinfo;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.Html;

public class Webber {

	// Web URLs used in-app

	// preferences file
	public static final String MY_PREFS = "MyPreferencesFile";

	// web url for the food menu
	public static String foodAddress = "http://mpi.mashie.se/mashie/MashiePublic/MenuPresentation/Common/MenuSite.aspx?Siteid=4c2901c9-61f3-4b38-a30c-a02f00dc7f9b";

	// Url to it's learning
	public static String itslearningAddress = "https://falkoping.itslearning.com/elogin/default.aspx";

	// Url to Dexter
	public static String dexterAddress = "https://m11-mg-local.falnet.falkoping.se/mg-local/index.html";
	
	// render schedule
	public static String renderSchedule(String number, int specday,
			Boolean showThisWeek, Point screenSize, Context c) {
		Calendar cal = Calendar.getInstance();

		int day = 0;
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		day = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (showThisWeek) {

			/*
			 * check if user have a sport
			 */
			SharedPreferences prefs = c.getSharedPreferences(
					"com.felectronix.alleinfo", 0);

			if (prefs.getBoolean("playSports", false)) {
				
				if (cal.get(Calendar.HOUR_OF_DAY) >= 20 && specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					day++;
				}
			} else {

				if (cal.get(Calendar.HOUR_OF_DAY) >= 16 && specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					day++;
				}
			}

			if (specday != -1) {
				if (specday == day && cal.get(Calendar.HOUR_OF_DAY) >= 16
						&& cal.get(Calendar.DAY_OF_WEEK) == day) {
					week++;
					if (week > 52)
						week = 1;
				}
				day = specday;
			}
		} else {
			week++;
			if (week > 52)
				week = 1;

			if (specday != -1) {
				day = specday;
			}
		}

		switch (day) {
		case 0:
		case 1:
		case 2:
			break;

		case 3:
			day = 4;
			break;

		case 4:
			day = 8;
			break;

		case 5:
			day = 16;
			break;

		default:
			day = 0;
			break;
		}

		// web url for schedule
		String schemesearch = "http://www.novasoftware.se/ImgGen/schedulegenerator.aspx?format=png&schoolid=20700/sv-se&type=1&id="
				+ number
				+ "&period=&week="
				+ String.valueOf(week)
				+ "&mode=0&printer=0&colors=64&head=0&clock=0&foot=0&day="
				+ String.valueOf(day)
				+ "&width="
				+ String.valueOf((int) (screenSize.x * 0.65))
				+ "&height="
				+ String.valueOf(screenSize.y)
				+ "&maxwidth="
				+ String.valueOf((int) (screenSize.x * 0.65))
				+ "&maxheight="
				+ String.valueOf(screenSize.y);

		// return value
		return schemesearch;
	}

	// get tiny news feed
	public static NewsInfo[] getTinyNewsFeed() {
		NewsInfo[] feed;

		// for each new feed update
		NewsInfo data = null;
		feed = new NewsInfo[3];
		for (int i = 0; i < 2; i++) {
			data = new NewsInfo();
			data.description = "En kort beskrivning av eventet.";
			data.type = "EVENT";
			data.handler = String.valueOf(Html.fromHtml("ELEVK&Aring;REN"));
			data.contentType = 0;
			data.uniqeIdentifier = null;
			feed[i] = data;
		}

		// news info, show more news
		data = new NewsInfo();
		data.description = "Fler nyheter...";
		data.type = null;
		data.handler = null;
		data.contentType = 1;
		data.uniqeIdentifier = null;
		feed[2] = data;

		// return value
		return feed;
	}
}
