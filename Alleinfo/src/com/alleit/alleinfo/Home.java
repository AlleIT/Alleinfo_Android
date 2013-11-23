package com.alleit.alleinfo;

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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Home extends SherlockActivity {

	Drawable currcolor;

	/*
	 * Buttons in the sidebar. Please keep these in the same order as the actual
	 * buttons
	 */
	Button home;
	Button schedule;
	Button food;
	Button news;
	Button elevkaren;
	Button dexter;
	Button itsLearning;

	TextView todDay, todDate, todWeek;

	// Colors for the sidebar buttons. Expand in strings.xml as necessary.
	String[] colorlist;

	// The user's location
	HomePage current = HomePage.Start;

	SlidingMenu leftBar;
	ActionBar bar;
	ViewGroup viewGroup;
	Menu menu;

	// TODO: Convert chosenDay to enum for simplicity
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

		sharedP = this.getSharedPreferences("com.alleit.alleinfo", 0);

		// Set up sidebar menu
		leftBar = new SlidingMenu(this);
		leftBar.setMode(SlidingMenu.LEFT);
		leftBar.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		leftBar.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		leftBar.setBehindWidth((int) (getApplicationContext().getResources()
				.getDisplayMetrics().density * 300));
		leftBar.setMenu(R.layout.leftbar);

		c = this;

		viewGroup = (ViewGroup) findViewById(R.id.content);

		/*
		 * Please keep these in the same order as the actual buttons
		 * (leftbar.xml)
		 */
		home = (Button) findViewById(R.id.homeSlide);
		food = (Button) findViewById(R.id.foodSlide);
		schedule = (Button) findViewById(R.id.schemeSlide);
		news = (Button) findViewById(R.id.newsSlide);
		elevkaren = (Button) findViewById(R.id.karSlide);
		dexter = (Button) findViewById(R.id.dexSlide);
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
		bar.setDisplayHomeAsUpEnabled(false);
		bar.setHomeButtonEnabled(true);
		colorlist = getResources().getStringArray(R.array.colors);
		makeHome();

		/*
		 * XXX: OnClick listeners for sidebar Please keep these in the same
		 * order as the actual buttons
		 */

		// if user clicks on home menu button

		home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeHome();
			}
		});

		// if user clicks on food menu button

		food.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeFood();
			}
		});

		// if user clicks on schedule button

		schedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeScheme();
			}
		});

		// if user clicks on news button

		news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeNews();
			}
		});

		// if user clicks on "elevkar" button

		elevkaren.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeKar();
			}
		});

		// if user clicks on Dexter button

		dexter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeDexter();
			}
		});

		// if user clicks on itsLearning button

		itsLearning.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeItsLearning();
			}
		});
	}

	/*
	 * XXX: makeXxx() Prepare the various pages here. Keep the methods in the
	 * same order as the actual buttons please.
	 */

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
				todDay.setText(Html.fromHtml("M&aring;ndag"));
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
				todDay.setText(Html.fromHtml("L&ouml;rdag"));
				break;
			case Calendar.SUNDAY:
				todDay.setText(Html.fromHtml("S&ouml;ndag"));
				break;
			}
			todDate.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "/" + String.valueOf(cal.get(Calendar.MONTH) + 1) + " - "
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
		if (leftBar.isMenuShowing())
			leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void makeFood() {
		if (current != HomePage.Mat) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Mat;
			checkColors();
		}
		leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void makeNews() {
		if (current != HomePage.Nyheter) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null)); // TODO:
																		// Set
																		// the
																		// corresponding
																		// layout
			current = HomePage.Nyheter;
			checkColors();
		}
		if (leftBar.isMenuShowing())
			leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		// TODO: Load the newsfeed
	}

	private void makeKar() {
		if (current != HomePage.Elevkaren) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.elevkar, null));
			current = HomePage.Elevkaren;
			checkColors();

			// "buttons" for the various student assemblies
			LinearLayout theboardbut = (LinearLayout) findViewById(R.id.ebg);
			LinearLayout prbut = (LinearLayout) findViewById(R.id.prbg);
			LinearLayout festarebut = (LinearLayout) findViewById(R.id.festarebg);
			LinearLayout spexbut = (LinearLayout) findViewById(R.id.spexbg);
			LinearLayout skolifbut = (LinearLayout) findViewById(R.id.ifbg);
			LinearLayout alleitbut = (LinearLayout) findViewById(R.id.itbg);

			// Clickevents
			theboardbut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					makeKarSub(StudentAssembly.Styrelsen);
				}

			});
			prbut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					makeKarSub(StudentAssembly.PR);
				}

			});
			festarebut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					makeKarSub(StudentAssembly.Ållefestare);
				}

			});
			spexbut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					makeKarSub(StudentAssembly.ÅlleSpex);
				}

			});
			skolifbut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					makeKarSub(StudentAssembly.SkolIF);
				}

			});
			alleitbut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					makeKarSub(StudentAssembly.ÅlleIT);
				}

			});

		}
		if (leftBar.isMenuShowing())
			leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeKarSub(StudentAssembly SA) {
		if (current != HomePage.Elevkaren_SUB) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.detailview, null));
			current = HomePage.Elevkaren_SUB;
			checkColors();

			ImageView Pic = (ImageView) findViewById(R.id.pic);
			ProgressBar PB = (ProgressBar) findViewById(R.id.loadimg);
			Button but = (Button) findViewById(R.id.button);
			TextView rubrik = (TextView) findViewById(R.id.head);
			TextView info = (TextView) findViewById(R.id.info);
			TextView desc = (TextView) findViewById(R.id.desc);
			RelativeLayout separator = (RelativeLayout) findViewById(R.id.separator);

			PB.setVisibility(View.GONE);
			but.setText("Besök Facebooksidan");

			String url = "http://facebook.com/";

			// Describṕtions picked up from facebook
			// XXX: These need to be updated or kept up to date in some way.
			switch (SA) {
			case Styrelsen:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.elevkaren));
				rubrik.setText(Html.fromHtml(Karlista.Ename));
				info.setText("");
				separator.setBackgroundColor(Color.parseColor(Karlista.Ecolor));
				desc.setText(Html.fromHtml(Karlista.Edesc));
				url += Webber.theboard;
				break;
			case PR:
				Pic.setImageDrawable(getResources().getDrawable(R.drawable.pr));
				rubrik.setText(Html.fromHtml(Karlista.PRname));
				info.setText("");
				separator
						.setBackgroundColor(Color.parseColor(Karlista.PRcolor));
				desc.setText(Html.fromHtml(Karlista.PRdesc));
				url += Webber.PR;
				break;
			case Ållefestare:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.festare));
				rubrik.setText(Html.fromHtml(Karlista.Fname));
				info.setText("");
				separator.setBackgroundColor(Color.parseColor(Karlista.Fcolor));
				desc.setText(Html.fromHtml(Karlista.Fdesc));
				url += Webber.festare;
				break;
			case ÅlleSpex:
				Pic.setImageDrawable(getResources()
						.getDrawable(R.drawable.spex));
				rubrik.setText(Html.fromHtml(Karlista.spexname));
				info.setText("");
				separator.setBackgroundColor(Color
						.parseColor(Karlista.spexcolor));
				desc.setText(Html.fromHtml(Karlista.spexdesc));
				url += Webber.spex;
				break;
			case SkolIF:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.skolif));
				rubrik.setText(Html.fromHtml(Karlista.IFname));
				info.setText("");
				separator
						.setBackgroundColor(Color.parseColor(Karlista.IFcolor));
				desc.setText(Html.fromHtml(Karlista.IFdesc));
				url += Webber.skolif;
				break;
			case ÅlleIT:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.alleit));
				rubrik.setText(Html.fromHtml(Karlista.ITname));
				info.setText("");
				separator
						.setBackgroundColor(Color.parseColor(Karlista.ITcolor));
				desc.setText(Html.fromHtml(Karlista.ITdesc));
				url += Webber.IT;
				break;
			}

			final String finURL = url;

			but.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// Open the facebook page in the default browser.
					Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(finURL));
					startActivity(myIntent);

				}

			});

		}
		if (leftBar.isMenuShowing())
			leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeDexter() {
		if (current != HomePage.Dexter) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Dexter;
			checkColors();
		}
		if (leftBar.isMenuShowing())
			leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void makeItsLearning() {
		if (current != HomePage.ItsLearning) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.ItsLearning;
			checkColors();
		}
		if (leftBar.isMenuShowing())
			leftBar.toggle();
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	// get all the latest news from elevkaren

	private void prepFeed() {
		final Activity a = this;
		if (current == HomePage.Home) {
			final ListView listView = (ListView) a.findViewById(R.id.mininew);
			new Thread(new Runnable() {

				@Override
				public void run() {

					listData = Webber.getTinyNewsFeed();

					runOnUiThread(new Runnable() {
						public void run() {
							NewsFeedAdapter itemAdapter = new NewsFeedAdapter(
									a, R.layout.newsroll, listData);
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
		} else if (current == HomePage.Elevkaren) {

		}

	}

	// Check colors for the different buttons in the menu

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void checkColors() {

		currcolor = new ColorDrawable(Color.TRANSPARENT);

		// Reset the colors of all the buttons in the sidebar
		// Please keep these in the same order as the actual buttons

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
			news.setBackgroundDrawable(currcolor);
		else
			news.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			elevkaren.setBackgroundDrawable(currcolor);
		else
			elevkaren.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			dexter.setBackgroundDrawable(currcolor);
		else
			dexter.setBackground(currcolor);

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			itsLearning.setBackgroundDrawable(currcolor);
		else
			itsLearning.setBackground(currcolor);

		switch (current) {

		// which case, depends on what button the user clicks on

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

		case Nyheter:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[3]));
			bar.setTitle("Nyheter");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				news.setBackgroundDrawable(currcolor);
			else
				news.setBackground(currcolor);
			break;

		case Elevkaren:
		case Elevkaren_SUB:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[4]));
			bar.setTitle("Elevkåren");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				elevkaren.setBackgroundDrawable(currcolor);
			else
				elevkaren.setBackground(currcolor);
			break;

		case Dexter:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[5]));
			bar.setTitle("Dexter");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				dexter.setBackgroundDrawable(currcolor);
			else
				dexter.setBackground(currcolor);
			break;

		case ItsLearning:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[6]));
			bar.setTitle("It's learning");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				itsLearning.setBackgroundDrawable(currcolor);
			else
				itsLearning.setBackground(currcolor);
			break;

		default:
			current = HomePage.Home;
			currcolor = new ColorDrawable(Color.parseColor(colorlist[0]));
			bar.setTitle("Hem");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				home.setBackgroundDrawable(currcolor);
			else
				home.setBackground(currcolor);
			break;
		}
	}

	// progressbar, show loading when web is loading

	@SuppressWarnings("deprecation")
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public void prepWeb() {
		final WebView WV;
		WV = (WebView) findViewById(R.id.webber);
		final ProgressBar PB = (ProgressBar) findViewById(R.id.loadprogress);
		final TextView progress = (TextView) findViewById(R.id.progress);
		final Button retry = (Button) findViewById(R.id.retry);
		PB.setVisibility(View.VISIBLE);
		progress.setVisibility(View.VISIBLE);
		progress.setText("0");

		WV.clearView();
		WV.clearCache(true);
		WV.getSettings().setJavaScriptEnabled(true);
		WV.setVerticalScrollBarEnabled(false);

		// Allow/Deny the user to click links on pages

		WV.setWebViewClient(new WebViewClient() {

			String lasturl = null;
			Boolean error = false;

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// We want to avoid navigation from some pages
				if (current != HomePage.Schema && current != HomePage.Mat) {
					PB.setVisibility(View.VISIBLE);
					progress.setVisibility(View.VISIBLE);
					retry.setVisibility(View.INVISIBLE);
					progress.setText("0");
					error = false;
					view.loadUrl(url);
				}
				return true;
			}

			// when the page is finished

			public void onPageFinished(WebView view, String url) {
				if (PB.isShown() && error == false) {
					PB.setVisibility(View.INVISIBLE);
					progress.setVisibility(View.INVISIBLE);
					mWebView = WV;
				}
			}

			// if SSL certificate doesn't work, don't show any error

			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				lasturl = failingUrl;
				WV.loadUrl("about:blank");
				tryAgain(WV, PB, progress, retry, "Sidan kunde inte laddas",
						lasturl);
			}

		});

		// Show the progress of the loading page
		WV.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int newProgress) {
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				Boolean connected;
				if (connectivityManager.getActiveNetworkInfo() != null) {
					connected = connectivityManager.getActiveNetworkInfo()
							.isConnectedOrConnecting();
				} else {
					final String lasturl = WV.getUrl();
					tryAgain(WV, PB, progress, retry,
							"Det finns ingen uppkoppling", lasturl);
					return;
				}

				if (connected != null && connected != false) {
					if (newProgress <= 98) {
						if (PB.getVisibility() != View.VISIBLE) {
							PB.setVisibility(View.VISIBLE);
							progress.setVisibility(View.VISIBLE);
							retry.setVisibility(View.GONE);
						}
						progress.setText(String.valueOf(newProgress));
					}
					if (newProgress == 99) {
						PB.setVisibility(View.INVISIBLE);
						progress.setVisibility(View.INVISIBLE);
					}
				} else {
					final String lasturl = WV.getUrl();
					tryAgain(WV, PB, progress, retry,
							"Det finns ingen uppkoppling", lasturl);
				}
			}

		});

		// Load Schedule
		if (current == HomePage.Home || current == HomePage.Schema) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					final String toLoad = Webber.renderSchedule(number,
							chosenDay, showThisWeek, xy, c);
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

		// Load Food menu

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

			WV.loadUrl(Webber.foodAddress);
		}

		// Load It's Learning or Dexter

		if (current == HomePage.ItsLearning || current == HomePage.Dexter) {
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
			if (current == HomePage.Dexter)
				WV.loadUrl(Webber.dexterAddress);
			else
				WV.loadUrl(Webber.itslearningAddress);

		}
		mWebView = WV;
	}

	private void tryAgain(final WebView WV, ProgressBar PB, TextView progress,
			final Button retry, String message, final String lasturl) {
		PB.setVisibility(View.GONE);
		progress.setText(message);
		progress.setVisibility(View.VISIBLE);
		retry.setVisibility(View.VISIBLE);
		retry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				retry.setVisibility(View.GONE);
				if (lasturl.equalsIgnoreCase("about:blank")) {
					prepWeb();
				} else {
					WV.loadUrl(lasturl);
				}
			}

		});
	}

	private void sendEmail(String recipant) {
		Uri uri = Uri.parse("mailto:" + recipant);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		if (current == HomePage.Schema) {
			menu.findItem(R.id.WeekDayContainer).setVisible(true);

			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
						&& cal.get(Calendar.HOUR_OF_DAY) > 16
						&& !sharedP.getBoolean("playSports", false)
						|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
						&& cal.get(Calendar.HOUR_OF_DAY) > 20) {
					showThisWeek = false;
					menu.findItem(R.id.whatWeekContainer).setVisible(false);
				} else
					menu.findItem(R.id.whatWeekContainer).setVisible(true);
			} else {
				showThisWeek = false;
				menu.findItem(R.id.whatWeekContainer).setVisible(false);
			}
		} else {
			menu.findItem(R.id.WeekDayContainer).setVisible(false);
			menu.findItem(R.id.whatWeekContainer).setVisible(false);
		}

		if (current == HomePage.ItsLearning || current == HomePage.Dexter) {
			menu.findItem(R.id.goBack).setVisible(true);
			menu.findItem(R.id.goForward).setVisible(true);
		} else {
			menu.findItem(R.id.goBack).setVisible(false);
			menu.findItem(R.id.goForward).setVisible(false);
		}

		if (current == HomePage.Elevkaren || current == HomePage.Elevkaren_SUB) {
			menu.findItem(R.id.beMember).setVisible(true);
			menu.findItem(R.id.contact).setVisible(true);
		} else {
			menu.findItem(R.id.beMember).setVisible(false);
			menu.findItem(R.id.contact).setVisible(false);
		}

		return true;
	}

	// toggle

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			leftBar.toggle();
			return true;

			// Schedule

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

		case R.id.beMember:
			// Open the signup page in the default browser.
			Intent myIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Webber.signupAddress));
			startActivity(myIntent);
			return true;

		case R.id.contactKaren:
			sendEmail(Karlista.Eemail);
			return true;

		case R.id.contactpr:
			sendEmail(Karlista.PRemail);
			return true;

		case R.id.contactFestare:
			sendEmail(Karlista.Femail);
			return true;

		case R.id.contactSpex:
			sendEmail(Karlista.spexemail);
			return true;

		case R.id.contactSkolIF:
			sendEmail(Karlista.IFemail);
			return true;

		case R.id.contactIT:
			sendEmail(Karlista.ITemail);
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

	// go back to home screen

	@Override
	public void onBackPressed() {
		if (leftBar.isMenuShowing()) {
			leftBar.toggle();
			return;
		}
		if (current == HomePage.ItsLearning || current == HomePage.Dexter) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				makeHome();
			}
		} else if (current == HomePage.Elevkaren_SUB) {
			makeKar();
		} else {
			if (current != HomePage.Home) {
				makeHome();
			} else {
				finish();
			}

		}
	}

}