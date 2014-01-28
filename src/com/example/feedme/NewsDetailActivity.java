package com.example.feedme;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		Intent k = getIntent();
		((TextView)findViewById(R.id.TextViewSubject)).setText(k.getStringExtra("headline"));
		((TextView)findViewById(R.id.TextViewDescription)).setText(Html.fromHtml(k.getStringExtra("description")));
		ImageLoader mImageLoader;
		mImageLoader = new ImageLoader(this);
		mImageLoader.DisplayImage(k.getStringExtra("image"),(ImageView)findViewById(R.id.imageView1));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news_detail, menu);
		return true;
	}

}
