package fr.joeybronner.freehandtwitter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import fr.joeybronner.freehandtwitter.api.TwitterAsyncTask;
import fr.joeybronner.freehandtwitter.util.AndroidNetworkUtility;

public class ResultActivity extends ListActivity {

	public String search, result_type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get search word(s)
		Intent myIntent = getIntent();
		search = myIntent.getStringExtra("search");
		result_type = myIntent.getStringExtra("result_type");

		AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
		if (androidNetworkUtility.isConnected(this)) {
			try {
				new TwitterAsyncTask(this).execute(search,this).get(20000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				MainActivity.dialog.dismiss();
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_interrupted), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (ExecutionException e) {
				MainActivity.dialog.dismiss();
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_execution), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (TimeoutException e) {
				MainActivity.dialog.dismiss();
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_timeout), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			};
		} else {
			finish();
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_noconnection), Toast.LENGTH_SHORT).show();
		}
	}
}
