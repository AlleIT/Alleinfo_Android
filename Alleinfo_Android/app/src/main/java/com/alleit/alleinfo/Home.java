package com.alleit.alleinfo;

import android.annotation.SuppressLint;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alleit.Alleinfo_Android.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Home extends ActionBarActivity {

	//<editor-fold> Variables

	private Drawable currcolor;

	private TextView todDay, todDate, todWeek;

	// Colors for the sidebar buttons. Expand in strings.xml as necessary.
	private ColorDrawable[] colorlist;

	// The user's location
	private HomePage current = HomePage.Start;

	private ActionBar bar;

	private ActionBarDrawerToggle mDrawerToggle;
	private ViewGroup viewGroup;

	private int chosenDay = -1;

	private Boolean showThisWeek = true;
	private Point xy;
	private Context c;
	private WebView mWebView;
	private ListView leftBar;
	private DrawerLayout mDrawerLayout;
	private String number = null;
	private SharedPreferences sharedP;
	private String pin = null;
	private NewsData[] newsListData;
	private PosterData[] posterListData;
	private Boolean isPlayingSport;

	private LeftBarAdapter leftBarAdapter;

	private static final int home_index = 0, schedule_index = 1, food_index = 2, news_index = 3, kar_index = 4, dexter_index = 5, itslearning_index = 6;

	//</editor-fold>

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

		if (!fromIntent.getBooleanExtra("Stored", false)) {
			isPlayingSport = fromIntent.getBooleanExtra(
					PreferenceInfo.Extended_Schedule_Display_Name, false);
		} else {
			isPlayingSport = sharedP.getBoolean(
					PreferenceInfo.Extended_Schedule_Display_Name, false);
		}

		c = this;

		viewGroup = (ViewGroup) findViewById(R.id.content);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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

		colorlist = setUpColorList();
		SetUpLeftBar();
		makeHome();
	}

	private void SetUpLeftBar() {

		String[] menuEntries = getResources().getStringArray(R.array.drawer_navigation);

		leftBar = (ListView) findViewById(R.id.leftBar);

		leftBarAdapter = new LeftBarAdapter(this, R.layout.leftbar_entry, R.id.action_text, menuEntries);

		leftBar.setAdapter(leftBarAdapter);

		leftBar.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
					default:
					case home_index:
						makeHome();
						break;
					case schedule_index:
						makeScheme();
						break;
					case food_index:
						makeFood();
						break;
					case news_index:
						makeNews();
						break;
					case kar_index:
						makeKar();
						break;
					case dexter_index:
						makeDexter();
						break;
					case itslearning_index:
						makeItsLearning();
						break;
				}
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
		mDrawerLayout.post(new Runnable() {

			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	private ColorDrawable[] setUpColorList() {
		return new ColorDrawable[]{
				new ColorDrawable(getResources().getColor(R.color.col_green)),
				new ColorDrawable(getResources().getColor(R.color.col_cyan)),
				new ColorDrawable(getResources().getColor(R.color.col_dk_yellow)),
				new ColorDrawable(getResources().getColor(R.color.col_magenta)),
				new ColorDrawable(getResources().getColor(R.color.col_karbla)),
				new ColorDrawable(getResources().getColor(R.color.col_red)),
				new ColorDrawable(getResources().getColor(R.color.col_orange))
		};
	}

	// <editor-fold>: makeXxx() Prepare the various pages here. Keep the methods in the same order as the actual buttons please.

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
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void makeNews() {
		if (current != HomePage.Nyheter) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.list, null));
			current = HomePage.Nyheter;
			checkColors();
			prepFeed();
		}
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeNewsSub(final NewsData data) {
		if (current != HomePage.Nyheter_SUB) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.detailview, null));
			current = HomePage.Nyheter_SUB;

			checkColors();

			currcolor = new ColorDrawable(Color.parseColor(data.color));
			handlePageChange(data.headline, news_index);

			new Thread(new Runnable() {

				@Override
				public void run() {
					final ProgressBar imageLoadBar = (ProgressBar) findViewById(R.id.ImageLoadPB);
					imageLoadBar.setVisibility(View.VISIBLE);
					final Drawable image = data.Image(c);
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								Animation fadeInTransition = AnimationUtils.loadAnimation(c, R.anim.fadein);
								ImageView Pic = (ImageView) findViewById(R.id.pic);
								Pic.setImageDrawable(image);
								Pic.startAnimation(fadeInTransition);
								imageLoadBar.setVisibility(View.GONE);
							} catch (Exception e) {
								// The user probably exited the news screen before the image was loaded. Don't worry about it.
							}
						}
					});
				}
			}).start();

			Button but = (Button) findViewById(R.id.button);
			TextView info = (TextView) findViewById(R.id.info);
			TextView desc = (TextView) findViewById(R.id.desc);
			RelativeLayout separator = (RelativeLayout) findViewById(R.id.separator);
			RelativeLayout top = (RelativeLayout) findViewById(R.id.top);

			info.setText(data.shortInfo);
			desc.setText(data.description);

			top.setBackgroundColor(Color.parseColor(data.color));

			separator.setBackgroundColor(Color.parseColor(data.color));

			but.setText("Mer info");

			String url = data.butURL;

			if (url.length() == 0) {
				but.setVisibility(View.GONE); // Important that this is set to View.GONE in order for the "kort info"-scrolling text to work
			}

			final String finURL = url;

			but.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// Open the facebook page in the default browser.
					try {
						Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri
								.parse(finURL));
						startActivity(myIntent);

					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(),
								"Sidan kunde inte laddas", Toast.LENGTH_SHORT)
								.show();
					}
				}

			});

		}
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeKar() {
		if (current != HomePage.Elevkaren) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.list, null));
			HomePage lastPage = current;
			current = HomePage.Elevkaren;
			checkColors();

			final Activity a = this;
			final ProgressBar LoadEntriesBar = (ProgressBar) findViewById(R.id.listProgBar);
			final ListView elevkarListView = (ListView) findViewById(R.id.feed);

			if (lastPage != HomePage.Elevkaren_SUB) {
				LoadEntriesBar.setVisibility(View.VISIBLE);

				new Thread(new Runnable() {

					@Override
					public void run() {
						final PosterData[] temp = Webber.getKarInfo(c);

						runOnUiThread(new Runnable() {
							public void run() {
								if (temp.length == 0) {
									posterListData = new PosterData[1];
									posterListData[0] = new PosterData();
									posterListData[0].name = "Fel :'(";
									posterListData[0].color = "#000000";
									posterListData[0].logo = c
											.getResources()
											.getDrawable(
													android.R.drawable.ic_menu_report_image);
								} else {
									posterListData = temp;
								}
								elevkarListView.setDividerHeight((int) TypedValue
										.applyDimension(
												TypedValue.COMPLEX_UNIT_DIP, 0,
												getResources()
														.getDisplayMetrics()));

								ElevkarEntriesAdapter itemAdapter = new ElevkarEntriesAdapter(
										a, R.layout.elevkar_entry,
										posterListData);
								elevkarListView.setAdapter(itemAdapter);
								LoadEntriesBar.setVisibility(View.INVISIBLE);
							}
						});

					}

				}).start();
			} else {
				elevkarListView.setDividerHeight((int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
								getResources().getDisplayMetrics()));

				ElevkarEntriesAdapter itemAdapter = new ElevkarEntriesAdapter(
						a, R.layout.elevkar_entry, posterListData);
				elevkarListView.setAdapter(itemAdapter);
				LoadEntriesBar.setVisibility(View.INVISIBLE);

			}

			elevkarListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
				                        int position, long id) {
					if (posterListData.length <= 1)
						return;

					makeKarSub(position);
				}
			});
		}
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeKarSub(int position) {
		if (current != HomePage.Elevkaren_SUB) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.detailview, null));
			current = HomePage.Elevkaren_SUB;

			checkColors();

			currcolor = new ColorDrawable(Color.parseColor(posterListData[position].color));
			handlePageChange(posterListData[position].name, kar_index);

			final String finURL = posterListData[position].socialLink;

			ImageView Pic = (ImageView) findViewById(R.id.pic);
			Button but = (Button) findViewById(R.id.button);
			TextView infoHead = (TextView) findViewById(R.id.shortInfoText);
			TextView info = (TextView) findViewById(R.id.info);
			TextView desc = (TextView) findViewById(R.id.desc);
			RelativeLayout separator = (RelativeLayout) findViewById(R.id.separator);
			RelativeLayout top = (RelativeLayout) findViewById(R.id.top);

			infoHead.setVisibility(View.INVISIBLE);
			info.setText("");

			Pic.setImageDrawable(posterListData[position].logo);

			desc.setText(posterListData[position].description);

			separator.setBackgroundColor(Color
					.parseColor(posterListData[position].color));

			top.setBackgroundColor(Color.parseColor(posterListData[position].color));

			but.setText("Sociala medier");
			but.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(finURL));
					startActivity(myIntent);

				}

			});

		}
		Home.this.supportInvalidateOptionsMenu();
	}

	private void makeDexter() {
		if (current != HomePage.Dexter) {
			viewGroup.removeAllViews();
			viewGroup.addView(View.inflate(c, R.layout.webber, null));
			current = HomePage.Dexter;
			checkColors();
		}
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
		Home.this.supportInvalidateOptionsMenu();
		prepWeb();
	}

	private void prepFeed() {
		final Activity a = this;

		final ProgressBar LoadEntriesBar = (ProgressBar) findViewById(R.id.listProgBar);
		final ListView listView = (ListView) a.findViewById(R.id.feed);
		LoadEntriesBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {

				List<NewsData> temp = Arrays.asList(Webber.getNews());

				if (temp.isEmpty()) {
					newsListData = new NewsData[0];
				} else {
					int i = 0;
					newsListData = new NewsData[temp.size()];
					for (NewsData NI : temp) {
						newsListData[i] = NI;
						i++;
					}
				}

				runOnUiThread(new Runnable() {
					public void run() {
						if (newsListData.length == 0) {
							newsListData = new NewsData[1];
							newsListData[0] = new NewsData();
							newsListData[0].headline = "Inga nyheter hittades";
							newsListData[0].contentType = ContentType.NoNews;
							newsListData[0].color = "#000000";
						}
						NewsFeedAdapter itemAdapter = new NewsFeedAdapter(
								a, R.layout.newsroll, newsListData);
						listView.setAdapter(itemAdapter);
						LoadEntriesBar.setVisibility(View.INVISIBLE);
					}
				});

			}

		}).start();

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
			                        int position, long id) {
				if (newsListData[position].contentType == ContentType.News) {
					makeNewsSub(newsListData[position]);
				}
			}
		});

	}
	// </editor-fold>

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void checkColors() {

		switch (current) {

			case Home:
				currcolor = colorlist[home_index];
				handlePageChange(getString(R.string.home), home_index);
				break;

			case Schema:
				currcolor = colorlist[schedule_index];
				handlePageChange(getString(R.string.scheme), schedule_index);
				break;

			case Mat:
				currcolor = colorlist[food_index];
				handlePageChange(getString(R.string.food), food_index);
				break;

			case Nyheter:
				currcolor = colorlist[news_index];
				handlePageChange(getString(R.string.news), news_index);
				break;

			case Nyheter_SUB:
			case Elevkaren_SUB:
				break;

			case Elevkaren:
				currcolor = colorlist[kar_index];
				handlePageChange(getString(R.string.elevkar), kar_index);
				break;

			case Dexter:
				currcolor = colorlist[dexter_index];
				handlePageChange(getString(R.string.dexter), dexter_index);
				break;

			case ItsLearning:
				currcolor = colorlist[itslearning_index];
				handlePageChange(getString(R.string.itslearning), itslearning_index);
				break;

			default:
				current = HomePage.Home;
				currcolor = colorlist[home_index];
				handlePageChange(getString(R.string.home), home_index);
				break;
		}
	}

	private void handlePageChange(String title, int index) {
		bar.setTitle(title);

		leftBarAdapter.selectedPos = index;
		leftBarAdapter.notifyDataSetChanged();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ColorDrawable statusBarColor = (ColorDrawable) currcolor;
			float[] hsv = new float[3];
			Color.colorToHSV(statusBarColor.getColor(), hsv);
			hsv[2] *= .8;
			getWindow().setStatusBarColor(Color.HSVToColor(hsv));
		}

		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
			leftBar.setBackgroundDrawable(currcolor);
		else
			leftBar.setBackground(currcolor);

		// Not depreciated..?
		// DON'T TOUCH THIS LINE. IT JUST WORKS. LEAVE IT!
		// {
		bar.setBackgroundDrawable(new ColorDrawable(((ColorDrawable) currcolor).getColor()));
		// }
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({"SetJavaScriptEnabled", "NewApi"})
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
				if (current != HomePage.Schema) {
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
				if (PB.isShown() && !error) {
					PB.setVisibility(View.INVISIBLE);
					progress.setVisibility(View.INVISIBLE);
					mWebView = WV;
				}
			}

			// if SSL certificate doesn't work, don't show any error

			public void onReceivedSslError(WebView view,
			                               SslErrorHandler handler, SslError error) {
				System.out.println("SSL Error. Continuing...");
				handler.proceed();
			}

			public void onReceivedError(WebView view, int errorCode,
			                            String description, String failingUrl) {
				lasturl = failingUrl;
				System.out.println("Failed with EC: " + String.valueOf(errorCode) + "\nDescription: " + description + "\nURL:" + failingUrl);
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

				if (connected) {
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
							WV.getSettings().setUseWideViewPort(false);
							WV.getSettings().setLoadWithOverviewMode(true);
							WV.getSettings().setLayoutAlgorithm(
									LayoutAlgorithm.NORMAL);
							WV.setInitialScale(100);
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
			WV.getSettings().setUseWideViewPort(true);
			WV.getSettings().setLoadWithOverviewMode(true);
			WV.getSettings().setLayoutAlgorithm(
					LayoutAlgorithm.NORMAL);
			WV.setInitialScale(100);
			WV.getSettings().setBuiltInZoomControls(false);
			WV.getSettings().setSupportZoom(false);

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

	// <editor-fold> Menu stuff
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mDrawerLayout.closeDrawers();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		if (current == HomePage.Home)
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

		if (current == HomePage.Elevkaren || current == HomePage.Elevkaren_SUB)
			menu.findItem(R.id.beMember).setVisible(true);
		else
			menu.findItem(R.id.beMember).setVisible(false);

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
										.apply();

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
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				}
				return true;

			case R.id.goForward:
				if (mWebView.canGoForward()) {
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

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//</editor-fold>

	//<editor-fold> Button press handlers
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
	//</editor-fold>

}