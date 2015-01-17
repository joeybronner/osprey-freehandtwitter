package fr.joeybronner.freehandtwitter.api;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.joeybronner.freehandtwitter.R;

public class TwitterAsyncTask extends AsyncTask<Object, Void, ArrayList<TwitterStatuses>> {
    ListActivity callerActivity;

    final static String TWITTER_API_KEY = "zOzdMJLSH1uNEF37Gvzz1No3M";
    final static String TWITTER_API_SECRET = "lwSxPa55Z3spqaMr30aqbINV802eaY1mFzsdvv8Yqxst6Rrrk4";

    @Override
    protected ArrayList<TwitterStatuses> doInBackground(Object... params) {
        ArrayList<TwitterStatuses> twitterTweets = null;
        callerActivity = (ListActivity) params[1];
        if (params.length > 0) {
            TwitterAPI twitterAPI = new TwitterAPI(TWITTER_API_KEY,TWITTER_API_SECRET);
            twitterTweets = twitterAPI.getTwitterTweets(params[0].toString());
        }
        return twitterTweets;
    }

    @Override
    protected void onPostExecute(ArrayList<TwitterStatuses> twitterTweets) {
        ArrayAdapter<TwitterStatuses> adapter = new ArrayAdapter<TwitterStatuses>(callerActivity, R.layout.activity_main, R.id.listTextView, twitterTweets);
        callerActivity.setListAdapter(adapter);
        ListView lv = callerActivity.getListView();
        lv.setDividerHeight(0);
        //lv.setDivider(this.getResources().getDrawable(android.R.color.transparent));
        lv.setBackgroundColor(callerActivity.getResources().getColor(R.color.Twitter_blue));
    }
}
