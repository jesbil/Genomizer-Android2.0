package se.umu.cs.pvt151;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelectedfilesRegionFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.selectedfiles_layout_region,container, false);
		
		return rootView;
	}
	
}
