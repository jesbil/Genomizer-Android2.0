package se.umu.cs.pvt151.search;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.pvt151.R;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchResultSettingsFragment extends Fragment {
	private ListView mAnnotationList;
	private Spinner mAnnotationSortBySpinner;
	private Button mCheckAllAnnotationsButton;
	private Button mUncheckAllAnnotationsButton;
	private ArrayList<String> annotationNames;
	private HashMap<String,Boolean> visibleAnnotation;
	private AnnotationListAdapter annotationListAdapter;
	private String sortBy;
	
	@SuppressWarnings("unchecked") // serializable to HashMap<String,Boolean>
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Resources res = getActivity().getResources();
		String annotationsKey = res.getString(R.string.annotationsKey);
		String visibleAnnotationsKey = res.getString(R.string.visibleAnnotationsKey);
		String annotationSortByKey = res.getString(R.string.annotationSortByKey);
		Bundle bundle = getArguments();
		annotationNames = bundle.getStringArrayList(annotationsKey);
		visibleAnnotation = (HashMap<String,Boolean>) bundle.getSerializable(visibleAnnotationsKey);
		sortBy = bundle.getString(annotationSortByKey);
		
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.search_layout_experimentlistsettings,container, false);
		
		mAnnotationList = (ListView) rootView.findViewById(R.id.search_lv_resultAnnotationsSettings);
		mAnnotationSortBySpinner = (Spinner) rootView.findViewById(R.id.searchresult_settings_spnr_sortBySpinner);
		mCheckAllAnnotationsButton = (Button) rootView.findViewById(R.id.searchresult_settings_btn_checkAllAnnotations);
		mUncheckAllAnnotationsButton = (Button) rootView.findViewById(R.id.searchresult_settings_btn_uncheckAllAnnotations);
		
		annotationListAdapter = new AnnotationListAdapter(annotationNames);
		mAnnotationList.setAdapter(annotationListAdapter);
		
		mAnnotationSortBySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, annotationNames));
		if(sortBy!=null){
			mAnnotationSortBySpinner.setSelection(annotationNames.indexOf(sortBy));
			
		}
		mCheckAllAnnotationsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkAllAnnotations();
				annotationListAdapter.notifyDataSetChanged();
			}
		});
		
		mUncheckAllAnnotationsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uncheckAllAnnotations();
				annotationListAdapter.notifyDataSetChanged();
			}
		});		
		
		return rootView;
	}
	
 	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}
	
	@Override
	public void onDestroyView() {
		sortBy = mAnnotationSortBySpinner.getSelectedItem().toString();
		SearchResultFragment.setSortBy(sortBy);
		super.onDestroyView();
	}
	
	private void uncheckAllAnnotations() {
		for(String annotationName : annotationNames){
			visibleAnnotation.put(annotationName, false);
		}
	}
	
	private void checkAllAnnotations() {
		for(String annotationName : annotationNames){
			visibleAnnotation.put(annotationName, true);
		}
	}
	
	private class AnnotationListAdapter extends ArrayAdapter<String> {
		

		/**
		 * Constructor for the FileListAdapter
		 * @param fileNames with the values to
		 * be displayed in list view. 
		 */
		public AnnotationListAdapter(ArrayList<String> annotations) {			
			super(getActivity(), 0, annotations);	
		}
		
		/**
		 * Returns the view of an object in the listview at specified position.
		 * This method is called by the system to build and visualize the
		 * listview.
		 */
		@Override
		public View getView(final int position, View view, ViewGroup parent) {

			if (view == null) {
				LayoutInflater mInflater = (LayoutInflater)
						getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				view = mInflater.inflate(R.layout.search_layout_experminetlistsettings_element, null);
			}
			TextView mAnnotationName = (TextView) view.findViewById(R.id.searchresult_settings_tv_annotationName);
			CheckBox mAnnotationCheckBox = (CheckBox) view.findViewById(R.id.searchresult_settings_cb_annotationCheckBox);

			mAnnotationName.setText(annotationNames.get(position));
			mAnnotationCheckBox.setChecked(visibleAnnotation.get(annotationNames.get(position)));


			mAnnotationCheckBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					visibleAnnotation.put(annotationNames.get(position), !visibleAnnotation.get(annotationNames.get(position)));
				}
			});

			return view;
		}
	}
	

	
}
