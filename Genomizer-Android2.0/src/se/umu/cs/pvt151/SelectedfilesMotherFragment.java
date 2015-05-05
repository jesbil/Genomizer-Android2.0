package se.umu.cs.pvt151;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelectedfilesMotherFragment extends Fragment {
	private FragmentTabHost mTabHost;

	public SelectedfilesMotherFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.selectedfiles_layout_mother,container, false);


		mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.selectedfiles_realtabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("raw").setIndicator("raw"),
				SelectedfilesRawFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("profile").setIndicator("profile"),
				SelectedfilesProfileFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("region").setIndicator("region"),
				SelectedfilesRegionFragment.class, null);


		return rootView;
	}
}
