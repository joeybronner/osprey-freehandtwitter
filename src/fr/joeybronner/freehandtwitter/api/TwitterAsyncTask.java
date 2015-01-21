package fr.joeybronner.freehandtwitter.api;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.joeybronner.freehandtwitter.R;
import fr.joeybronner.freehandtwitter.ResultActivity;
import fr.joeybronner.freehandtwitter.util.Constants;

public class TwitterAsyncTask extends AsyncTask<Object, Void, ArrayList<TwitterStatus>> {
    ListActivity callerActivity;
    private ProgressDialog dialog;
    private Context context;

    public TwitterAsyncTask(ResultActivity activity) {
        context = activity;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
        this.dialog.setMessage("Progress start");
        this.dialog.show();
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
        ArrayAdapter<TwitterStatus> adapter = new ArrayAdapter<TwitterStatus>(callerActivity, R.layout.activity_result, R.id.listTextView, twitterTweets);
        callerActivity.setListAdapter(adapter);
        ListView lv = callerActivity.getListView();
        lv.setDividerHeight(0);
        //lv.setDivider(this.getResources().getDrawable(android.R.color.transparent));
        lv.setBackgroundColor(callerActivity.getResources().getColor(R.color.blue));
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
