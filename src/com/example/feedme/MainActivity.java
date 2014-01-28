package com.example.feedme;

import java.io.IOException;
import java.io.InputStream;
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
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	ArrayList<String> headlines;
	ArrayList<String> links;
	ArrayList<String> imageURL;
	Activity mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Initializing instance variables
		headlines = new ArrayList<String>();
		links = new ArrayList<String>();
		imageURL = new ArrayList<String>();
		mActivity = this;
		DownloadRssTask d = new DownloadRssTask();
		d.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public InputStream getInputStream(URL url) {
		   try {
		       return url.openConnection().getInputStream();
		   } catch (IOException e) {
		       return null;
		     }
		}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	   Uri uri = Uri.parse((String) links.get(position));
	   Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	   startActivity(intent);
	}
	
	private class DownloadRssTask extends AsyncTask<String, Void, String> {
	    @Override
	    protected String doInBackground(String... urls) {
	    	try {
			    //URL url = new URL("http://www.bbc.co.uk/blogs/food/rss.xml");
			    URL url = new URL("http://www.update66.com/rss/1.xml");
			    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			    factory.setNamespaceAware(false);
			    XmlPullParser xpp = factory.newPullParser();
			 
			        // We will get the XML from an input stream
			    xpp.setInput(getInputStream(url), "UTF_8");
			 
			        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
			         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
			         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
			         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
			         * and take in consideration only "<title>" tag which is a child of "<item>"
			         *
			         * In order to achieve this, we will make use of a boolean variable.
			         */
			    boolean insideItem = false;
			 
			        // Returns the type of current event: START_TAG, END_TAG, etc..
			    int eventType = xpp.getEventType();
			    while (eventType != XmlPullParser.END_DOCUMENT) {
			        if (eventType == XmlPullParser.START_TAG) {
			 
			            if (xpp.getName().equalsIgnoreCase("item")||
			            		xpp.getName().equalsIgnoreCase("entry")) {
			                insideItem = true;
			            } else if (xpp.getName().equalsIgnoreCase("title")) {
			                if (insideItem)
			                    headlines.add(xpp.nextText()); //extract the headline
			            } else if (xpp.getName().equalsIgnoreCase("link")) {
			                if (insideItem)
			                    links.add(xpp.nextText()); //extract the link of article
			            }
			            else if(xpp.getName().equalsIgnoreCase("description")||
			            		xpp.getName().equalsIgnoreCase("content")){
			            	 if (insideItem)
			            	imageURL.add(renderHtml(xpp.nextText()));
			            }
			        }else if(eventType==XmlPullParser.END_TAG && (xpp.getName().equalsIgnoreCase("item")||
			        		xpp.getName().equalsIgnoreCase("entry"))){
			            insideItem=false;
			        }
			 
			        eventType = xpp.next(); //move to next element
			    }
			 
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
	    	String[] hlArr = headlines.toArray(new String[headlines.size()]);
	    	
	    	String[] imgUrlArr = imageURL.toArray(new String[imageURL.size()]);
	    	Log.v("HL","HEADLINE LENGTH:"+hlArr.length);
	    				FeedsAdapter adapter = new FeedsAdapter(mActivity,
	    						hlArr,imgUrlArr);
	    				 
	    				setListAdapter(adapter);
	    }
	  }

	
	public String renderHtml(String body) {
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = pattern.matcher(body);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        String imgURL="";
        while (matcher.find()) {
//            String replacement = (matcher.group(0));
//            builder.append(body.substring(i, matcher.start()));
//            if (replacement == null)
//                builder.append(matcher.group(0));
//            else
//                builder.append(replacement);
//            i = matcher.end();
        	
        	imgURL = matcher.group(1); // Access a submatch group; String can't do this.
        }
        Log.v("HL","Load image:"+imgURL);
        return imgURL;
//        builder.append(body.substring(i, body.length()));
//        return builder.toString();
    }
	
	} 

