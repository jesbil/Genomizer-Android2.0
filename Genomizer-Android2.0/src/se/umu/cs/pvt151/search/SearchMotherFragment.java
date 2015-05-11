package se.umu.cs.pvt151.search;

import se.umu.cs.pvt151.R;
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
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.search_realtabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("regular").setIndicator("regular"),
				SearchRegularFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("pubmed").setIndicator("pubmed"),
				SearchPubmedFragment.class, null);

		return rootView;
	}

}
