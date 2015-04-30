package se.umu.cs.pvt151;

import java.util.ArrayList;
import java.util.Arrays;

import android.R.array;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class LoginSettingsActivity extends Activity {
	
	private Spinner mServerSpinner;
	private ArrayAdapter<CharSequence> spinnerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout_settings);
		mServerSpinner = (Spinner) 
						 this.findViewById(R.id.login_settings_spinner_servers);
		spinnerAdapter = ArrayAdapter.createFromResource(this,android.R.layout.simple_spinner_item ,R.array.login_settings_serverList);
	
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
		String serverURL = (String) mServerSpinner.getSelectedItem();
		Resources res = getResources();
		String[] temp = res.getStringArray(R.array.login_settings_serverList);
		ArrayList<String> servers = new ArrayList<String>(Arrays.asList(temp));
		
		
				
		spinnerAdapter.clear();
 		spinnerAdapter.addAll(servers);
 		spinnerAdapter.notifyDataSetChanged();
		
	}
}
