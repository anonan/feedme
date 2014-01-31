package com.example.feedme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;
/**
 * Activity list
 * 
 * @author Virus
 * 
 */
public class FeedsAdapter extends BaseAdapter {

	private Vector<String> dataHeadline;
	private Vector<String> dataImage;
	private Vector<String> dataPubDate;
	private static LayoutInflater inflater = null;
	final static String TAG = "FeedsAdapter";
	ImageLoader mImageLoader;
	boolean mShowName;
	int screenWidth,screenHeight;
	Activity mA;
	public FeedsAdapter(Activity a, String[] headLines,String[] images,String[] pubDate) {
		mA = a;
		//data = d;
		dataHeadline = new Vector<String>();
		for(int i=0;i<headLines.length;i++){
			dataHeadline.add(headLines[i]);
		}
		
		dataImage = new Vector<String>();
		for(int i=0;i<images.length;i++){
			dataImage.add(images[i]);
		}
		dataPubDate = new Vector<String>();
		for(int i=0;i<pubDate.length;i++){
			dataPubDate.add(pubDate[i]);
		}
		
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mImageLoader = new ImageLoader(mA);
		
		Display display = a.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		 screenWidth = size.x;
		 screenHeight = size.y;
	}

	public int getCount() {
		
			return dataHeadline.size();
		
	}

	

	@SuppressLint("SimpleDateFormat")
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		String strHL = (String)dataHeadline.get(position);
		Log.v(TAG,"DATAIMAGE :"+dataImage.get(position).toString());
		if(position<dataImage.size()&&!dataImage.get(position).toString().equals("")){
			//Full image view
			if (convertView == null) {
				vi = inflater.inflate(R.layout.list_feed, null);
				vi.setTag("1");
			}
			else{
				if(!vi.getTag().equals("1")){
					vi = inflater.inflate(R.layout.list_feed, null);
					vi.setTag("1");
				}
			}
			TextView headline = (TextView)vi.findViewById(R.id.textView1);
			ImageView imgProfile = (ImageView)vi.findViewById(R.id.imageView1);
			if(position<dataImage.size()&&!dataImage.get(position).toString().equals(""))
			mImageLoader.DisplayImage((String)dataImage.get(position),imgProfile);
			
			int newHeight = screenWidth;
			// double check my math, this should be right, though
			int newWidth = screenWidth;
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
			imgProfile.setLayoutParams(params);
			imgProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
			
			
			headline.setText(strHL);
		}
		else{
			//No image view
			//Full image view
			if (convertView == null) {
				vi = inflater.inflate(R.layout.list_feed_imgtext, null);
				vi.setTag("2");
			}
			else{
				if(!vi.getTag().equals("2")){
					vi = inflater.inflate(R.layout.list_feed_imgtext, null);
					vi.setTag("2");
				}
			}
			
			TextView headline = (TextView)vi.findViewById(R.id.textView1);
			headline.setText(strHL);
			TextView updateAt = (TextView)vi.findViewById(R.id.update);
			updateAt.setText(dataPubDate.get(position).toString());
		
		}
		
		return vi;
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}
}