package fr.joeybronner.freehandtwitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().hide();

		final EditText etSearch = (EditText) findViewById(R.id.etSearch);

		Button btSearch= (Button) findViewById(R.id.btSearch);
		btSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (searchFieldIsValid(etSearch.getText().toString())) {
					Intent intent = new Intent(MainActivity.this, ResultActivity.class);
					intent.putExtra("search",etSearch.getText().toString());
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
}
