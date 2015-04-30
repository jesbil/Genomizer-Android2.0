package se.umu.cs.pvt151;

import java.util.ArrayList;
import java.util.Arrays;

import javax.security.auth.callback.CallbackHandler;

import android.R.array;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginSettingsActivity extends Activity {
	
	private Spinner mServerSpinner;
	private ArrayList<String> savedServerURLs;
	private ArrayAdapter<CharSequence> mSpinnerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout_settings);
		
		
		mServerSpinner = (Spinner) 
						 this.findViewById(R.id.login_settings_spinner_servers);
		
	
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
			return true;
		case R.id.login_settings_removeURL:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClickEditURL(View v) {
		editURLDialog();
	}
	
	
	private void editURLDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Edit URL");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setText(mServerSpinner.getSelectedItem().toString());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  setEditedURL(input.getText().toString());
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});
		alert.show();
	}
	
	private void setEditedURL(String editedURL) {
		createSpinnerAdapter();
		String serverURL = (String) mServerSpinner.getSelectedItem();
		Resources res = getResources();
		CharSequence[] temp = res.getStringArray(R.array.login_settings_serverList);
		ArrayList<CharSequence> servers = new ArrayList<CharSequence>(Arrays.asList(temp));
		servers.remove(serverURL);
		servers.add(editedURL);
		mSpinnerAdapter.clear();
 		mSpinnerAdapter.addAll(servers);
 		mSpinnerAdapter.notifyDataSetChanged();
 		mServerSpinner.setSelection(servers.indexOf(editedURL));
	}

	/**
	 * Create an adapter for the Spinner(drop-down) element. Fill the Spinner with all URLs
	 * stored within the 'mSavedURLsList'-list. 
	 */
	private void createSpinnerAdapter() {
		mSpinnerAdapter = ArrayAdapter.createFromResource(this,android.R.layout.simple_spinner_item ,R.array.login_settings_serverList);
		//		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, savedServerURLs);
		mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		mServerSpinner.setAdapter(mSpinnerAdapter);
		mSpinnerAdapter.notifyDataSetChanged();

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
