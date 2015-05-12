package se.umu.cs.pvt151.login;

import java.util.ArrayList;
import java.util.List;
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

		
		//buildServerSpinner();
		buildEditURLDialog();
		buildRemoveURLDialog();
		buildAddURLDialog();
	}
	
	@Override
	public void onPause() {
		saveToSharedPreferences();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		buildServerSpinner();
		super.onResume();
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
		mAddURLInput.setText("http://");
		
		mAddURLDialog = new AlertDialog.Builder(this)
				.setTitle("Add URL")
				.setView(mAddURLInput)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						addServerURL(mAddURLInput.getText().toString());
						mAddURLInput.setText("http://");
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
	}
	
	private void onClickAddURL() {
		mAddURLDialog.show();
	}
	
	private void addServerURL(String serverURL) {
		String formattedURL = formatURLString(serverURL);
		savedServerURLs.add(formattedURL);
		mSpinnerAdapter.notifyDataSetChanged();
		mServerSpinner.setSelection(mSpinnerAdapter.getCount() - 1);
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
		String selectedServer = (String) mServerSpinner.getSelectedItem();
		Editor editor = sharedPreferences.edit();
		
		editor.putInt(INDEX_OF_SELECTED_SERVER, selectedPosition);
		editor.putString(ALL_SERVERS, allServers);
		editor.putString(NAME_OF_SELECTED_SERVER, selectedServer);
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
	
}
	
