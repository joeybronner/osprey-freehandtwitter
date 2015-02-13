package fr.joeybronner.freehandtwitter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import fr.joeybronner.freehandtwitter.api.TwitterAsyncTask;
import fr.joeybronner.freehandtwitter.util.AndroidNetworkUtility;

public class ResultActivity extends ListActivity {

    final static String TAG = "MainActivity";
    
    public ProgressDialog progress;
    public String search, result_type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide action bar
        //getActionBar().hide();
        
        // Get search word(s)
        Intent myIntent = getIntent();
        search = myIntent.getStringExtra("search");
        result_type = myIntent.getStringExtra("result_type");
        
        AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
        if (androidNetworkUtility.isConnected(this)) {
    		new TwitterAsyncTask(this).execute(search,this);
        } else {
        	finish();
        	Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_noconnection), Toast.LENGTH_SHORT).show();
        }
    }
}
