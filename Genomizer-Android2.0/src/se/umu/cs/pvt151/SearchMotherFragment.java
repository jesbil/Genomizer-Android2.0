package se.umu.cs.pvt151;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchMotherFragment extends Fragment {
	private FragmentTabHost mTabHost;

	public SearchMotherFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.search_layout_mother,container, false);


		mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("regular").setIndicator("Regular"),
				SearchRegularFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("pubmed").setIndicator("Pubmed"),
				SearchPubmedFragment.class, null);



		return rootView;
	}

}
