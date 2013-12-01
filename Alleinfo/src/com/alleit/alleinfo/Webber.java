package com.alleit.alleinfo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Point;
import android.text.Html;

public class Webber {

	// Web URLs used in-app
	// XXX: Only URLS in this area!

	// web url for the food menu
	public static String foodAddress = "http://mpi.mashie.se/mashie/MashiePublic/MenuPresentation/Common/MenuSite.aspx?Siteid=4c2901c9-61f3-4b38-a30c-a02f00dc7f9b";

	// Url to it's learning
	public static String itslearningAddress = "https://falkoping.itslearning.com/elogin/default.aspx";

	// Url to Dexter
	public static String dexterAddress = "https://m11-mg-local.falnet.falkoping.se/mg-local/login?type=webtoken";

	// URL for signing up to elevkåren
	public static String signupAddress = "http://ebas.gymnasiet.sverigeselevkarer.se/signups/index/539";

	// XXX: End Of Area

	// XXX: Facebook ID:s in this area

	public static String theboard = "328398517288602";
	public static String PR = "324024177734072";
	public static String festare = "305522136255915";
	public static String spex = "268351709901709";
	public static String skolif = "547561921942120";
	public static String IT = "1403614713207231";

	// XXX: End Of Area

	// render schedule
	public static String renderSchedule(String number, int specday,
			Boolean showThisWeek, Point screenSize, Boolean playSport, Context c) {
		Calendar cal = Calendar.getInstance();

		int day = 0;
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		day = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (showThisWeek) {

			if (playSport) {

				if (cal.get(Calendar.HOUR_OF_DAY) >= 20 && specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					day++;
				} else if (cal.get(Calendar.HOUR_OF_DAY) >= 20 && specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
						|| specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					day = 0;
					week++;
					if (week > 52)
						week = 1;

				}

			} else {

				if (cal.get(Calendar.HOUR_OF_DAY) >= 16 && specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
						&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					day++;
				} else if (cal.get(Calendar.HOUR_OF_DAY) >= 16 && specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
						|| specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| specday == -1
						&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					day = 0;
					week++;
					if (week > 52)
						week = 1;

				}
			}
		} else {
			week++;
			if (week > 52)
				week = 1;
		}

		if (specday != -1)
			day = specday;

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
		List<NewsInfo> temp = Arrays.asList(getNews("", Mode.All));

		if (temp.isEmpty())
			return new NewsInfo[0];

		if (temp.size() > 2) {
			feed = new NewsInfo[3];
			feed[0] = temp.get(0);
			feed[1] = temp.get(1);

			// news info, show more news
			data = new NewsInfo();
			data.headline = "Fler nyheter...";
			data.type = null;
			data.handler = null;
			data.contentType = 1;
			data.uniqeIdentifier = null;
			feed[2] = data;
		} else {
			int i = 0;
			feed = new NewsInfo[temp.size()];
			for (NewsInfo NI : temp) {
				feed[i] = NI;
				i++;
			}
		}
		// return value
		return feed;
	}

	public static NewsInfo[] getNews(String uniqeIdentifier, Mode returnMode) {
		HttpResponse response;
		String htmlResponse;

		try {
			HttpHost targetHost = new HttpHost("teknikprogrammet.net");
			HttpGet targetGet = new HttpGet(
					"/AlleIT/Alleinfo/webadmin/includes/studentbody_news.php");
			HttpClient httpClient = new DefaultHttpClient();
			response = httpClient.execute(targetHost, targetGet);
			HttpEntity entity = response.getEntity();
			htmlResponse = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return new NewsInfo[0];
		}
		if (htmlResponse.contains("~") == false) {
			return new NewsInfo[0];
		}
		String[] delimitered = htmlResponse.split("~");

		if (returnMode == Mode.All) {
			NewsInfo[] data = new NewsInfo[delimitered.length / 7];
			for (int i = 0, x = 0; i < data.length; i++) {
				data[i] = new NewsInfo();
				data[i].headline = String
						.valueOf(Html.fromHtml(delimitered[x]));
				x++;
				data[i].description = String.valueOf(Html
						.fromHtml(delimitered[x]));
				x++;
				data[i].longDescription = String.valueOf(Html
						.fromHtml(delimitered[x]));
				x++;
				data[i].butURL = String.valueOf(Html.fromHtml(delimitered[x]));
				x++;
				data[i].type = String.valueOf(Html.fromHtml(delimitered[x]));
				x++;
				data[i].handler = String.valueOf(Html.fromHtml(delimitered[x]));
				x++;
				data[i].uniqeIdentifier = String.valueOf(Html
						.fromHtml(delimitered[x]));
				x++;
				data[i].contentType = 0;
			}
			return data;
		} else {
			return extractSpecificNews(uniqeIdentifier, delimitered);
		}
	}

	private static NewsInfo[] extractSpecificNews(String uniqeIdentifier,
			String[] delimitered) {
		for (int i = 6; i < delimitered.length; i += 7) {
			if (delimitered[i].contains(uniqeIdentifier)) {
				NewsInfo[] data = new NewsInfo[1];
				data[0] = new NewsInfo();
				data[0].headline = String.valueOf(Html
						.fromHtml(delimitered[i - 6]));
				data[0].description = String.valueOf(Html
						.fromHtml(delimitered[i - 5]));
				data[0].longDescription = String.valueOf(Html
						.fromHtml(delimitered[i - 4]));
				data[0].butURL = String.valueOf(Html
						.fromHtml(delimitered[i - 3]));
				data[0].type = String
						.valueOf(Html.fromHtml(delimitered[i - 2]));
				data[0].handler = String.valueOf(Html
						.fromHtml(delimitered[i - 1]));
				data[0].uniqeIdentifier = String.valueOf(Html
						.fromHtml(delimitered[i]));
				data[0].contentType = 0;
				return data;
			}
		}
		return new NewsInfo[0];
	}
}
