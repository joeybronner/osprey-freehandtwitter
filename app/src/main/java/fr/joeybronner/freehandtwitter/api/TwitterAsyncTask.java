package fr.joeybronner.freehandtwitter.api;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import fr.joeybronner.freehandtwitter.MainActivity;
import fr.joeybronner.freehandtwitter.ResultActivity;
import fr.joeybronner.freehandtwitter.TweetFlipperActivity;
import fr.joeybronner.freehandtwitter.util.Constants;

public class TwitterAsyncTask extends AsyncTask<Object, Void, ArrayList<TwitterStatus>> {
	ListActivity callerActivity;
	
	public TwitterAsyncTask(ResultActivity activity) {

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
		if (MainActivity.dialog.isShowing()) {
			Constants.twit = twitterTweets;
			MainActivity.dialog.dismiss();
    		Intent i = new Intent(callerActivity, TweetFlipperActivity.class);
    		callerActivity.startActivity(i);
    		callerActivity.finish();
		}
	}
}
