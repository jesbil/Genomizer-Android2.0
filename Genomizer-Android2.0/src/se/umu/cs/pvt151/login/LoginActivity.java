package se.umu.cs.pvt151.login;


import java.io.IOException;
import java.util.Arrays;

import se.umu.cs.pvt151.MainActivity;
import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.com.ComHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	private final static String SAVED_INSTANCE_USERNAME = 
			"se.umu.cs.pvt151.login.LoginActivity.SAVED_INSTANCE_USERNAME";
	
	private final static String SAVED_INSTANCE_PASSWORD =
			"se.umu.cs.pvt151.login.LoginActivity.SAVED_INSTANCE_PASSWORD";
	
	private String serverURL;
	private TextView mServerURLTextView;
	private EditText mUsernameEditText;
	private EditText mPasswordEditText;
	private Button mSignInButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
	
		
		mServerURLTextView = (TextView) findViewById(R.id.login_tv_serverURL);
		mUsernameEditText = (EditText) findViewById(R.id.login_et_enterUsername);
		mPasswordEditText = (EditText) findViewById(R.id.login_et_enterPassword);
		
		/*if (savedInstanceState != null) {
			String username = savedInstanceState.getString(SAVED_INSTANCE_USERNAME);
			String password = savedInstanceState.getString(SAVED_INSTANCE_PASSWORD);
			mUsernameEditText.setText(username);
			mPasswordEditText.setText(password);
		}*/
		
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
	public void onSaveInstanceState(Bundle savedInstanceState) {
		String username = mUsernameEditText.toString();
		String password = mPasswordEditText.toString();
		
		savedInstanceState.putString(SAVED_INSTANCE_USERNAME, username);
		savedInstanceState.putString(SAVED_INSTANCE_PASSWORD, password);
		
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		String username = savedInstanceState.getString(SAVED_INSTANCE_USERNAME);
		String password = savedInstanceState.getString(SAVED_INSTANCE_PASSWORD);
		mUsernameEditText.setText(username);
		mPasswordEditText.setText(password);
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
		mSignInButton = (Button) findViewById(R.id.login_btn_signIn);
		mSignInButton.setEnabled(false);
		
		new LoginTask().execute();
	}
	
	/**
	 * Fetches the text that user inputs into the user name and user password
	 * input fields. These strings are used to verify server access through the
	 * ComHandler. The ComHandler receives a token identifier if the user gets
	 * access, and is needed throughout the session.
	 */
	private boolean sendLoginRequest() {
		String username = mUsernameEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();
		if (username.length() <= 0 || password.length() <= 0) {
			return false;
		}		
		
		ComHandler.setServerURL(serverURL);
		
		try {
			
			return ComHandler.login(username, password);		
			
		} catch (IOException e) {
			Log.d("login", "exception: " + Arrays.toString(e.getStackTrace()));
			Log.d("login", "exception: " + e.getMessage());			
		}
		
		return false;
	}
	
	/**
	 * A private class that handles the login request in a
	 * background thread.
	 * @author Petter Nilsson (ens11pnn)
	 *
	 */
	private class LoginTask extends AsyncTask<Void, Void, Boolean> {
		
		
		private static final String CONNECT_MESSAGE = 
				"Connecting to server: \n";
		
		private static final String CONNECT = "Connecting";
		
		private ProgressDialog mProgress;
		
		/**
		 * Creates a new LoginTask. Builds a ProgressDialog that is displayed
		 * during the background work.
		 */
		public LoginTask() {
			mProgress = new ProgressDialog(LoginActivity.this);
			mProgress.setTitle(CONNECT);
			mProgress.setMessage(CONNECT_MESSAGE + serverURL);
			mProgress.show();
		}
		
		/**
		 * Sends login request and dispatches the answer onPostExecute
		 */
		@Override
		protected Boolean doInBackground(Void... params) {
			return sendLoginRequest();
		}
		
		/**
		 * If the login-request was successful SearchFragment is started up.
		 * Also re-enables the login button and dismiss the progress screen
		 * that is shown during the login request.
		 */
		@Override
		protected void onPostExecute(Boolean result) {

			if (result.booleanValue()) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
			
			mSignInButton.setEnabled(true);		
			mProgress.dismiss();
		}
		
	}
}
