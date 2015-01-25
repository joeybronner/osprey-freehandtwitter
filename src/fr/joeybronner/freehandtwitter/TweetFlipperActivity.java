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
	ImageView btPlayPause, ivUser, btTweetNext;
	TextView tvTweet, tvArobase;
	View v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_flipper);
		getActionBar().hide();

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
		ivUser = (ImageView) findViewById(R.id.ivUser);
		btPlayPause = (ImageView) findViewById(R.id.btTweetPlayPause);
		btTweetNext = (ImageView) findViewById(R.id.btTweetNext);
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
				setBackgroundColor();
				new ImageDownloader(ivUser).execute(Constants.twit.get(i).getTwitterUser().getProfileImageUrl());
				handler.postDelayed(this, SLIDER_TIMER);
				i++;
			}
		}; 
		handler.postDelayed(r, 0);

		btPlayPause.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) { 
				if(isPaused) {
					btPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.tweetpause));
					isPaused = false;
					handler.postDelayed(r, 0);
					viewFlipper.startFlipping();
				}
				else {
					btPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.tweetplay));
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
			}
		});
	}

	private void setBackgroundColor() {
		String fontColor = "#" + Constants.twit.get(i).getTwitterUser().getProfileBackgroundColor();
		getWindow().getDecorView().setBackgroundColor(Color.parseColor(fontColor));
		int lum = getBrightness(Color.parseColor("#" + Constants.twit.get(i).getTwitterUser().getProfileBackgroundColor()));
		
		if (lum > 150)
		{
			tvArobase.setTextColor(Color.BLACK);
			tvTweet.setTextColor(Color.BLACK);
		}
		else
		{
			tvArobase.setTextColor(Color.WHITE);
			tvTweet.setTextColor(Color.WHITE);
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

