package se.umu.cs.pvt151;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SearchRegularFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.search_layout_regular,container, false);
		
		Button mSearchButton = (Button) rootView.findViewById(R.id.search_regular_btn_search);
		mSearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		return rootView;
	}
	
}
