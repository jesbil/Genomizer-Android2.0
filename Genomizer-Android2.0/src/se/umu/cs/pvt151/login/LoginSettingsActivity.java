package se.umu.cs.pvt151.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.R.array;
import se.umu.cs.pvt151.R.id;
import se.umu.cs.pvt151.R.layout;
import se.umu.cs.pvt151.R.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginSettingsActivity extends Activity {
	
	private final static String SERVER_PREFERENCES = 
			"se.umu.cs.pvt151.SERVER_PREFERENCES";
	
	private final static String INDEX_OF_SELECTED_SERVER = 
			"indexOfSelectedServer";
	
	private final static String ALL_SERVERS = "allServers";
	
	private final static String DELIMITER = "#";
	
	private Spinner mServerSpinner;
	private List<String> savedServerURLs;
	private ArrayAdapter<String> mSpinnerAdapter;
	
	private EditText mEditURLInput;
	private AlertDialog mEditURLDialog;
	
	private TextView mRemoveURLText;
	private AlertDialog mRemoveURLDialog;
	
	private EditText mAddURLInput;
	private AlertDialog mAddURLDialog;
	
	private SharedPreferences sharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout_settings);
		
		sharedPreferences = getSharedPreferences(SERVER_PREFERENCES, 
				Context.MODE_PRIVATE);

		
		buildServerSpinner();
		buildEditURLDialog();
		buildRemoveURLDialog();
		buildAddURLDialog();
	}
	
	private void buildServerSpinner() {		
		mServerSpinner = (Spinner) 
				 findViewById(R.id.login_settings_spinner_servers);
		
		savedServerURLs = getSavedURLArrayList();
		
		mSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, savedServerURLs);
		
		mSpinnerAdapter.setDropDownViewResource(
				android.R.layout.simple_list_item_single_choice);
		
		mServerSpinner.setAdapter(mSpinnerAdapter);
		
		mServerSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				saveToSharedPreferences();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
				
			}
			
		});
		
		mServerSpinner.setSelection(getSavedSelectedIndex());
	}
	
	private void buildEditURLDialog() {
		mEditURLInput = new EditText(this);
		
		mEditURLDialog = new AlertDialog.Builder(this)
				.setTitle("Edit server")
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
	
	private void buildRemoveURLDialog() {
		mRemoveURLText = new TextView(this);
		mRemoveURLText.setTypeface(null, Typeface.BOLD);
		
		mRemoveURLDialog = new AlertDialog.Builder(this)
				.setTitle("Remove server")
				.setView(mRemoveURLText)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeSelectedURL();	
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
	
	private void buildAddURLDialog() {
		mAddURLInput = new EditText(this);
		
		mAddURLDialog = new AlertDialog.Builder(this)
				.setTitle("Add URL")
				.setView(mAddURLInput)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						addServerURL(mAddURLInput.getText().toString());	
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
	
	public void onClickEditURL(View v) {
		if (!savedServerURLs.isEmpty()) {
			mEditURLInput.setText(mServerSpinner.getSelectedItem().toString());
			mEditURLDialog.show();
		}
	}
	
	private void setEditedURL(String editedURL) {
		String serverURL = (String) mServerSpinner.getSelectedItem();
		
		savedServerURLs.set(savedServerURLs.indexOf(serverURL), formatURLString(editedURL));
 		mSpinnerAdapter.notifyDataSetChanged();
 		
 		saveToSharedPreferences();
	}
	
	public void onClickRemoveURL() {
		if (!savedServerURLs.isEmpty()) {
			mRemoveURLText.setText("Really remove URL " 
					   + mServerSpinner.getSelectedItem()
					   + "?");
			mRemoveURLDialog.show();	
		}

	}
	
	private void removeSelectedURL() {
		String serverURL = (String) mServerSpinner.getSelectedItem();
		savedServerURLs.remove(serverURL);
		mSpinnerAdapter.notifyDataSetChanged();
		
		saveToSharedPreferences();
	}
	
	private void onClickAddURL() {
		mAddURLDialog.show();
	}
	
	private void addServerURL(String serverURL) {
		String formattedURL = formatURLString(serverURL);
		savedServerURLs.add(formattedURL);
		mSpinnerAdapter.notifyDataSetChanged();
		mServerSpinner.setSelection(mSpinnerAdapter.getCount() - 1);
		
		saveToSharedPreferences();
	}
	
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
	
	private void saveToSharedPreferences() {
		int selectedPosition = mServerSpinner.getSelectedItemPosition();
		String allServers = concatServersWithDelimiter();
		Editor editor = sharedPreferences.edit();
		
		editor.putInt(INDEX_OF_SELECTED_SERVER, selectedPosition);
		editor.putString(ALL_SERVERS, allServers);
		editor.commit();
	}
	
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
	
//	/**
//	 * Fetch the currently selected URL from the settings file and mark this URL within
//	 * the Spinner (drop-down) menu.
//	 */
//	private void markCurrentlyUsedURL() {
//		String currentURL = getCurrentlySelectedURL();
//
//		for(int i = 0; i < mSavedURLsList.size(); i++) {
//			if(currentURL.equals(mSavedURLsList.get(i))) {
//				spinner.setSelection(i);
//			}
//		}
//	}
//	
//	/**
//	 * Check if an URL already exists with the 'mSavedURLsList' container.
//	 * @param url The server URL which the user wish to add.
//	 * @return true if the URL already exits, false otherwise.
//	 */
//	private boolean urlExists(String url) {		
//		for(String oldURL : mSavedURLsList) {
//			if(oldURL.equals(url)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * Write the currently selected URL to a settings file to enable persistence between application restarts.
//	 * @param url The server URL to store within the settings file.
//	 */
//	private void saveCurrentlySelectedURL(String url) {
//		SharedPreferences settings = getActivity().getSharedPreferences(getResources().getString(R.string.settings_fileAnchor) ,Context.MODE_PRIVATE);	
//		SharedPreferences.Editor prefEditor = settings.edit();		
//		prefEditor.putString(getResources().getString(R.string.settings_serverSelectedURLAnchor), url);
//		prefEditor.commit();			
//		
//	}
//	
//	/**
//	 * Fetch the server URL that was previously stored within the settings file.
//	 * @return A string representing the URL of the selected server.
//	 */
//	private String getCurrentlySelectedURL() {
//		SharedPreferences settings = this.getSharedPreferences(getResources().getString(R.string.settings_fileAnchor) ,Context.MODE_PRIVATE);	
//		return settings.getString(getResources().getString(R.string.settings_serverSelectedURLAnchor), ComHandler.getServerURL());
//	}
//	
//	/**
//	 * Locate all server URLs that have been saved within the settings file and store them in the 'mSavedURLsList'.
//	 * Also fetch the saved 'currently selected server URL' from the settings file and add it to the 'mSavedURLsList' if it is not
//	 * in the list.
//	 */
//	private void fetchSavedURLs() {		
//		SharedPreferences settings = getActivity().getSharedPreferences(getResources().getString(R.string.settings_fileAnchor) ,Context.MODE_PRIVATE);
//		String savedURLsWithDelimiters = settings.getString(getResources().getString(R.string.settings_serverALLURLAnchor), "#");
//		String[] savedURLs = savedURLsWithDelimiters.split("#");
//		
//		for(String url : savedURLs) {
//			mSavedURLsList.add(url);
//		}
//		
//		String currentURL = getCurrentlySelectedURL();
//		if(!urlExists(currentURL)) {
//			mSavedURLsList.add(currentURL);
//		}
//		String comURL = ComHandler.getServerURL();
//		if(!urlExists(comURL)) {
//			mSavedURLsList.add(comURL);
//		}
//		
//		
//	}
//
//	/**
//	 * Save all server URLs that the user have added to a settings file. The server URLs are stored as a single string with 
//	 * the character '#' as separator.
//	 */
//	public void saveMultipleURLs() {
//		SharedPreferences settings = getActivity().getSharedPreferences(getResources().getString(R.string.settings_fileAnchor), Context.MODE_PRIVATE);	
//		SharedPreferences.Editor prefEditor = settings.edit();
//		
//		StringBuilder sb = new StringBuilder();
//		for(String url : mSavedURLsList) {
//			sb.append(url);
//			sb.append('#');
//		}		
//		
//		prefEditor.putString(getResources().getString(R.string.settings_serverALLURLAnchor), sb.toString());
//		prefEditor.commit();				
//	}
//	
	
	
}
