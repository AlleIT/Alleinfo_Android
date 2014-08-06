package com.alleit.alleinfo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.alleit.Alleinfo_Android.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends Activity {

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

	ActionBar bar;
	ActionBarDrawerToggle mDrawerToggle;
	ViewGroup viewGroup;
	Menu menu;

	// TODO: Convert chosenDay to enum for simplicity
	int chosenDay = -1;

	Boolean showThisWeek = true;
	Point xy;
	Context c;
	WebView mWebView;
	DrawerLayout mDrawerLayout;
	ScrollView leftBar;
	AlertDialog AD;
	private String number = null;
	SharedPreferences sharedP;
	String pin = null;
	NewsInfo[] listData;
	Boolean isPlayingSport;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);

		// Get personal number
		Intent fromIntent = getIntent();
		number = fromIntent.getStringExtra(PreferenceInfo.Pers_Num_Name);

		sharedP = getApplicationContext().getSharedPreferences(
				PreferenceInfo.Preference_Name, MODE_PRIVATE);

		if (fromIntent.getBooleanExtra("Stored", false) == false) {
			isPlayingSport = fromIntent.getBooleanExtra(
					PreferenceInfo.Extended_Schedule_Display_Name, false);
		} else {
			isPlayingSport = sharedP.getBoolean(
					PreferenceInfo.Extended_Schedule_Display_Name, false);
		}

		/*
		 * TODO: Remove // Set up sidebar menu leftBar = new SlidingMenu(this);
		 * leftBar.setMode(SlidingMenu.LEFT);
		 * leftBar.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		 * leftBar.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		 * leftBar.setBehindWidth((int) (getApplicationContext().getResources()
		 * .getDisplayMetrics().density * 300));
		 * leftBar.setMenu(R.layout.leftbar);
		 */

		c = this;

		viewGroup = (ViewGroup) findViewById(R.id.content);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftBar = (ScrollView) findViewById(R.id.leftBarContainer);

		/*
		 * Please keep these in the same order as the actual buttons
		 * (leftbar.xml)
		 */
		home = (Button) findViewById(R.id.homeSlide);
		schedule = (Button) findViewById(R.id.schemeSlide);
		food = (Button) findViewById(R.id.foodSlide);
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

		bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setBackgroundDrawable(new ColorDrawable(0xff222222));
		colorlist = getResources().getStringArray(R.array.colors);
		SetUpLeftBar();
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

		// if user clicks on schedule button

		schedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeScheme();
			}
		});

		// if user clicks on food menu button

		food.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeFood();
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

	private void SetUpLeftBar() {

		mDrawerToggle = new ActionBarDrawerToggle(
				this, mDrawerLayout, /* DrawerLayout object */
				R.drawable.ic_navigation_drawer, /*
										 * nav drawer image to replace 'Up'
										 * caret
										 */
				R.string.app_name, /*
									 * "open drawer" description for
									 * accessibility
									 */
				R.string.app_name /* "close drawer" description for accessibility */
		);
		mDrawerLayout.post(new Runnable() {

			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);

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
		Home.this.invalidateOptionsMenu();
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
		Home.this.invalidateOptionsMenu();
		prepWeb();
	}

	private void makeFood() {
		if (current != HomePage.Mat) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Mat;
			checkColors();
		}
		Home.this.invalidateOptionsMenu();
		prepWeb();
	}

	private void makeNews() {
		if (current != HomePage.Nyheter) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.news, null));
			current = HomePage.Nyheter;
			checkColors();
		}
		Home.this.invalidateOptionsMenu();
		prepFeed();
	}

	private void makeNewsSub() {
		if (current != HomePage.Nyheter_SUB) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.detailview, null));
			current = HomePage.Nyheter_SUB;
			checkColors();

			ImageView Pic = (ImageView) findViewById(R.id.pic);
			Button but = (Button) findViewById(R.id.button);
			TextView rubrik = (TextView) findViewById(R.id.head);
			TextView info = (TextView) findViewById(R.id.info);
			TextView desc = (TextView) findViewById(R.id.desc);
			RelativeLayout separator = (RelativeLayout) findViewById(R.id.separator);

			rubrik.setText(listData[0].headline);
			info.setText(listData[0].description);
			desc.setText(listData[0].longDescription);

			String handler = String.valueOf(Html.fromHtml(listData[0].handler))
					.toUpperCase(Locale.ENGLISH);

			if (handler.contains(Html.fromHtml(Posterlist.Ename).toString()
					.toUpperCase(Locale.ENGLISH))) {
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.elevkaren));
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.Ecolor));
			} else if (handler.contains(Html.fromHtml(Posterlist.PRname)
					.toString().toUpperCase(Locale.ENGLISH))) {
				Pic.setImageDrawable(getResources().getDrawable(R.drawable.pr));
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.PRcolor));

			} else if (handler.contains(Html.fromHtml(Posterlist.Fname)
					.toString().toUpperCase(Locale.ENGLISH))) {
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.festare));
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.Fcolor));

			} else if (handler.contains(Html.fromHtml(Posterlist.spexname)
					.toString().toUpperCase(Locale.ENGLISH))) {
				Pic.setImageDrawable(getResources()
						.getDrawable(R.drawable.spex));
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.spexcolor));

			} else if (handler.contains(Html.fromHtml(Posterlist.IFname)
					.toString().toUpperCase(Locale.ENGLISH))) {
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.skolif));
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.IFcolor));

			} else if (handler.contains(Html.fromHtml(Posterlist.ITname)
					.toString().toUpperCase(Locale.ENGLISH))) {
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.alleit));
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.ITcolor));

			} else if (handler.contains(Html.fromHtml(Posterlist.Skolaname)
					.toString().toUpperCase(Locale.ENGLISH))) {
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.gymnasiet));
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.Skolacolor));

			} else {
				Toast.makeText(
						getApplicationContext(),
						Html.fromHtml("Ingen ansvarig nyhetspublicerare kunde hittas"),
						Toast.LENGTH_SHORT).show();
			}

			but.setText("Mer info");

			String url = listData[0].butURL;

			if (url.length() == 0) {
				but.setVisibility(View.INVISIBLE);
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
		Home.this.invalidateOptionsMenu();
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
					makeKarSub(StudentAssembly.allefestare);
				}

			});
			spexbut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					makeKarSub(StudentAssembly.alleSpex);
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
					makeKarSub(StudentAssembly.alleIT);
				}

			});

		}
		Home.this.invalidateOptionsMenu();
	}

	private void makeKarSub(StudentAssembly SA) {
		if (current != HomePage.Elevkaren_SUB) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.detailview, null));
			current = HomePage.Elevkaren_SUB;
			checkColors();

			ImageView Pic = (ImageView) findViewById(R.id.pic);
			Button but = (Button) findViewById(R.id.button);
			TextView rubrik = (TextView) findViewById(R.id.head);
			TextView info = (TextView) findViewById(R.id.info);
			TextView desc = (TextView) findViewById(R.id.desc);
			RelativeLayout separator = (RelativeLayout) findViewById(R.id.separator);

			but.setText("Besök Facebooksidan");

			String url = "http://facebook.com/";

			// Describṕtions picked up from facebook
			// XXX: These need to be updated or kept up to date in some way.
			switch (SA) {
			case Styrelsen:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.elevkaren));
				rubrik.setText(Html.fromHtml(Posterlist.Ename));
				info.setText("");
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.Ecolor));
				desc.setText(Html.fromHtml(Posterlist.Edesc));
				url += Webber.theboard;
				break;
			case PR:
				Pic.setImageDrawable(getResources().getDrawable(R.drawable.pr));
				rubrik.setText(Html.fromHtml(Posterlist.PRname));
				info.setText("");
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.PRcolor));
				desc.setText(Html.fromHtml(Posterlist.PRdesc));
				url += Webber.PR;
				break;
			case allefestare:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.festare));
				rubrik.setText(Html.fromHtml(Posterlist.Fname));
				info.setText("");
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.Fcolor));
				desc.setText(Html.fromHtml(Posterlist.Fdesc));
				url += Webber.festare;
				break;
			case alleSpex:
				Pic.setImageDrawable(getResources()
						.getDrawable(R.drawable.spex));
				rubrik.setText(Html.fromHtml(Posterlist.spexname));
				info.setText("");
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.spexcolor));
				desc.setText(Html.fromHtml(Posterlist.spexdesc));
				url += Webber.spex;
				break;
			case SkolIF:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.skolif));
				rubrik.setText(Html.fromHtml(Posterlist.IFname));
				info.setText("");
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.IFcolor));
				desc.setText(Html.fromHtml(Posterlist.IFdesc));
				url += Webber.skolif;
				break;
			case alleIT:
				Pic.setImageDrawable(getResources().getDrawable(
						R.drawable.alleit));
				rubrik.setText(Html.fromHtml(Posterlist.ITname));
				info.setText("");
				separator.setBackgroundColor(Color
						.parseColor(Posterlist.ITcolor));
				desc.setText(Html.fromHtml(Posterlist.ITdesc));
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
		Home.this.invalidateOptionsMenu();
	}

	private void makeDexter() {
		if (current != HomePage.Dexter) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Dexter;
			checkColors();
		}
		Home.this.invalidateOptionsMenu();
		prepWeb();
	}

	private void makeItsLearning() {
		if (current != HomePage.ItsLearning) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.ItsLearning;
			checkColors();
		}
		Home.this.invalidateOptionsMenu();
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

					List<NewsInfo> temp = Arrays.asList(Webber
							.getTinyNewsFeed());

					if (temp.isEmpty()) {
						listData = new NewsInfo[0];
					} else {
						int i = 0;
						listData = new NewsInfo[temp.size()];
						for (NewsInfo NI : temp) {
							listData[i] = NI;
							i++;
						}
					}

					runOnUiThread(new Runnable() {
						public void run() {
							if (listData.length == 0) {
								listData = new NewsInfo[1];
								listData[0] = new NewsInfo();
								listData[0].headline = "Inga nyheter hittades";
								listData[0].contentType = -1;
							}
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
						makeNews();
					} else {
						if (listData[position].contentType != -1)
							ShowSpecNews(listData[position].uniqeIdentifier);
					}
				}
			});
		} else if (current == HomePage.Nyheter) {
			final ListView listView = (ListView) a.findViewById(R.id.feed);
			new Thread(new Runnable() {

				@Override
				public void run() {

					List<NewsInfo> temp = Arrays.asList(Webber.getNews("",
							Mode.All));

					if (temp.isEmpty()) {
						listData = new NewsInfo[0];
					} else {
						int i = 0;
						listData = new NewsInfo[temp.size()];
						for (NewsInfo NI : temp) {
							listData[i] = NI;
							i++;
						}
					}

					runOnUiThread(new Runnable() {
						public void run() {
							if (listData.length == 0) {
								listData = new NewsInfo[1];
								listData[0] = new NewsInfo();
								listData[0].headline = "Inga nyheter hittades";
								listData[0].contentType = -1;
							}
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
					if (listData[position].contentType == 0) {
						ShowSpecNews(listData[position].uniqeIdentifier);
					}
				}
			});

		}

	}

	private void ShowSpecNews(final String uniqeIdentifier) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				List<NewsInfo> temp = Arrays.asList(Webber.getNews(
						uniqeIdentifier, Mode.Specific));

				if (temp.isEmpty()) {
					listData = new NewsInfo[0];
				} else {
					int i = 0;
					listData = new NewsInfo[temp.size()];
					for (NewsInfo NI : temp) {
						listData[i] = NI;
						i++;
					}
				}

				runOnUiThread(new Runnable() {
					public void run() {
						if (listData.length > 0) {
							makeNewsSub();
						} else {
							Toast.makeText(
									getApplicationContext(),
									Html.fromHtml("Nyheten kunde inte hittas."),
									Toast.LENGTH_SHORT).show();
							prepFeed();
						}
					}
				});

			}

		}).start();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void checkColors() {

		currcolor = new ColorDrawable(Color.TRANSPARENT);

		// Reset the colors of all the buttons in the sidebar

		LinearLayout container = (LinearLayout) findViewById(R.id.slideContainer);
		for (int i = 0; i < (container).getChildCount(); i++) {
			Button b = (Button) container.getChildAt(i);

			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				b.setBackgroundDrawable(currcolor);
			else
				b.setBackground(currcolor);
		}

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

		case Nyheter:
		case Nyheter_SUB:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[4]));

			if (current == HomePage.Nyheter)
				bar.setTitle("Nyheter");
			else
				bar.setTitle(listData[0].headline);

			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				news.setBackgroundDrawable(currcolor);
			else
				news.setBackground(currcolor);
			break;

		case Elevkaren:
		case Elevkaren_SUB:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[5]));
			bar.setTitle("Elevkåren");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				elevkaren.setBackgroundDrawable(currcolor);
			else
				elevkaren.setBackground(currcolor);
			break;

		case Dexter:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[6]));
			bar.setTitle("Dexter");
			if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
				dexter.setBackgroundDrawable(currcolor);
			else
				dexter.setBackground(currcolor);
			break;

		case ItsLearning:
			currcolor = new ColorDrawable(Color.parseColor(colorlist[7]));
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
		if (current == HomePage.Home || current == HomePage.Schema)
			WV.setVerticalScrollBarEnabled(true);
		else
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
							chosenDay, showThisWeek, xy, isPlayingSport, c);
					runOnUiThread(new Runnable() {
						public void run() {
							WV.getSettings().setUseWideViewPort(true);
							WV.getSettings().setLoadWithOverviewMode(true);
							WV.getSettings().setLayoutAlgorithm(
									LayoutAlgorithm.SINGLE_COLUMN);
							WV.getSettings().setBuiltInZoomControls(true);
							try {
								WV.getSettings().setDisplayZoomControls(false);
							} catch (Exception e) {
							}
							WV.getSettings().setSupportZoom(true);
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
		mDrawerLayout.closeDrawers();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		if (current == HomePage.Home || current == HomePage.Schema)
			menu.findItem(R.id.chng_user).setVisible(true);
		else
			menu.findItem(R.id.chng_user).setVisible(false);

		if (current == HomePage.Schema) {
			menu.findItem(R.id.WeekDayContainer).setVisible(true);

			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
						&& cal.get(Calendar.HOUR_OF_DAY) > 16
						&& !sharedP.getBoolean(
								PreferenceInfo.Extended_Schedule_Display_Name,
								false)
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
			menu.findItem(R.id.openInBrowser).setVisible(true);
		} else {
			menu.findItem(R.id.openInBrowser).setVisible(false);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
          }
		switch (item.getItemId()) {

		// Change user = Home and Schedule
		case R.id.chng_user:
			AlertDialog.Builder chng_user_notice = new AlertDialog.Builder(c);

			chng_user_notice.setMessage(c.getString(R.string.chng_user_notice));
			chng_user_notice.setTitle("Byta anv\u00E4ndare?");
			chng_user_notice.setPositiveButton(
					c.getString(android.R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							sharedP.edit().remove(PreferenceInfo.Pers_Num_Name)
									.commit();

							AlertDialog.Builder restart_app_notice = new AlertDialog.Builder(
									c);

							restart_app_notice.setMessage(c
									.getString(R.string.restart_app_notice));
							restart_app_notice
									.setTitle("Appen m\u00E5ste startas om");
							restart_app_notice.setPositiveButton(
									c.getString(android.R.string.ok), null);
							restart_app_notice.setCancelable(false);
							restart_app_notice.create().show();
						}
					});
			chng_user_notice.setNegativeButton(
					c.getString(android.R.string.no), null);
			chng_user_notice.setCancelable(false);
			chng_user_notice.create().show();
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

		case R.id.openInBrowser:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(mWebView.getOriginalUrl()));
			startActivity(browserIntent);
			return true;

		case R.id.beMember:
			// Open the signup page in the default browser.
			Intent myIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Webber.signupAddress));
			startActivity(myIntent);
			return true;

		case R.id.contactKaren:
			sendEmail(Posterlist.Eemail);
			return true;

		case R.id.contactpr:
			sendEmail(Posterlist.PRemail);
			return true;

		case R.id.contactFestare:
			sendEmail(Posterlist.Femail);
			return true;

		case R.id.contactSpex:
			sendEmail(Posterlist.spexemail);
			return true;

		case R.id.contactSkolIF:
			sendEmail(Posterlist.IFemail);
			return true;

		case R.id.contactIT:
			sendEmail(Posterlist.ITemail);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mDrawerLayout.openDrawer(leftBar);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerVisible(leftBar)) {
			mDrawerLayout.closeDrawers();
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
		} else if (current == HomePage.Nyheter_SUB) {
			makeNews();
		} else {
			if (current != HomePage.Home) {
				makeHome();
			} else {
				finish();
			}

		}
	}

}