package fr.joeybronner.freehandtwitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import fr.joeybronner.freehandtwitter.util.Constants;

public class TweetFlipperActivity extends Activity {

	private ViewFlipper viewFlipper;
	private static int SLIDER_TIMER; 
	String search;
	int i = 0;
	boolean isPaused = false;
	boolean isDark;
	ImageView btPlayPause, ivUser, btTweetNext, btTweetBack, btShare;
	TextView tvArobase, tvName, tvTweet;
	View v;
	Bitmap bm;
	final BitmapFactory.Options options = new BitmapFactory.Options();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_flipper);
		getActionBar().hide();

		if (Constants.twit.isEmpty() || Constants.twit == null) {
			finish();
		}

		// Stay screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Typeface
		Constants.tf = Typeface.createFromAsset(this.getAssets(),"fonts/OpenSans-Light.ttf");

		// Recalculation of SLIDER_TIMER
		SLIDER_TIMER = 6000; 
		if (Constants.SCROLL_SPEED < 20) {
			SLIDER_TIMER = (int) (SLIDER_TIMER*1.5);
		} else if (Constants.SCROLL_SPEED >= 20 && Constants.SCROLL_SPEED < 40) {
			SLIDER_TIMER = (int) (SLIDER_TIMER*1.2);
		} else if (Constants.SCROLL_SPEED >= 40 && Constants.SCROLL_SPEED < 60) {
			// Nothing
		} else if (Constants.SCROLL_SPEED >= 60 && Constants.SCROLL_SPEED < 80) {
			SLIDER_TIMER = (int) (SLIDER_TIMER*0.8);
		} else if (Constants.SCROLL_SPEED >= 80) {
			SLIDER_TIMER = (int) (SLIDER_TIMER*0.5);
		}

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
		final Runnable r = new Runnable() {
			public void run() {
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

		btShare.setOnClickListener(new OnClickListener() { 
			@SuppressLint("SimpleDateFormat") public void onClick(View v) {
				try {
					Bitmap screenshot = screenshot(v);

					// Save file
					OutputStream output;
					File filepath = Environment.getExternalStorageDirectory();
					File dir = new File(filepath.getAbsolutePath() + "/FreeHandTwitter/");
					dir.mkdirs();
					DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					Calendar cal = Calendar.getInstance();
					File file = new File(dir, "freehandtwitter" 
							+ dateFormat.format(cal.getTime()) 
							+ ".png");

					// Share Intent
					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("image/jpeg");
					output = new FileOutputStream(file);

					// Compress into png format image from 0% - 100%
					screenshot.compress(Bitmap.CompressFormat.PNG, 100, output);
					output.flush();
					output.close();

					// Locate the image to Share
					Uri uri = Uri.fromFile(file);
					share.putExtra(Intent.EXTRA_STREAM, uri);
					startActivity(Intent.createChooser(share, getResources().getString(R.string.share_tweet)));
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unable_share), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private Bitmap screenshot(View v) {
		Bitmap bitmap;
		View v1 = v.getRootView();
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);
		return bitmap;
	}

	public void sendImage(Bitmap bitmap, String name) {
		String pathofBmp = Images.Media.insertImage(getContentResolver(), bitmap, name, null);
		Uri bmpUri = Uri.parse(pathofBmp);
		final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
		emailIntent1.setType("image/png");
	}

	public void saveImage(Context context, Bitmap b,String name){
		FileOutputStream out;
		try {
			out = context.openFileOutput(name, Context.MODE_PRIVATE);
			b.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Bitmap getImageBitmap(Context context, String name){
		try{
			FileInputStream fis = context.openFileInput(name);
			Bitmap b = BitmapFactory.decodeStream(fis);
			fis.close();
			return b;
		}
		catch(Exception e){
		}
		return null;
	}

	private void setBackgroundColor() {
		String fontColor = "#" + Constants.twit.get(i).getTwitterUser().getProfileBackgroundColor();
		getWindow().getDecorView().setBackgroundColor(Color.parseColor(fontColor));
	}

	private void setFontColor() {
		int lum = getBrightness(Color.parseColor("#" + Constants.twit.get(i).getTwitterUser().getProfileBackgroundColor()));
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		options.inDither = true;
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

