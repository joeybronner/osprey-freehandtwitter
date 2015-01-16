package fr.joeybronner.freehandtwitter;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import fr.joeybronner.freehandtwitter.api.TwitterAsyncTask;
import fr.joeybronner.freehandtwitter.util.AndroidNetworkUtility;

public class MainActivity extends ListActivity {

    final static String twitterScreenName = "BBCNews";
    final static String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
        if (androidNetworkUtility.isConnected(this)) {
            new TwitterAsyncTask().execute(twitterScreenName,this);
        } else {
            Log.v(TAG, "Network not Available!");
        }
    }
}
