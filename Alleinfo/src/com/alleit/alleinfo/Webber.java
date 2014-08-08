package com.alleit.alleinfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

	// IP to server
	private static String serverAddress = "http://83.223.17.30"; // TODO: Change
																	// to
																	// teknikprogrammet.net
																	// when URL
																	// is back

	// Url to news
	private static String NewsPath = "/AlleIT/Alleinfo/webbadmin/includes/studentbody_news.php";

	// Url to information about posters
	private static String PosterPath = "/AlleIT/Alleinfo/webbadmin/includes/poster_info.php";

	// XXX: End Of Area

	// XXX: Facebook ID:s in this area

	public static String theboard = "328398517288602"; // Styrelsen
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
				}
			}

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
			data.type = "";
			data.handler = "";
			data.color = "#FFFFFF";
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
		return feed;
	}

	public static NewsInfo[] getNews(String uniqeIdentifier, Mode returnMode) {
		HttpResponse response;
		JSONArray jsonResponse;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			response = httpClient
					.execute(new HttpGet(serverAddress + NewsPath));
			HttpEntity entity = response.getEntity();
			jsonResponse = new JSONArray(EntityUtils.toString(entity));

			if (jsonResponse.length() == 0) {
				System.out.println("Servern gav ett tomt svar");
				return new NewsInfo[0];
			}

			if (returnMode == Mode.All) {
				NewsInfo[] data = new NewsInfo[jsonResponse.length()];

				for (int i = 0; i < jsonResponse.length(); i++) {
					JSONObject jsonObject = jsonResponse.getJSONObject(i);

					data[i] = new NewsInfo();

					data[i].headline = String.valueOf(Html.fromHtml(jsonObject
							.getString("headline")));

					data[i].shortInfo = String.valueOf(Html.fromHtml(jsonObject
							.getString("shortInfo")));

					data[i].description = String.valueOf(Html
							.fromHtml(jsonObject.getString("description")));

					data[i].butURL = String.valueOf(Html.fromHtml(jsonObject
							.getString("butURL")));

					data[i].type = String.valueOf(Html.fromHtml(jsonObject
							.getString("type")));

					data[i].handler = String.valueOf(Html.fromHtml(jsonObject
							.getString("handler")));

					data[i].rawHandler = String.valueOf(jsonObject
							.getString("handler"));

					data[i].color = String.valueOf(Html.fromHtml(jsonObject
							.getString("color")));

					data[i].uniqeIdentifier = String.valueOf(Html
							.fromHtml(jsonObject.getString("id")));

					data[i].contentType = 0;
				}
				return data;
			} else {
				for (int i = 0; i < jsonResponse.length(); i++) {
					if (jsonResponse.getJSONObject(i).getString("id")
							.equals(uniqeIdentifier)) {

						JSONObject jsonObject = jsonResponse.getJSONObject(i);

						NewsInfo[] data = new NewsInfo[1];

						data[0] = new NewsInfo();

						data[0].headline = String.valueOf(Html
								.fromHtml(jsonObject.getString("headline")));

						data[0].shortInfo = String.valueOf(Html
								.fromHtml(jsonObject.getString("shortInfo")));

						data[0].description = String.valueOf(Html
								.fromHtml(jsonObject.getString("description")));

						data[0].butURL = String.valueOf(Html
								.fromHtml(jsonObject.getString("butURL")));

						data[0].type = String.valueOf(Html.fromHtml(jsonObject
								.getString("type")));

						data[0].handler = String.valueOf(Html
								.fromHtml(jsonObject.getString("handler")));

						data[0].rawHandler = String.valueOf(jsonObject
								.getString("handler"));

						data[0].color = String.valueOf(Html.fromHtml(jsonObject
								.getString("color")));

						data[0].uniqeIdentifier = String.valueOf(Html
								.fromHtml(jsonObject.getString("id")));

						data[0].contentType = 0;
						return data;
					}
				}
				return new NewsInfo[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new NewsInfo[0];
		}

	}

	public static Drawable getImg(Context c, String handler) {
		HttpResponse response;
		JSONArray jsonResponse;

		try {
			// Get ImageURL
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(serverAddress + PosterPath);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("handler", handler));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();

			String ans = EntityUtils.toString(entity);

			if (ans.equals("request failed") || ans.equals("") || ans == null) {
				System.out
						.println("Servern gav ett ov\u00E4ntat svar n\u00E4r bilden skulle h\u00E4mtas");
				System.out.println("Svar: \"" + ans + "\"");
				return c.getResources().getDrawable(
						android.R.drawable.ic_menu_report_image);
			}

			jsonResponse = new JSONArray(ans);

			// Get Image
			URL url = new URL(jsonResponse.getJSONObject(0).getString(
					"logoPath"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(10000);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			InputStream is = conn.getInputStream();
			return new BitmapDrawable(c.getResources(),
					BitmapFactory.decodeStream(is));
		} catch (Exception e) {
			e.printStackTrace();
			return c.getResources().getDrawable(
					android.R.drawable.ic_menu_report_image);
		}

	}
}
