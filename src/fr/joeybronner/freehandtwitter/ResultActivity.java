package fr.joeybronner.freehandtwitter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import fr.joeybronner.freehandtwitter.api.TwitterAsyncTask;
import fr.joeybronner.freehandtwitter.util.AndroidNetworkUtility;

public class ResultActivity extends ListActivity {

    final static String TAG = "MainActivity";
    
    public ProgressDialog progress;
    String search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide action bar
        getActionBar().hide();
        
        // Get search word(s)
        Intent myIntent = getIntent();
        search = myIntent.getStringExtra("search");
        
        AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
        if (androidNetworkUtility.isConnected(this)) {
    		new TwitterAsyncTask(this).execute(search,this);
        } else {
            Log.v(TAG, "Network not Available!");
        }
    }
}
