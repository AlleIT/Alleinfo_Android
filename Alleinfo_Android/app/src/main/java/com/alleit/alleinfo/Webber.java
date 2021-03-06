package com.alleit.alleinfo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Webber {

	// Web URLs used in-app
	// XXX: Only URLS in this area!

	// web url for the food menu
	public static String foodAddress = "https://mpi.mashie.eu/public/menu/falköpings+kommun/4c2901c9";

	// Url to it's learning
	public static String itslearningAddress = "https://falkoping.itslearning.com/Index.aspx";

	// Url to Dexter
	public static String dexterAddress = "https://m11-mg-local.falnet.falkoping.se/mg-local/login?type=webtoken";

	// URL for signing up to elevkåren
	public static String signupAddress = "http://ebas.gymnasiet.sverigeselevkarer.se/signups/index/539";

	// IP to server
	private static String serverAddress = "http://allebergsgymnasiet.se/elevkaren";

	// Url to news
	private static String NewsPath = "/Alleinfo/Client/news_fetcher.php";

	// Url to information about posters
	private static String PosterPath = "/Alleinfo/Client/utskotts_info.php";

	//URL to images
	private static String UtskottsBildPath = "/utskottsbilder/";

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

	public static NewsData[] getNews() {
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
				return new NewsData[0];
			}

			NewsData[] data = new NewsData[jsonResponse.length()];

			for (int i = 0; i < jsonResponse.length(); i++) {
				JSONObject jsonObject = jsonResponse.getJSONObject(i);

				data[i] = new NewsData();

				data[i].headline = String.valueOf(Html.fromHtml(jsonObject
						.getString("headline")));

				data[i].shortInfo = String.valueOf(Html.fromHtml(jsonObject
						.getString("shortInfo")));

				data[i].description = String.valueOf(Html.fromHtml(jsonObject
						.getString("description")));

				data[i].butURL = String.valueOf(Html.fromHtml(jsonObject
						.getString("butURL")));

				data[i].type = String.valueOf(Html.fromHtml(jsonObject
						.getString("type")));

				data[i].handler = String.valueOf(Html.fromHtml(jsonObject
						.getString("handler")));

				data[i].rawHandler = jsonObject.getString("handler");

				data[i].color = String.valueOf(Html.fromHtml(jsonObject
						.getString("color")));

				data[i].uniqueIdentifier = String.valueOf(Html
						.fromHtml(jsonObject.getString("id")));

				data[i].contentType = ContentType.News;
			}

			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return new NewsData[0];
		}

	}

	public static PosterData[] getKarInfo(Context c) {
		HttpResponse response;
		JSONArray jsonResponse;

		try {
			HttpClient httpClient = new DefaultHttpClient();

			response = httpClient.execute(new HttpGet(serverAddress
					+ PosterPath));

			HttpEntity entity = response.getEntity();

			String ans = EntityUtils.toString(entity);

			if (ans.equals("request failed") || ans.equals("")) {
				System.out
						.println("Servern gav ett ov\u00E4ntat svar n\u00E4r info skulle h\u00E4mtas; getKarInfo");
				System.out.println("Svar: \"" + ans + "\"");
				return new PosterData[0];
			}

			jsonResponse = new JSONArray(ans);

			PosterData[] data = new PosterData[jsonResponse.length()];

			for (int i = 0; i < jsonResponse.length(); i++) {
				JSONObject jsonObject = jsonResponse.getJSONObject(i);

				data[i] = new PosterData();

				data[i].name = String.valueOf(Html.fromHtml(jsonObject
						.getString("handler")));
				data[i].handler = jsonObject.getString("handler");
				data[i].color = jsonObject.getString("color");
				data[i].description = String.valueOf(Html.fromHtml(jsonObject
						.getString("description")));
				data[i].socialLink = jsonObject.getString("socialLink");
				data[i].logo = getImg(c, jsonObject.getString("logoPath"), "");
			}

			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return new PosterData[0];
		}

	}

	public static Drawable getImg(Context c, String link, String rawHandler) {
		try {

			if (link.length() == 0) {
				HttpResponse response;
				JSONArray jsonResponse;

				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost post = new HttpPost(serverAddress + PosterPath);

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);

					nameValuePairs.add(new BasicNameValuePair("handler",
							rawHandler));

					post.setEntity(new UrlEncodedFormEntity(nameValuePairs,
							"UTF-8"));

					response = httpClient.execute(post);

					HttpEntity entity = response.getEntity();

					String ans = EntityUtils.toString(entity);

					if (ans.equals("request failed") || ans.equals("")) {
						System.out
								.println("Servern gav ett ov\u00E4ntat svar n\u00E4r bilden skulle h\u00E4mtas");
						System.out.println("Svar: \"" + ans + "\"");

						return c.getResources().getDrawable(
								android.R.drawable.ic_menu_report_image);
					}

					jsonResponse = new JSONArray(ans);

					link = jsonResponse.getJSONObject(0).getString("logoPath");

				} catch (Exception e) {
					e.printStackTrace();

					return c.getResources().getDrawable(
							android.R.drawable.ic_menu_report_image);
				}
			}

			URL url = new URL(serverAddress + UtskottsBildPath + URLEncoder.encode(link, "UTF-8").replace("+", "%20"));
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
