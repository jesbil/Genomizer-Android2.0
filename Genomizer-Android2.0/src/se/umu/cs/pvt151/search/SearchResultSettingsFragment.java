package se.umu.cs.pvt151.search;

import se.umu.cs.pvt151.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchResultSettingsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.search_layout_experimentlistsettings,container, false);
		
		return rootView;
	}
	 
}