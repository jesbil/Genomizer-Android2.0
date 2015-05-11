package se.umu.cs.pvt151.login;


import se.umu.cs.pvt151.MainActivity;
import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.R.id;
import se.umu.cs.pvt151.R.layout;
import se.umu.cs.pvt151.R.menu;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	private String serverURL;
	private TextView mServerURLTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		mServerURLTextView = (TextView) findViewById(R.id.login_tv_serverURL);
	}
	
	@Override
	public void onResume() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				LoginSettingsActivity.SERVER_PREFERENCES, Context.MODE_PRIVATE);
		
		serverURL = sharedPreferences.getString(LoginSettingsActivity.NAME_OF_SELECTED_SERVER, "No server selected.");
		mServerURLTextView.setText(serverURL);
		
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.login_action_settings) {
			Intent intent = new Intent(this, LoginSettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void login(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}
}
