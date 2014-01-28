package com.example.feedme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	String TAG = "MAIN";
	ArrayList<String> headlines;
	ArrayList<String> links;
	ArrayList<String> imageURL;
	ArrayList<String> descriptions;
	Activity mActivity;
	ListView listNews;
	ListView mDrawerList;
	DrawerLayout mDrawerLayout;
	String mTitle;
	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the
	// action bar
	ActionBarDrawerToggle mDrawerToggle;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTitle = "";
		// Initializing instance variables
		headlines = new ArrayList<String>();
		links = new ArrayList<String>();
		imageURL = new ArrayList<String>();
		descriptions = new ArrayList<String>();
		mActivity = this;
		listNews = (ListView) findViewById(R.id.listnews);

		// Getting reference to the DrawerLayout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);

		// Getting reference to the ActionBarDrawerToggle
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_menu, R.string.app_name,
				R.string.app_name) {

			/** Called when drawer is closed */
			@SuppressLint("NewApi")
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("Update66");
				invalidateOptionsMenu();
			}

			/** Called when a drawer is opened */
			@SuppressLint("NewApi")
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("Select category");
				invalidateOptionsMenu();
			}
		};
		// Setting DrawerToggle on DrawerLayout
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// Creating an ArrayAdapter to add items to the listview mDrawerList
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.drawer_list_item,
				getResources().getStringArray(R.array.rivers));

		// Setting the adapter on mDrawerList
		mDrawerList.setAdapter(adapter);
		// Creating an ArrayAdapter to add items to the listview mDrawerList
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), R.layout.drawer_list_item,
				getResources().getStringArray(R.array.rivers));

		// Setting the adapter on mDrawerList
		mDrawerList.setAdapter(adapter1);
		// Enabling Home button
		getActionBar().setHomeButtonEnabled(true);

		// Enabling Up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Setting item click listener for the listview mDrawerList
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				DownloadRssTask d = new DownloadRssTask();
				d.execute(String.valueOf(position + 1));
				// Closing the drawer
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		});

		DownloadRssTask d = new DownloadRssTask();
		d.execute();
	}

	public InputStream getInputStream(URL url) {
		try {
			return url.openConnection().getInputStream();
		} catch (IOException e) {
			return null;
		}
	}

	private class DownloadRssTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... feed) {
			try {
				String feedCat;
				if (feed == null || feed.length == 0)
					feedCat = "1";
				else
					feedCat = feed[0];
				URL url = new URL("http://www.update66.com/rss/" + feedCat + ".xml");

				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(false);
				XmlPullParser xpp = factory.newPullParser();

				// We will get the XML from an input stream
				xpp.setInput(getInputStream(url), "UTF_8");

				/*
				 * We will parse the XML content looking for the "<title>" tag
				 * which appears inside the "<item>" tag. However, we should
				 * take in consideration that the rss feed name also is enclosed
				 * in a "<title>" tag. As we know, every feed begins with these
				 * lines: "<channel><title>Feed_Name</title>...." so we should
				 * skip the "<title>" tag which is a child of "<channel>" tag,
				 * and take in consideration only "<title>" tag which is a child
				 * of "<item>"
				 * 
				 * In order to achieve this, we will make use of a boolean
				 * variable.
				 */
				boolean insideItem = false;
				headlines.clear();
				links.clear();
				imageURL.clear();
				descriptions.clear();
				extractDatafrommHTML(convertStreamToString(getInputStream(url)));

				// Returns the type of current event: START_TAG, END_TAG, etc..
				// int eventType = xpp.getEventType();
				// while (eventType != XmlPullParser.END_DOCUMENT) {
				// if (eventType == XmlPullParser.START_TAG) {
				//
				// if (xpp.getName().equalsIgnoreCase("item")) {
				// insideItem = true;
				// } else if (xpp.getName().equalsIgnoreCase("title")) {
				// if (insideItem) {
				// // Extract img url from title
				// headlines.add((xpp.nextText())); // extract
				// // the
				// // headline
				// }
				//
				// } else if (xpp.getName().equalsIgnoreCase("link")) {
				// if (insideItem)
				// links.add(xpp.nextText()); // extract the link
				// // of article
				// } else if (xpp.getName().equalsIgnoreCase("description")) {
				// Log.v("DES",xpp.);
				//
				// String des = removeTagfromTitle(xpp.nextText());
				// Log.v("DESS",des);
				// descriptions.add(des);
				// if (insideItem){
				// imageURL.add(renderHtml(des));
				// }
				//
				// }
				// } else if (eventType == XmlPullParser.END_TAG &&
				// xpp.getName().equalsIgnoreCase("item")) {
				// insideItem = false;
				// }
				//
				// eventType = xpp.next(); // move to next element
				// }

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			// Binding data
			final String[] hlArr = headlines.toArray(new String[headlines.size()]);

			final String[] imgUrlArr = imageURL.toArray(new String[imageURL.size()]);
			final String[] desArr = descriptions.toArray(new String[descriptions.size()]);
			final String[] linkArr = links.toArray(new String[links.size()]);

			FeedsAdapter adapter = new FeedsAdapter(mActivity, hlArr, imgUrlArr);
			listNews.setAdapter(null);
			listNews.setAdapter(adapter);
			listNews.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> root, View view, int position, long id) {
					Intent k = new Intent(mActivity, NewsDetailActivity.class);
					k.putExtra("headline", hlArr[position]);
					k.putExtra("image", imgUrlArr[position]);
					k.putExtra("description", desArr[position]);
					// k.putExtra("link", linkArr[position]);
					mActivity.startActivity(k);
				}
			});
		}
	}

	private String removeTagfromTitle(String body) {

		return body.replaceAll("\\<(/?[^\\>]+)\\>", "");

	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	private void extractDatafrommHTML(String body) {
		Pattern pattern = Pattern.compile("<title>(.*)</title>");
		Matcher matcher = pattern.matcher(body);
		
		int i = 0;
		
		while (matcher.find()) {
			if (i == 0) {
				i++;
				continue;
			}
			
			headlines.add(matcher.group(1));
			i++;
		}

		pattern = Pattern.compile("<description>(.*)</description>");

		matcher = pattern.matcher(body);
		i = 0;
		while (matcher.find()) {
			if (i == 0) {
				i++;
				continue;
			}
			descriptions.add((matcher.group(1)));
			i++;
		}
		pattern = Pattern.compile("<image>http(.*)jpg</image>");
		matcher = pattern.matcher(body);
		i = 0;
		while (matcher.find()) {

			imageURL.add("http" + removeTagfromTitle(matcher.group(1)) + "jpg");
			i++;
		}

	}

	public String renderHtml(String body) {
		Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
		Matcher matcher = pattern.matcher(body);
		StringBuilder builder = new StringBuilder();
		int i = 0;
		String imgURL = "";
		while (matcher.find()) {

			imgURL = matcher.group(1); // Access a submatch group; String can't
										// do this.
		}
		Log.v("HL", "Load image:" + imgURL);
		return imgURL;

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	/** Handling the touch event of app icon */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
