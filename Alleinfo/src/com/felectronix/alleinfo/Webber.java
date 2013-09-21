package com.felectronix.alleinfo;

import java.util.Calendar;

import android.graphics.Point;

public class Webber {

	public String foodAddress = "http://mpi.mashie.se/mashie/MashiePublic/MenuPresentation/Common/MenuSite.aspx?Siteid=4c2901c9-61f3-4b38-a30c-a02f00dc7f9b";

	public String renderSchedule(String number, int specday, Boolean specweek,
			Point screenSize) {
		Calendar cal = Calendar.getInstance();
		int day = 0;
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		day = cal.get(Calendar.DAY_OF_WEEK) - 1;

		if (cal.get(Calendar.HOUR_OF_DAY) >= 16 && specday == -1
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			day++;
		}

		if (specday != -1) {
			if (specday < day || specday == day
					&& cal.get(Calendar.HOUR_OF_DAY) >= 16) {
				week++;
				if (week > 52)
					week = 1;
			}
			day = specday;
		}

		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
				|| specday == -1 && cal.get(Calendar.HOUR_OF_DAY) >= 16
				&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {

			week++;
			if (week > 52)
				week = 1;
		} else {
			if (!specweek && week == cal.get(Calendar.WEEK_OF_YEAR)) {
				week++;
			} else if (specweek && week > cal.get(Calendar.WEEK_OF_YEAR)) {
				week--;
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

		return schemesearch;
	}

	public String getTodaysMeal() {
		String todMeal = null;

		return todMeal;
	}

	public NewsInfo[] getTinyNewsFeed() {
		NewsInfo[] feed;

		NewsInfo data = null;
		feed = new NewsInfo[3];
		for (int i = 0; i < 2; i++) {
			data = new NewsInfo();
			data.description = "En kort beskrivning av eventet.";
			data.type = "EVENT";
			data.handler = "ELEVKÅREN";
			data.contentType = 0;
			data.uniqeIdentifier = null;
			feed[i] = data;
		}
		data = new NewsInfo();
		data.description = "Fler nyheter...";
		data.type = null;
		data.handler = null;
		data.contentType = 1;
		data.uniqeIdentifier = null;
		feed[2] = data;

		return feed;
	}
}
