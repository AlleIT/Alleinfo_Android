package com.felectronix.alleinfo;

import java.util.Calendar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Home extends SherlockActivity {
	
	Drawable currcolor;
	Button home;
	Button food;
	Button schedule;
	Button calendar;
	Button blogs;
	Button itsLearning;
	TextView todDay, todDate, todWeek;
	String[] colorlist;
	HomePage current = HomePage.Start;
	SlidingMenu leftBar;
	ActionBar bar;
	ViewGroup viewGroup;
	Menu menu;
	int chosenDay = -1;
	Boolean showThisWeek = true;
	Point xy;
	Context c;
	WebView mWebView;
	AlertDialog AD;
	private String number = null;
	SharedPreferences sharedP;
	String pin = null;
	NewsInfo[] listData;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);

		// Get personal number
		Intent fromIntent = getIntent();
		number = fromIntent.getStringExtra("number");

		sharedP = this.getSharedPreferences("com.felectronix.alleinfo", 0);

		leftBar = new SlidingMenu(this);
		leftBar.setMode(SlidingMenu.LEFT);
		leftBar.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		leftBar.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		leftBar.setBehindWidth((int) (getApplicationContext().getResources()
				.getDisplayMetrics().density * 300));
		leftBar.setMenu(R.layout.leftbar);

		c = this;

		viewGroup = (ViewGroup) findViewById(R.id.content);

		home = (Button) findViewById(R.id.homeSlide);
		food = (Button) findViewById(R.id.foodSlide);
		schedule = (Button) findViewById(R.id.schemeSlide);
		calendar = (Button) findViewById(R.id.calSlide);
		blogs = (Button) findViewById(R.id.blogSlide);
		itsLearning = (Button) findViewById(R.id.itsSlide);

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			xy = new Point();
			display.getSize(xy);
		} else {
			xy = new Point(display.getWidth(), display.getHeight());
		}

		bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		colorlist = getResources().getStringArray(R.array.colors);
		makeHome();

		home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeHome();
			}
		});
		food.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeFood();
			}
		});
		schedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeScheme();
			}
		});
		calendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeCal();
			}
		});
		blogs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeBlog();
			}
		});
		itsLearning.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeItsLearning();
			}
		});
	}

	private void makeHome() {
		chosenDay = -1;
		showThisWeek = true;
		if (current != HomePage.Home) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.home, null));
			current = HomePage.Home;
			todDay = (TextView) findViewById(R.id.dayShow);
			todDate = (TextView) findViewById(R.id.dateShow);
			todWeek = (TextView) findViewById(R.id.weekShow);
			Calendar cal = Calendar.getInstance();
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				todDay.setText("Måndag");
				break;
			case Calendar.TUESDAY:
				todDay.setText("Tisdag");
				break;
			case Calendar.WEDNESDAY:
				todDay.setText("Onsdag");
				break;
			case Calendar.THURSDAY:
				todDay.setText("Torsdag");
				break;
			case Calendar.FRIDAY:
				todDay.setText("Fredag");
				break;
			case Calendar.SATURDAY:
				todDay.setText("Lördag");
				break;
			case Calendar.SUNDAY:
				todDay.setText("Söndag");
				break;
			}
			todDate.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "/" + String.valueOf(cal.get(Calendar.MONTH)) + " - "
					+ String.valueOf(cal.get(Calendar.YEAR)));
			todWeek.setText("Vecka "
					+ String.valueOf(cal.get(Calendar.WEEK_OF_YEAR)));
			checkColors();
			prepWeb();
			prepFeed();
		}
		if (leftBar.isMenuShowing())
			leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeScheme() {
		if (current != HomePage.Schema) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Schema;
			chosenDay = -1;
			showThisWeek = true;
			checkColors();
		}
		leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void makeFood() {
		if (current != HomePage.Mat) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Mat;
			chosenDay = -1;
			checkColors();
		}
		leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void makeCal() {
		if (current != HomePage.Kalender) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Kalender;
			checkColors();
		}
		leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();

	}

	private void makeBlog() {
		if (current != HomePage.Kalender) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Kalender;
			checkColors();
		}
		leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeItsLearning() {
		if (current != HomePage.ItsLearning) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.ItsLearning;
			checkColors();
		}
		leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void prepFeed() {
		final Activity a = this;
		final Webber webHandler = new Webber();
		final ListView listView = (ListView) a.findViewById(R.id.mininew);
		new Thread(new Runnable() {

			@Override
			public void run() {

				listData = webHandler.getTinyNewsFeed();

				runOnUiThread(new Runnable() {
					public void run() {
						NewsFeedAdapter itemAdapter = new NewsFeedAdapter(a,
								R.layout.elevkar_previewrow, listData);
						listView.setAdapter(itemAdapter);
					}
				});

			}

		}).start();

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listData[position].contentType == 1) {
					// TODO: Visa fler nyheter
					// makeNewsFeed();
				} else {
					// TODO: Visa nyheten i fråga.
					// ShowNew(listData[position].uniqeIdentifier);
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void checkColors() {

		currcolor = new ColorDrawable(Color.TRANSPARENT);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			home.setBackgroundDrawable(currcolor);
		else
			home.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			schedule.setBackgroundDrawable(currcolor);
		else
			schedule.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			food.setBackgroundDrawable(currcolor);
		else
			food.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			calendar.setBackgroundDrawable(currcolor);
		else
			calendar.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			blogs.setBackgroundDrawable(currcolor);
		else
			blogs.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			itsLearning.setBackgroundDrawable(currcolor);
		else
			itsLearning.setBackground(currcolor);

		switch (current) {

		case Home:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[0]));
			bar.setTitle("Hem");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				home.setBackgroundDrawable(currcolor);
			else
				home.setBackground(currcolor);
			break;

		case Schema:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[1]));
			bar.setTitle("Schema");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				schedule.setBackgroundDrawable(currcolor);
			else
				schedule.setBackground(currcolor);
			break;

		case Mat:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[2]));
			bar.setTitle("Skolmat");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				food.setBackgroundDrawable(currcolor);
			else
				food.setBackground(currcolor);
			break;

		case Kalender:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[3]));
			bar.setTitle("Kalender");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				calendar.setBackgroundDrawable(currcolor);
			else
				calendar.setBackground(currcolor);
			break;

		case Bloggar:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[4]));
			bar.setTitle("Bloggar");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				blogs.setBackgroundDrawable(currcolor);
			else
				blogs.setBackground(currcolor);
			break;

		case ItsLearning:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[5]));
			bar.setTitle("It's learning");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				itsLearning.setBackgroundDrawable(currcolor);
			else
				itsLearning.setBackground(currcolor);
			break;

		default:
			current = HomePage.Home;
			currcolor = new ColorDrawable(Color.parseColor(colorlist[6]));
			bar.setTitle("Hem");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				home.setBackgroundDrawable(currcolor);
			else
				home.setBackground(currcolor);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public void prepWeb() {
		final WebView WV;
		WV = (WebView) findViewById(R.id.webber);
		final ProgressBar PB = (ProgressBar) findViewById(R.id.loadprogress);
		final TextView progress = (TextView) findViewById(R.id.progress);
		final Webber webHandler = new Webber();
		PB.setVisibility(View.VISIBLE);
		progress.setVisibility(View.VISIBLE);

		WV.clearView();
		WV.clearCache(true);
		WV.getSettings().setJavaScriptEnabled(true);
		WV.setVerticalScrollBarEnabled(false);

		WV.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				PB.setVisibility(View.VISIBLE);
				progress.setVisibility(View.VISIBLE);
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				if (PB.isShown()) {
					PB.setVisibility(View.INVISIBLE);
					progress.setVisibility(View.INVISIBLE);
					mWebView = WV;
				}
			}

			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

		});

		WV.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress <= 99 && PB.getVisibility() != View.VISIBLE) {
					PB.setVisibility(View.VISIBLE);
					progress.setVisibility(View.VISIBLE);
				}
				if (newProgress == 100) {
					PB.setVisibility(View.INVISIBLE);
					progress.setVisibility(View.INVISIBLE);
				}
				progress.setText(String.valueOf(newProgress));
			}

		});

		if (current == HomePage.Home || current == HomePage.Schema) {
			new Thread(new Runnable() {

				@Override
				public void run() {

					final String toLoad = webHandler.renderSchedule(number,
							chosenDay, showThisWeek, xy);

					chosenDay = -1;

					runOnUiThread(new Runnable() {
						public void run() {
							WV.getSettings().setUseWideViewPort(false);
							WV.getSettings().setLoadWithOverviewMode(false);
							WV.getSettings().setLayoutAlgorithm(
									LayoutAlgorithm.SINGLE_COLUMN);
							WV.getSettings().setBuiltInZoomControls(false);
							WV.getSettings().setSupportZoom(false);
							WV.setPadding(0, 0, 0, 0);
							WV.setInitialScale(0);
							WV.loadUrl(toLoad);
						}
					});

				}

			}).start();
		}

		if (current == HomePage.Mat) {
			WV.getSettings().setUseWideViewPort(false);
			WV.getSettings().setLoadWithOverviewMode(false);
			WV.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
			WV.getSettings().setBuiltInZoomControls(false);
			WV.getSettings().setSupportZoom(false);
			WV.setPadding(0, 0, 0, 0);

			float scaling = 100;
			int display_width;
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			display_width = dm.widthPixels;
			scaling = (((float) display_width / 430) * 100);
			scaling = (int) Math.floor(scaling);
			WV.setInitialScale((int) scaling);

			WV.loadUrl(webHandler.foodAddress);
		}

		if (current == HomePage.ItsLearning) {
			WV.getSettings().setUseWideViewPort(true);
			WV.getSettings().setLoadWithOverviewMode(true);
			WV.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
			WV.getSettings().setBuiltInZoomControls(true);
			if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				WV.getSettings().setDisplayZoomControls(false);
			}
			WV.getSettings().setSupportZoom(true);
			WV.setPadding(0, 0, 0, 0);
			WV.setInitialScale(0);
			WV.loadUrl("https://falkoping.itslearning.com/elogin/default.aspx");

		}
		mWebView = WV;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		if (current == HomePage.Schema) {
			menu.findItem(R.id.WeekDayContainer).setVisible(true);

			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
					|| chosenDay == -1 && cal.get(Calendar.HOUR_OF_DAY) >= 16
					&& cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
				menu.findItem(R.id.whatWeekContainer).setVisible(true);

			}
		} else {
			menu.findItem(R.id.WeekDayContainer).setVisible(false);
			menu.findItem(R.id.whatWeekContainer).setVisible(false);
		}

		if (current == HomePage.ItsLearning) {
			menu.findItem(R.id.goBack).setVisible(true);
			menu.findItem(R.id.goForward).setVisible(true);
		} else {
			menu.findItem(R.id.goBack).setVisible(false);
			menu.findItem(R.id.goForward).setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			leftBar.toggle();
			return true;

			// Schema

		case R.id.mandag:
			chosenDay = 1;
			prepWeb();
			return true;

		case R.id.tisdag:
			chosenDay = 2;
			prepWeb();
			return true;

		case R.id.onsdag:
			chosenDay = 3;
			prepWeb();
			return true;

		case R.id.torsdag:
			chosenDay = 4;
			prepWeb();
			return true;

		case R.id.fredag:
			chosenDay = 5;
			prepWeb();
			return true;

		case R.id.wholeWeek:
			chosenDay = 0;
			prepWeb();
			return true;

		case R.id.thisWeek:
			item.setChecked(true);
			showThisWeek = true;
			prepWeb();
			return true;

		case R.id.nextWeek:
			showThisWeek = false;
			item.setChecked(true);
			prepWeb();
			return true;

			// It's learning
		case R.id.goBack:
			if (mWebView.canGoBack() == true) {
				mWebView.goBack();
			}
			return true;

		case R.id.goForward:
			if (mWebView.canGoForward() == true) {
				mWebView.goForward();
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			leftBar.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (leftBar.isMenuShowing()) {
			leftBar.toggle();
		} else {
			if (current != HomePage.Home) {
				makeHome();
			} else
				finish();
		}
	}

}