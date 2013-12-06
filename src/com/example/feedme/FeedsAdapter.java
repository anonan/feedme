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
import android.util.Log;
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
	private static LayoutInflater inflater = null;
	final static String TAG = "CommentAdapter";
	ImageLoader mImageLoader;
	boolean mShowName;

	Activity mA;
	public FeedsAdapter(Activity a, String[] headLines,String[] images) {
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
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mImageLoader = new ImageLoader(mA);
	}

	public int getCount() {
		
			return dataHeadline.size();
		
	}

	

	@SuppressLint("SimpleDateFormat")
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		String strHL = (String)dataHeadline.get(position);
		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_feed, null);
		}
		TextView headline = (TextView)vi.findViewById(R.id.textView1);
		
		ImageView imgProfile = (ImageView)vi.findViewById(R.id.imageView1);
		if(position<dataImage.size()&&!dataImage.get(position).toString().equals(""))
		mImageLoader.DisplayImage((String)dataImage.get(position),imgProfile);
		
		headline.setText(strHL);
		
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