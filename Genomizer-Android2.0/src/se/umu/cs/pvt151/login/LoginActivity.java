package se.umu.cs.pvt151.login;


import java.io.IOException;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Login screens activity that creates the login_layout.
 * Handles logic so the loginrequest is sent and loginerrors.
 * 
 */
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
	
	/**
	 * Starts the lifecycle of the activity. 
	 * Creates the layout with its items.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
	
		mServerURLTextView = (TextView) findViewById(R.id.login_tv_serverURL);
		mUsernameEditText = (EditText) findViewById(R.id.login_et_enterUsername);
		mPasswordEditText = (EditText) findViewById(R.id.login_et_enterPassword);
		mSignInButton = (Button) findViewById(R.id.login_btn_signIn);
	}
	/**
	 * When the login screen has been called more then once this is called so 
	 * last server user used is saved in SharedPreferences.
	 */
	@Override
	public void onResume() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				LoginSettingsActivity.SERVER_PREFERENCES, Context.MODE_PRIVATE);
		
		serverURL = sharedPreferences.getString(LoginSettingsActivity.NAME_OF_SELECTED_SERVER, "No server selected.");
		mServerURLTextView.setText(serverURL);
		
		super.onResume();
	}
	
	/**
	 * Saves username and password so they can be sent in a login-
	 * package.
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		String username = mUsernameEditText.toString();
		String password = mPasswordEditText.toString();
		
		savedInstanceState.putString(SAVED_INSTANCE_USERNAME, username);
		savedInstanceState.putString(SAVED_INSTANCE_PASSWORD, password);
		
		super.onSaveInstanceState(savedInstanceState);
	}
	/**
	 * Restores the username and password back after it has been saved.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		String username = savedInstanceState.getString(SAVED_INSTANCE_USERNAME);
		String password = savedInstanceState.getString(SAVED_INSTANCE_PASSWORD);
		mUsernameEditText.setText(username);
		mPasswordEditText.setText(password);
	}
	/**
	 * Inflates the login_menu so items shows in the actionbar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_menu, menu);
		return true;
	}
	/**
	 * Action bar items i handled here. Will handle the click on 
	 * login_action_settings that takes the user to LoginSettingsActivity.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.login_action_settings) {
			Intent intent = new Intent(this, LoginSettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * Executes the LoginTask.
	 * @param v
	 */
	public void login(View v) {
		mSignInButton.setEnabled(false);
		new LoginTask().execute();
	}
	
	/**
	 * A private class that handles the login request in a
	 * background thread.
	 * @author Petter Nilsson (ens11pnn)
	 *
	 */
	private class LoginTask extends AsyncTask<Void, Void, Integer> {
		
		
		private static final String CONNECT_MESSAGE = 
				"Connecting to server:\n";
		
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
		protected Integer doInBackground(Void... params) {
			return sendLoginRequest();
		}
		
		/**
		 * If the login-request was successful the MainActivity is started up.
		 * Also re-enables the login button and dismiss the progress screen
		 * that is shown during the login request.
		 * 
		 * If the login should fail a proper toast is displayed.
		 */
		@Override
		protected void onPostExecute(Integer result) {
			mSignInButton.setEnabled(true);		
			mProgress.dismiss();
			
			if (result.equals(ComHandler.OK)) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				toastLoginError(result);
			}
		}
		
		/**
		 * Fetches the text that user inputs into the user name and user password
		 * input fields. These strings are used to verify server access through the
		 * ComHandler. The ComHandler receives a token identifier if the user gets
		 * access, and is needed throughout the session.
		 */
		private int sendLoginRequest() {
			String username = mUsernameEditText.getText().toString();
			String password = mPasswordEditText.getText().toString();
			ComHandler.setServerURL(serverURL);
			
			try {
				return ComHandler.login(username, password);
			} catch (IOException ioe) {
				return ComHandler.NO_CONNECTION_WITH_SERVER;
			}
			
		}
		
		/**
		 * A method that makes a toast depending on the error code
		 * provided as input. The error code is matched with a R.string
		 * and then displayed to the user.
		 * <br><br/>
		 * Make sure to call this method when you are on the main GUI
		 * thread.
		 * @param code The error code found in ComHandler
		 * @author Petter Nilsson (ens11pnn)
		 */
		private void toastLoginError(int code) {
			int stringId;
			switch (code) {
				case ComHandler.BAD_REQUEST:
					stringId = R.string.msg_http_response_400;
					break;
				case ComHandler.FORBIDDEN:
					stringId = R.string.msg_http_response_403;
					break;
				case ComHandler.NO_CONNECTION_WITH_SERVER:
					stringId = R.string.msg_no_connection_with_server;
					break;
				case ComHandler.NO_CONTENT:
					stringId = R.string.msg_http_response_204;
					break;
				case ComHandler.NO_INTERNET_CONNECTION:
					stringId = R.string.msg_no_internet_availiable;
					break;
				case ComHandler.NOT_ALLOWED:
					stringId = R.string.msg_http_response_405;
					break;
				case ComHandler.SERVICE_UNAVAILIABLE:
					stringId = R.string.msg_http_response_503;
					break;
				case ComHandler.TOO_MANY_REQUESTS:
					stringId = R.string.msg_http_response_429;
					break;
				case ComHandler.UNAUTHORIZED:
					stringId = R.string.msg_http_response_401;
					break;
				default:
					stringId = R.string.msg_an_error_occurred;
					break;
			}
			
			Toast.makeText(LoginActivity.this, 
						   stringId, 
						   Toast.LENGTH_LONG).show();
		}
		
	}
}
