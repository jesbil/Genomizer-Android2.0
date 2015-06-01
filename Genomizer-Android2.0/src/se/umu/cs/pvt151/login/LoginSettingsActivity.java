package se.umu.cs.pvt151.login;

import java.util.ArrayList;
import java.util.regex.Pattern;

import se.umu.cs.pvt151.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class LoginSettingsActivity extends Activity {
	
	protected final static String SERVER_PREFERENCES = 
			"se.umu.cs.pvt151.SERVER_PREFERENCES";
	
	private final static String INDEX_OF_SELECTED_SERVER = 
			"indexOfSelectedServer";
	
	protected final static String NAME_OF_SELECTED_SERVER = 
			"nameOfSelectedServer";
	
	private final static String ALL_SERVERS = "allServers";
	
	private final static String DELIMITER = "#";
	
	private Spinner mServerSpinner;
	private ArrayList<String> savedServerURLs;
	private ArrayAdapter<String> mSpinnerAdapter;
	
	private EditText mEditURLInput;
	private AlertDialog mEditURLDialog;
	
	private TextView mRemoveURLText;
	private AlertDialog mRemoveURLDialog;
	
	private EditText mAddURLInput;
	private AlertDialog mAddURLDialog;
	
	private SharedPreferences sharedPreferences;
	
	/**
	 * Starts the lifcycle of login_layout_settings.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout_settings);
		
		sharedPreferences = getSharedPreferences(SERVER_PREFERENCES, 
				Context.MODE_PRIVATE);

		buildEditURLDialog();
		buildRemoveURLDialog();
		buildAddURLDialog();
	}
	/**
	 * Saves the sevrers when user goes back to loginscreen.
	 */
	@Override
	public void onPause() {
		saveToSharedPreferences();
		super.onPause();
	}
	
	/**
	 * Builds the serverSpinner when the user returns to the login_layout_settings.
	 */
	@Override
	public void onResume() {
		buildServerSpinner();
		super.onResume();
	}
	
	/**
	 * Creates the Spinner that handles the serverURLs.
	 * Gets the saved urls that has been added before by the user.
	 */
	private void buildServerSpinner() {		
		mServerSpinner = (Spinner) 
				 findViewById(R.id.login_settings_spinner_servers);
		
		savedServerURLs = getSavedURLArrayList();
		
		mSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, savedServerURLs);
		
		mSpinnerAdapter.setDropDownViewResource(
				android.R.layout.simple_list_item_single_choice);
		
		mServerSpinner.setAdapter(mSpinnerAdapter);
		
		mServerSpinner.setSelection(getSavedSelectedIndex());
	}
	
	/**
	 * Dialog window so the selected server can be edited.
	 * 
	 */
	private void buildEditURLDialog() {
		mEditURLInput = new EditText(this);
		
		mEditURLDialog = new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.login_settings_btn_editURL))
				.setView(mEditURLInput)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setEditedURL(mEditURLInput.getText().toString());	
						
					}
				})
				.setNegativeButton("Cancel", new DialogInterface
						.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Cancel
					}
				})
				.create();
	}
	
	/**
	 * Comes up when the user has selected a serverurk in the spinner and
	 * presses "Red cross" in the actionbar. This secures that the user really want to 
	 * delete that serverURL.
	 */
	private void buildRemoveURLDialog() {
		mRemoveURLText = new TextView(this);
		mRemoveURLText.setTypeface(null, Typeface.BOLD);
		
		mRemoveURLDialog = new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.login_settings_removeURL))
				.setView(mRemoveURLText)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeSelectedURL();	
					}
				})
				.setNegativeButton("No", new DialogInterface
						.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Cancel
					}
				})
				.create();
	}
	
	/**
	 * Dialog window that comes up when the user presses the green
	 * add button in the actionbar. There the user can write in 
	 * the serverURL and press ok to save it to the Spinner.
	 */
	private void buildAddURLDialog() {
		mAddURLInput = new EditText(this);
		mAddURLInput.setText("http://");
		mAddURLInput.setSelection(mAddURLInput.getText().length());
		mAddURLDialog = new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.login_settings_addURL))
				.setView(mAddURLInput)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						addServerURL(mAddURLInput.getText().toString());
						mAddURLInput.setText("http://");
						mAddURLInput.setSelection(mAddURLInput.getText().length());
					}
				})
				.setNegativeButton("Cancel", new DialogInterface
						.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Cancel
					}
				})
				.create();
	}
	 	
	@Override
	public boolean onNavigateUp() {
		onBackPressed();
		return true;
	}
	/**
	 * Inflates the login_settings_menu and it's items shows.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_settings_menu, menu);
		return true;
	}
	/**
	 * Handles the actionbar clicks on items login_settings_addURL
	 * and login_settings_removeURL.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		case R.id.login_settings_addURL:
			onClickAddURL();
			return true;
		case R.id.login_settings_removeURL:
			onClickRemoveURL();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * When the Edit URL button is pressed creates the 
	 * 
	 * @param v
	 */
	public void onClickEditURL(View v) {
		if (!savedServerURLs.isEmpty()) {
			mEditURLInput.setText(mServerSpinner.getSelectedItem().toString());
			mEditURLInput.setSelection(mEditURLInput.getText().length());
			mEditURLDialog.show();
		}
	}
	/**
	 * Updates the spinner with the edited url.
	 * @param editedURL - severURL form edit dialog.
	 */
	private void setEditedURL(String editedURL) {
		String serverURL = (String) mServerSpinner.getSelectedItem();
		
		savedServerURLs.set(savedServerURLs.indexOf(serverURL), formatURLString(editedURL));
 		mSpinnerAdapter.notifyDataSetChanged();
	}
	
	/**
	 * If the spinner is not empty the dialog is shown with the seected url when
	 * the user presses "red cross". 
	 */
	public void onClickRemoveURL() {
		if (!savedServerURLs.isEmpty()) {
			mRemoveURLText.setText("Do you really want to remove URL:\n " 
					   + mServerSpinner.getSelectedItem());
			mRemoveURLDialog.show();	
		}

	}
	/**
	 * Removes the selected URL when user presses ok in the remove dialog.
	 */
	private void removeSelectedURL() {
		String serverURL = (String) mServerSpinner.getSelectedItem();
		savedServerURLs.remove(serverURL);
		mSpinnerAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Shows the add dialog.
	 */
	private void onClickAddURL() {
		mAddURLDialog.show();
	}
	
	/**
	 * Adds the URL the user has written in the add dialog.
	 * @param serverURL - dialog from the addDialog.
	 */
	private void addServerURL(String serverURL) {
		String formattedURL = formatURLString(serverURL);
		savedServerURLs.add(formattedURL);
		mSpinnerAdapter.notifyDataSetChanged();
		mServerSpinner.setSelection(mSpinnerAdapter.getCount() - 1);
	}
	
	/**
	 * Format the url so it has "http://" in fornt of it if 
	 * it's not exist. Also adds a "/" in the ennd if it not ends with it.
	 * @param url - serverURL.
	 * @return - formated String
	 */
	public static String formatURLString(String url) {
		String formattedURL = url;
		
		if (!formattedURL.startsWith("http://") && !formattedURL.startsWith("https://")) {
			formattedURL = "http://" + formattedURL;
		}
		
		if (!formattedURL.endsWith("/")) {
			formattedURL = formattedURL + "/";
		}
		
		return formattedURL;
	}
	
	/**
	 *  Saves the spinner server infromation that is 
	 *  essential for recreating server spinner. 
	 */
	private void saveToSharedPreferences() {
		int selectedPosition = mServerSpinner.getSelectedItemPosition();
		String allServers = concatServersWithDelimiter();
		String selectedServer = (String) mServerSpinner.getSelectedItem();
		Editor editor = sharedPreferences.edit();
		
		editor.putInt(INDEX_OF_SELECTED_SERVER, selectedPosition);
		editor.putString(ALL_SERVERS, allServers);
		editor.putString(NAME_OF_SELECTED_SERVER, selectedServer);
		editor.commit();
	}
	
	/**
	 * Adds a delimeter so the serer String can be diveded after each URL.
	 * @return
	 */
	private String concatServersWithDelimiter() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < savedServerURLs.size(); i++) {
			sb.append(savedServerURLs.get(i));
			sb.append(DELIMITER);
		}
		
		return sb.toString();
	}
	
	private ArrayList<String> getSavedURLArrayList() {
		String allServers = sharedPreferences.getString(ALL_SERVERS, "");
		
		if (allServers.equals("")) {
			return new ArrayList<String>();
		}
		
		String[] splittedServers = allServers.split(Pattern.quote(DELIMITER));
		
		ArrayList<String> serverURLs = new ArrayList<String>();
		
		for (int i = 0; i < splittedServers.length; i++) {
			serverURLs.add(splittedServers[i]);
		}
		
		return serverURLs;
	}
	
	private int getSavedSelectedIndex() {
		return sharedPreferences.getInt(INDEX_OF_SELECTED_SERVER, 0);	
	}
	
}
	
