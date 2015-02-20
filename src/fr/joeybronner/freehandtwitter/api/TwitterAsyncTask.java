package fr.joeybronner.freehandtwitter.api;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Window;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import fr.joeybronner.freehandtwitter.R;
import fr.joeybronner.freehandtwitter.ResultActivity;
import fr.joeybronner.freehandtwitter.TweetFlipperActivity;
import fr.joeybronner.freehandtwitter.util.Constants;

public class TwitterAsyncTask extends AsyncTask<Object, Void, ArrayList<TwitterStatus>> {
	ListActivity callerActivity;
	private Dialog loading;
	private Context context;

	public TwitterAsyncTask(ResultActivity activity) {
		context = activity;
		// Create the new dialog
		loading = new Dialog(context);
		// No title
		loading.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		loading.setCancelable(true);
		// Content of the dialog
		loading.setContentView(R.layout.activity_loading);
		
		ImageView gif = (ImageView) loading.findViewById(R.id.ivGif);
		Ion.with(gif).load("android.resource://fr.joeybronner.freehandtwitter/drawable/load");
	
		loading.show();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ArrayList<TwitterStatus> doInBackground(Object... params) {
		ArrayList<TwitterStatus> twitterTweets = null;
		callerActivity = (ListActivity) params[1];
		if (params.length > 0) {
			TwitterAPI twitterAPI = new TwitterAPI(Constants.TWITTER_API_KEY,Constants.TWITTER_API_SECRET);
			twitterTweets = twitterAPI.getTwitterTweets(params[0].toString());
		}
		return twitterTweets;
	}

	@Override
	protected void onPostExecute(ArrayList<TwitterStatus> twitterTweets) {
		//ArrayAdapter<TwitterStatus> adapter = new ArrayAdapter<TwitterStatus>(callerActivity, R.layout.activity_result, R.id.listTextView, twitterTweets);
		//callerActivity.setListAdapter(adapter);
		//ListView lv = callerActivity.getListView();
		//lv.setDividerHeight(0);
		//lv.setBackgroundColor(callerActivity.getResources().getColor(R.color.blue));
		if (loading.isShowing()) {
			Constants.twit = twitterTweets;
    		Intent i = new Intent(callerActivity, TweetFlipperActivity.class);
    		callerActivity.startActivity(i);
    		callerActivity.finish();
		}
	}
}
