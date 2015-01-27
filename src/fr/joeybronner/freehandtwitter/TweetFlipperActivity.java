package fr.joeybronner.freehandtwitter;

import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import fr.joeybronner.freehandtwitter.util.Constants;

public class TweetFlipperActivity extends Activity {

	private ViewFlipper viewFlipper;
	//private float lastX;
	private static final int SLIDER_TIMER = 6000; 
	String search;
	int i = 0;
	boolean isPaused = false;
	boolean isDark;
	ImageView btPlayPause, ivUser, btTweetNext, btTweetBack, btShare;
	TextView tvTweet, tvArobase, tvName;
	View v;
	Bitmap bm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_flipper);
		getActionBar().hide();

		if (Constants.twit.isEmpty()) {
			finish();
		}

		// Stay screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Typeface
		Constants.tf = Typeface.createFromAsset(this.getAssets(),"fonts/OpenSans-Light.ttf");

		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		viewFlipper.setFlipInterval(SLIDER_TIMER);
		viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
		viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
		viewFlipper.startFlipping();

		tvTweet = (TextView) findViewById(R.id.tvTweetContent);
		tvTweet.setTypeface(Constants.tf);
		tvArobase = (TextView) findViewById(R.id.textView1);
		tvArobase.setTypeface(Constants.tf);
		tvName = (TextView) findViewById(R.id.tvName);
		tvName.setTypeface(Constants.tf);
		ivUser = (ImageView) findViewById(R.id.ivUser);
		btPlayPause = (ImageView) findViewById(R.id.btTweetPlayPause);
		btTweetNext = (ImageView) findViewById(R.id.btTweetNext);
		btTweetBack = (ImageView) findViewById(R.id.btTweetBack);
		btShare = (ImageView) findViewById(R.id.btShare);
		final Handler handler = new Handler();
		final Runnable r = new Runnable()
		{
			public void run() 
			{
				if (i == Constants.twit.size()) {
					i = 0;
				}
				tvTweet.setText(Constants.twit.get(i).toString());
				tvArobase.setText("@" + Constants.twit.get(i).getTwitterUser().getScreenName());
				tvName.setText(Constants.twit.get(i).getTwitterUser().getName());
				setBackgroundColor();
				setFontColor();
				new ImageDownloader(ivUser).execute(Constants.twit.get(i).getTwitterUser().getProfileImageUrl());
				handler.postDelayed(this, SLIDER_TIMER);
				viewFlipper.setInAnimation(TweetFlipperActivity.this, R.anim.slide_in_from_right);
				viewFlipper.setOutAnimation(TweetFlipperActivity.this, R.anim.slide_out_to_left);
				i++;
			}
		}; 
		handler.postDelayed(r, 0);

		btPlayPause.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) { 
				if(isPaused) {
					if (isDark) {
						bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetpause),800, 800, true);
					} else {
						bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetpause_dark),800, 800, true);
					}
					btPlayPause.setImageBitmap(bm);
					isPaused = false;
					handler.postDelayed(r, 0);
					viewFlipper.startFlipping();
				}
				else {
					if (isDark) {
						bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay),800, 800, true);
					} else {
						bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay_dark),800, 800, true);
					}
					btPlayPause.setImageBitmap(bm);
					isPaused = true;
					handler.removeCallbacks(r);
					viewFlipper.stopFlipping();
				}
			}
		});

		btTweetNext.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				handler.removeCallbacks(r);
				viewFlipper.stopFlipping();
				handler.postDelayed(r, 0);
				viewFlipper.startFlipping();
				if (isDark) {
					bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay),800, 800, true);
				} else {
					bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay_dark),800, 800, true);
				}
				btPlayPause.setImageBitmap(bm);
				isPaused = false;
			}
		});
		
		btTweetBack.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				i = i-2;
				handler.removeCallbacks(r);
				viewFlipper.stopFlipping();
				viewFlipper.setInAnimation(TweetFlipperActivity.this, R.anim.slide_in_from_left);
				viewFlipper.setOutAnimation(TweetFlipperActivity.this, R.anim.slide_out_to_right);
				handler.postDelayed(r, 0);
				viewFlipper.startFlipping();
				if (isDark) {
					bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay),800, 800, true);
				} else {
					bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay_dark),800, 800, true);
				}
				btPlayPause.setImageBitmap(bm);
				isPaused = false;
			}
		});
	}

	private void setBackgroundColor() {
		String fontColor = "#" + Constants.twit.get(i).getTwitterUser().getProfileBackgroundColor();
		getWindow().getDecorView().setBackgroundColor(Color.parseColor(fontColor));
	}

	private void setFontColor() {
		int lum = getBrightness(Color.parseColor("#" + Constants.twit.get(i).getTwitterUser().getProfileBackgroundColor()));
		if (lum > 150)
		{
			isDark = false;
			tvArobase.setTextColor(getResources().getColor(R.color.darkgray));
			tvTweet.setTextColor(getResources().getColor(R.color.darkgray));
			tvName.setTextColor(getResources().getColor(R.color.darkgray));
			if (isPaused) {
				bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay_dark),800, 800, true);
			} else {
				bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetpause_dark),800, 800, true);
			}
			btPlayPause.setImageBitmap(bm);
			bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetnext_dark),800, 800, true);
			btTweetNext.setImageBitmap(bm);
			bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetback_dark),800, 800, true);
			btTweetBack.setImageBitmap(bm);
			bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_dark),800, 800, true);
			btShare.setImageBitmap(bm);
		}
		else
		{	
			isDark = true;
			tvArobase.setTextColor(getResources().getColor(R.color.white));
			tvTweet.setTextColor(getResources().getColor(R.color.white));
			tvName.setTextColor(getResources().getColor(R.color.white));
			if (isPaused) {
				bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetplay),800, 800, true);
			} else {
				bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetpause),800, 800, true);
			}
			btPlayPause.setImageBitmap(bm);
			bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetnext),800, 800, true);
			btTweetNext.setImageBitmap(bm);
			bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tweetback),800, 800, true);
			btTweetBack.setImageBitmap(bm);
			bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share),800, 800, true);
			btShare.setImageBitmap(bm);
		}
	}

	public static int getBrightness(int argb)
	{
		int lum= (   77  * ((argb>>16)&255) 
				+ 150 * ((argb>>8)&255) 
				+ 29  * ((argb)&255))>>8;
				return lum;
	}
}

class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
	ImageView bmImage;

	public ImageDownloader(ImageView bmImage) {
		this.bmImage = bmImage;
	}

	protected Bitmap doInBackground(String... urls) {
		String url = urls[0];
		Bitmap mIcon = null;
		try {
			InputStream in = new java.net.URL(url).openStream();
			mIcon = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
		}
		return mIcon;
	}

	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}
}

