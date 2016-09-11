package fr.joeybronner.freehandtwitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import fr.joeybronner.freehandtwitter.util.Constants;

public class TweetFlipperActivity extends Activity {

	private ViewFlipper viewFlipper;
	private ProgressBar progressBar;
	boolean updatePB = true;
	private static int SLIDER_TIMER;
	int i = 0;
	boolean isPaused = false;
	boolean isDark;
	ImageView btPlayPause, ivUser1, ivUser2, ivUser3, btTweetNext, btTweetBack, btShare;
	TextView tvArobase1, tvName1, tvTweet1, tvArobase2, tvName2, tvTweet2, tvArobase3, tvName3, tvTweet3, tvHashtag;
	Bitmap bm;
	final BitmapFactory.Options options = new BitmapFactory.Options();
	Handler handler = new Handler();
	int progressStatus = 0;
	Thread progressBarThread;
	boolean hideCard1, hideCard2, hideCard3;
	Animation nextAnim;
	int cardNr;
    long millis;
    int TMP_SLIDER_TIMER = 0;
    Activity activity;
    double TMP_SLIDER_TIMER_CARD1, TMP_SLIDER_TIMER_CARD2, TMP_SLIDER_TIMER_CARD3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_tweet_flipper);

			if (Constants.twit == null || Constants.twit.isEmpty()) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_noresults) + " #" + Constants.TWITTER_USER_SEARCH, Toast.LENGTH_SHORT).show();Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_noresults) + " #" + Constants.TWITTER_USER_SEARCH, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				// Number of tweets loaded
				// Toast.makeText(getApplicationContext(), Constants.twit.size() + " " + getResources().getString(R.string.loaded), Toast.LENGTH_SHORT).show();

				// Stay screen on
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

				// Typeface
				Constants.tf = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Light.ttf");

                // Activity
                activity = this;

				// Recalculation of SLIDER_TIMER
				SLIDER_TIMER = 12000;
				if (Constants.SCROLL_SPEED < 20) {
					SLIDER_TIMER = (int) (SLIDER_TIMER*1.5);
				} else if (Constants.SCROLL_SPEED >= 20 && Constants.SCROLL_SPEED < 40) {
					SLIDER_TIMER = (int) (SLIDER_TIMER*1.2);
				} else if (Constants.SCROLL_SPEED >= 40 && Constants.SCROLL_SPEED < 60) {
					// Normal speed
				} else if (Constants.SCROLL_SPEED >= 60 && Constants.SCROLL_SPEED < 80) {
					SLIDER_TIMER = (int) (SLIDER_TIMER*0.8);
				} else if (Constants.SCROLL_SPEED >= 80) {
					SLIDER_TIMER = (int) (SLIDER_TIMER*0.5);
				}

				nextAnim = AnimationUtils.loadAnimation(this, R.anim.next);

				viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
				//viewFlipper.setFlipInterval(SLIDER_TIMER);
				viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
				viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
				tvHashtag = (TextView) findViewById(R.id.hashtag);
				tvHashtag.setTypeface(Constants.tf);
				tvHashtag.setText("#" + Constants.TWITTER_USER_SEARCH);

				// First card
				ivUser1 = (ImageView) findViewById(R.id.ivUser1);
				tvName1 = (TextView) findViewById(R.id.tvName1);
				tvName1.setTypeface(Constants.tf, Typeface.BOLD);
				tvArobase1 = (TextView) findViewById(R.id.tvArobase1);
				tvArobase1.setTypeface(Constants.tf);
				tvTweet1 = (TextView) findViewById(R.id.tvTweetContent1);
				tvTweet1.setTypeface(Constants.tf);

				// Second card
				ivUser2 = (ImageView) findViewById(R.id.ivUser2);
				tvName2 = (TextView) findViewById(R.id.tvName2);
				tvName2.setTypeface(Constants.tf, Typeface.BOLD);
				tvArobase2 = (TextView) findViewById(R.id.tvArobase2);
				tvArobase2.setTypeface(Constants.tf);
				tvTweet2 = (TextView) findViewById(R.id.tvTweetContent2);
				tvTweet2.setTypeface(Constants.tf);

				// Third card
				ivUser3 = (ImageView) findViewById(R.id.ivUser3);
				tvName3 = (TextView) findViewById(R.id.tvName3);
				tvName3.setTypeface(Constants.tf, Typeface.BOLD);
				tvArobase3 = (TextView) findViewById(R.id.tvArobase3);
				tvArobase3.setTypeface(Constants.tf);
				tvTweet3 = (TextView) findViewById(R.id.tvTweetContent3);
				tvTweet3.setTypeface(Constants.tf);

				btPlayPause = (ImageView) findViewById(R.id.btTweetPlayPause);
				btTweetNext = (ImageView) findViewById(R.id.btTweetNext);
				btTweetBack = (ImageView) findViewById(R.id.btTweetBack);
				btShare = (ImageView) findViewById(R.id.btShare);
				progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
				progressBar.setMax(100);
				progressStatus = 0;
				btPlayPause.setOnClickListener(new OnClickListener() { 
					@Override
					public void onClick(View v) {
                        bm.recycle();
                        bm = null;
						if(isPaused) {
							bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause_material),800, 800, true);
							btPlayPause.setImageBitmap(bm);
							isPaused = false;
							handler.postDelayed(r, 0);
                            viewFlipper.showNext();
                            updatePB = true;
						}
						else {
							updatePB = false;
							bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.play_material),800, 800, true);
							btPlayPause.setImageBitmap(bm);
							isPaused = true;
							handler.removeCallbacks(r);
						}
					}
				});

				btTweetNext.setOnClickListener(new OnClickListener() { 
					@Override
					public void onClick(View v) {
                        millis = System.currentTimeMillis();
                        TMP_SLIDER_TIMER = 0;
                        i = i+3 >= Constants.twit.size() ? i=0 : i+3;
                        viewFlipper.showNext();
                        updateCardsContent();
					}
				});

				btTweetBack.setOnClickListener(new OnClickListener() { 
					@Override
					public void onClick(View v) {
                        millis = System.currentTimeMillis();
                        TMP_SLIDER_TIMER = 0;
                        i = i-3 < 0 ? i = 0 : i-3;
                        viewFlipper.showNext();
                        updateCardsContent();
					}
				});

				btShare.setOnClickListener(new OnClickListener() { 
					@Override
					@SuppressLint("SimpleDateFormat") public void onClick(View v) {
						try {
							if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
							} else {
                                Bitmap screenshot = takeScreenshot(v);
                                File sc = saveBitmap(screenshot);
                                shareIt(sc);
                            }
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unable_share), Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			finish();
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_loading_tweets), Toast.LENGTH_SHORT).show();
		}
	}

    private void shareIt(File imagePath) {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public File saveBitmap(Bitmap bitmap) {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot" + dateFormat.format(cal.getTime()) + ".png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
        return imagePath;
    }

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("loopState", i-2);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		i = savedInstanceState.getInt("loopState");
	}

	@Override
	protected void onPause() {
		handler.removeCallbacks(r);
		super.onPause();
	}

	@Override
	protected void onResume() {
		handler.postDelayed(r, 0);
        viewFlipper.showNext();
		super.onResume();
	}

	final Runnable r = new Runnable() {
		@Override
		public void run() {
				try {
					updatePB = true;
					hideCard1 = true;
					hideCard2 = true;
					hideCard3 = true;
                    millis = System.currentTimeMillis();
					updateCardsContent();

					viewFlipper.setInAnimation(TweetFlipperActivity.this, R.anim.slide_in_from_right);
					viewFlipper.setOutAnimation(TweetFlipperActivity.this, R.anim.slide_out_to_left);

                    TMP_SLIDER_TIMER_CARD1 = SLIDER_TIMER * 0.80;
                    TMP_SLIDER_TIMER_CARD2 = SLIDER_TIMER * 0.85;
                    TMP_SLIDER_TIMER_CARD3 = SLIDER_TIMER * 0.90;

					progressBarThread = new Thread(new Runnable() {
						public void run() {
                            TMP_SLIDER_TIMER = 0;
							while(progressStatus < 100 && updatePB==true) {
								try {
									Thread.sleep(100);
									TMP_SLIDER_TIMER += 100;
									progressStatus = doWork(millis);
									progressBar.setProgress(progressStatus);
									progressBar.refreshDrawableState();
									if (TMP_SLIDER_TIMER > TMP_SLIDER_TIMER_CARD3 && hideCard3) {
                                        hideCard3 = false;
										cardNr = 3;
										anim();
                                        Thread.sleep(500);
                                        millis = System.currentTimeMillis();
                                        TMP_SLIDER_TIMER = 0;
                                        i = i+3 >= Constants.twit.size() ? i=0 : i+3;
                                        hideCard1 = true;
                                        hideCard2 = true;
                                        hideCard3 = true;
										updateCardsContent();
									}
									if (TMP_SLIDER_TIMER > TMP_SLIDER_TIMER_CARD2 && hideCard2) {
                                        hideCard2 = false;
										cardNr = 2;
										anim();
									}
									if (TMP_SLIDER_TIMER > TMP_SLIDER_TIMER_CARD1 && hideCard1) {
                                        hideCard1 = false;
										cardNr = 1;
										anim();
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
					// Start progressbar
                    progressBarThread.start();

				} catch (Exception e) {

				}
		}
	};

	private void updateCardsContent() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allCardsVisible();
                // Card 1
                tvTweet1.setText(Constants.twit.get(i).toString());
                tvArobase1.setText(" " +
                        "@" + Constants.twit.get(i).getTwitterUser().getScreenName());
                tvName1.setText(Constants.twit.get(i).getTwitterUser().getName());
                setFontColor();
                new ImageDownloader(ivUser1).execute(Constants.twit.get(i).getTwitterUser().getProfileImageUrl());
                ivUser1.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/" + Constants.twit.get(i).getTwitterUser().getName()));
                        startActivity(browserIntent);
                    }
                });

                // Card 2
                tvTweet2.setText(Constants.twit.get(i+1).toString());
                tvArobase2.setText(" " +
                        "@" + Constants.twit.get(i+1).getTwitterUser().getScreenName());
                tvName2.setText(Constants.twit.get(i+1).getTwitterUser().getName());
                setFontColor();
                new ImageDownloader(ivUser2).execute(Constants.twit.get(i+1).getTwitterUser().getProfileImageUrl());
                ivUser2.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/" + Constants.twit.get(i+1).getTwitterUser().getName()));
                        startActivity(browserIntent);
                    }
                });

                // Card 3
                tvTweet3.setText(Constants.twit.get(i+2).toString());
                tvArobase3.setText(" " +
                        "@" + Constants.twit.get(i+2).getTwitterUser().getScreenName());
                tvName3.setText(Constants.twit.get(i+2).getTwitterUser().getName());
                setFontColor();
                new ImageDownloader(ivUser3).execute(Constants.twit.get(i+2).getTwitterUser().getProfileImageUrl());
                ivUser3.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/" + Constants.twit.get(i+2).getTwitterUser().getName()));
                        startActivity(browserIntent);
                    }
                });
            }
        });
	}

	private void anim() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LinearLayout dialog = null;
				if (cardNr == 1)
					dialog = (LinearLayout)findViewById(R.id.card1);
				else if (cardNr == 2)
					dialog = (LinearLayout)findViewById(R.id.card2);
				else if (cardNr == 3)
					dialog = (LinearLayout)findViewById(R.id.card3);
				dialog.startAnimation(nextAnim);
				dialog.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void allCardsVisible() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LinearLayout dialog1 = (LinearLayout)findViewById(R.id.card1);
				LinearLayout dialog2 = (LinearLayout)findViewById(R.id.card2);
				LinearLayout dialog3 = (LinearLayout)findViewById(R.id.card3);
				dialog1.setVisibility(View.VISIBLE);
				dialog2.setVisibility(View.VISIBLE);
				dialog3.setVisibility(View.VISIBLE);
			}
		});
	}

	private int doWork(long millis) throws InterruptedException {
		long diff = System.currentTimeMillis() - millis;
		return (int) ((diff*100)/SLIDER_TIMER);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

	private Bitmap takeScreenshot(View v) {
		Bitmap bitmap;
		View v1 = v.getRootView();
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);
		return bitmap;
	}

	private void setFontColor() {
		int lum = getBrightness(Color.parseColor("#" + Constants.twit.get(i).getTwitterUser().getProfileBackgroundColor()));
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		options.inDither = true;
        isDark = false;
		tvArobase1.setTextColor(getResources().getColor(R.color.darkgray));
		tvTweet1.setTextColor(getResources().getColor(R.color.darkgray));
		tvName1.setTextColor(getResources().getColor(R.color.darkgray));
        bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause_material),800, 800, true);
		btPlayPause.setImageBitmap(bm);
		//bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.next_material),800, 800, true);
        //btTweetNext.setImageBitmap(bm);
        //bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.previous_material),800, 800, true);
        //btTweetBack.setImageBitmap(bm);
        //bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_dark),800, 800, true);
        //btShare.setImageBitmap(bm);
	}

	public static int getBrightness(int argb) {
		int lum= (77  * ((argb>>16)&255)
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

	@Override
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

	@Override
	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}
}

