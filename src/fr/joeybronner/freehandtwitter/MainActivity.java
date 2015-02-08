package fr.joeybronner.freehandtwitter;

import fr.joeybronner.freehandtwitter.util.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressLint("DefaultLocale") public class MainActivity extends Activity {

	Button btMore, btSearch, btSettings;
	Spinner spinnerResultType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().hide();
		final EditText etSearch = (EditText) findViewById(R.id.etSearch);
		spinnerResultType = (Spinner) findViewById(R.id.spinnerResultType);

		btMore = (Button) findViewById(R.id.btMore);
		btMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMoreActivity();
			}
		});
		
		btSettings = (Button) findViewById(R.id.btSettings);
		btSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSettingsActivity();
			}
		});
		
		btSearch = (Button) findViewById(R.id.btSearch);
		btSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (searchFieldIsValid(etSearch.getText().toString())) {
					Intent intent = new Intent(MainActivity.this, ResultActivity.class);
					intent.putExtra("search",etSearch.getText().toString());
					Constants.TWITTER_RESULT_TYPE = "&result_type=" + spinnerResultType.getSelectedItem().toString().toLowerCase();
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.emptysearch), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private boolean searchFieldIsValid(String search) {
		if (!search.isEmpty() && !search.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	private void showSettingsActivity()
	{
		// Create the new dialog
		final Dialog dialog = new Dialog(btSettings.getContext());
		// No title
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setCancelable(true);

		// Content of the dialog
		dialog.setContentView(R.layout.activity_settings);
		dialog.show();
		
		final TextView tvSpeed = (TextView) dialog.findViewById(R.id.tvScrollSpeed);
		final SeekBar sk=(SeekBar) dialog.findViewById(R.id.sbScrollSpeed);     
		sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {             

			@Override       
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {     
				if (progress < 20) {
					tvSpeed.setText(getResources().getString(R.string.speed_veryslow));
				} else if (progress >= 20 && progress < 40) {
					tvSpeed.setText(getResources().getString(R.string.speed_slow));
				} else if (progress >= 40 && progress < 60) {
					tvSpeed.setText(getResources().getString(R.string.speed_normal));
				} else if (progress >= 60 && progress < 80) {
					tvSpeed.setText(getResources().getString(R.string.speed_fast));
				} else if (progress >= 80) {
					tvSpeed.setText(getResources().getString(R.string.speed_veryfast));
				}
				// Update scroll speed
				Constants.SCROLL_SPEED = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {	}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	}       
		});
	}

	private void showMoreActivity()
	{
		// Create the new dialog
		final Dialog dialog = new Dialog(btMore.getContext());
		// No title
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setCancelable(true);

		// Content of the dialog
		dialog.setContentView(R.layout.activity_more);
		dialog.show();
	}
}
