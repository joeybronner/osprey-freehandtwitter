package fr.joeybronner.freehandtwitter;

import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	ImageView btPlayPause, ivUser;
	TextView tvTweet, tvArobase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_flipper);
		getActionBar().hide();

		// Stay screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		viewFlipper.setFlipInterval(SLIDER_TIMER);
		viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
		viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
		viewFlipper.startFlipping();

		tvTweet = (TextView) findViewById(R.id.tvTweetContent);
		tvArobase = (TextView) findViewById(R.id.textView1);
		ivUser = (ImageView) findViewById(R.id.ivUser);
		btPlayPause = (ImageView) findViewById(R.id.btTweetPlayPause);
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
	}

	// Using the following method, we will handle all screen swaps
	/*
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: 
			lastX = touchevent.getX();
			break;
		case MotionEvent.ACTION_UP: 
			float currentX = touchevent.getX();

			// Handling left to right screen swap.
			if (lastX < currentX) {
				// If there aren't any other children, just break.
				if (viewFlipper.getDisplayedChild() == 0)
					break;

				// Next screen comes in from left.
				viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
				// Current screen goes out from right. 
				viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

				// Display next screen.
				viewFlipper.showNext();
			}
			// Handling right to left screen swap.
			if (lastX > currentX) {

				// If there is a child (to the left), kust break.
				if (viewFlipper.getDisplayedChild() == 1)
					break;

				// Next screen comes in from right.
				viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
				// Current screen goes out from left. 
				viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

				// Display previous screen.
				viewFlipper.showPrevious();
			}
			break;
		}
		return false;
	}*/
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

