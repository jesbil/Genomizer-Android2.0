package se.umu.cs.pvt151.search;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.model.GeneFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class SearchResultExperimentFragment extends Fragment {

	private static final String RAW = "raw";
	private static final String PROFILE = "profile";
	private static final String REGION = "region";

	private ListView rawListView;
	private ListView profileListView;
	private ListView regionListView;

	private HashMap<GeneFile, Boolean> fileSelectedMap;

	private ArrayList<GeneFile> rawDataFiles;
	private ArrayList<GeneFile> profileDataFiles;
	private ArrayList<GeneFile> regionDataFiles;

	/**
	 * Used to create holder for
	 * values used in get view method
	 *
	 */
	static class listViewHolder {
		protected TextView fileName;
		protected CheckBox fileCheckBox;
		protected ArrayList<String> forChecks;
		protected View convertView;
	}

	public SearchResultExperimentFragment(ArrayList<GeneFile> rawFiles, ArrayList<GeneFile> profileFiles, ArrayList<GeneFile> regionFiles) {
		rawDataFiles = rawFiles;
		profileDataFiles = profileFiles;
		regionDataFiles = regionFiles;
		fileSelectedMap = new HashMap<GeneFile, Boolean>();
		initiateFileSelectedMap();
	}

	private void initiateFileSelectedMap() {
		for(GeneFile rawFile : rawDataFiles){
			fileSelectedMap.put(rawFile, false);
		}
		for(GeneFile profileFile : profileDataFiles){
			fileSelectedMap.put(profileFile, false);
		}
		for(GeneFile regionFile : regionDataFiles){
			fileSelectedMap.put(regionFile, false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.search_layout_experimentfiles, container, false);

		rawListView = (ListView) rootView.findViewById(R.id.search_lv_rawList);
		rawListView.setAdapter(new FileListAdapter(rawDataFiles, RAW));

		profileListView = (ListView) rootView.findViewById(R.id.search_lv_profileList);
		profileListView.setAdapter(new FileListAdapter(profileDataFiles, PROFILE));

		regionListView = (ListView) rootView.findViewById(R.id.search_lv_regionList);
		regionListView.setAdapter(new FileListAdapter(regionDataFiles, REGION));

		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}


	/**
	 * Adapter used for list views.
	 * Got onClick for check boxes that
	 * detects and add/remove files depending on
	 * if box is checked or not. Got onClick for
	 * text view used to display extra information
	 * about a file in the list if it's clicked. 
	 *
	 */
	private class FileListAdapter extends ArrayAdapter<GeneFile> {
		private String dataType;
		private ArrayList<GeneFile> files;

		/**
		 * Constructor for the FileListAdapter
		 * @param fileNames with the values to
		 * be displayed in list view. 
		 * @param dataType (raw, profile, region) used
		 * to decide which list view to work with.
		 */
		public FileListAdapter(ArrayList<GeneFile> files, String dataType) {			
			super(getActivity(), 0, files);	
			this.dataType = dataType;
			this.files = files;

		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater)
						getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.search_layout_experimentfiles_list_element, null);
			}
			TextView mFileName = (TextView) convertView.findViewById(R.id.search_experimentfiles_tv_fileName);
			CheckBox mFileCheckBox = (CheckBox) convertView.findViewById(R.id.search_experimentfiles_cb_fileCheckBox);

			mFileName.setText(files.get(position).getName());
			mFileCheckBox.setChecked(false);


			mFileName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder build = new AlertDialog.Builder(getActivity());		
					build.setTitle(files.get(position).getName());
					build.setMessage(files.get(position).toString());
					build.setNeutralButton("OK", null);
					build.show();
				}
			});

			mFileCheckBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
						fileSelectedMap.put(files.get(position), !fileSelectedMap.get(files.get(position)));
				}
			});

			return convertView;
		}
	}
}
