package se.umu.cs.pvt151.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.model.GeneFile;
import se.umu.cs.pvt151.process.ProcessFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.TextView;

public class SearchResultExperimentFragment extends Fragment {

	private ListView mRawListView;
	private ListView mProfileListView;
	private ListView mRegionListView;

	private HashMap<GeneFile, Boolean> selectedFilesMap;

	private ArrayList<GeneFile> rawDataFiles;
	private ArrayList<GeneFile> profileDataFiles;
	private ArrayList<GeneFile> regionDataFiles;
	private Button mAddToSelectionButton;
	


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
		selectedFilesMap = new HashMap<GeneFile, Boolean>();
		initiateFileSelectedMap();
	}

	private void initiateFileSelectedMap() {
		for(GeneFile rawFile : rawDataFiles){
			selectedFilesMap.put(rawFile, false);
		}
		for(GeneFile profileFile : profileDataFiles){
			selectedFilesMap.put(profileFile, false);
		}
		for(GeneFile regionFile : regionDataFiles){
			selectedFilesMap.put(regionFile, false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.search_layout_experimentfiles, container, false);

		mRawListView = (ListView) rootView.findViewById(R.id.search_lv_rawList);
		mRawListView.setAdapter(new FileListAdapter(rawDataFiles));

		mProfileListView = (ListView) rootView.findViewById(R.id.search_lv_profileList);
		mProfileListView.setAdapter(new FileListAdapter(profileDataFiles));

		mRegionListView = (ListView) rootView.findViewById(R.id.search_lv_regionList);
		mRegionListView.setAdapter(new FileListAdapter(regionDataFiles));

		
		mAddToSelectionButton = (Button) rootView.findViewById(R.id.search_btn_addToSelection);
		mAddToSelectionButton.setEnabled(!rawDataFiles.isEmpty());
		mAddToSelectionButton.setOnClickListener(new OnClickListener() {
			// when "add to selection" is pressed
			@Override
			public void onClick(View v) {
				ArrayList<String> rawFileIds = new ArrayList<String>();
				ArrayList<String> profileFileIds = new ArrayList<String>();
				ArrayList<String> regionFileIds = new ArrayList<String>();
				
				for(GeneFile rawFile : rawDataFiles){
					if(selectedFilesMap.get(rawFile)){
						rawFileIds.add(rawFile.getFileId());
					}
				}
				for(GeneFile profileFile : profileDataFiles){
					if(selectedFilesMap.get(profileFile)){
						profileFileIds.add(profileFile.getFileId());
					}
				}
				for(GeneFile regionFile : regionDataFiles){
					if(selectedFilesMap.get(regionFile)){
						regionFileIds.add(regionFile.getFileId());
					}
				}
				Context context = getActivity();
				Resources res = context.getResources();
				String rawFileKey = res.getString(R.string.rawFiles);
				String profileFileKey = res.getString(R.string.profileFiles);
				String regionFileKey = res.getString(R.string.regionFiles);
				String FILE_IDS = res.getString(R.string.FILE_IDS);
				SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_IDS, Context.MODE_PRIVATE);
				Editor sharedPrefsEditor = sharedPrefs.edit();
				sharedPrefsEditor.putStringSet(rawFileKey, new HashSet<String>(rawFileIds)).commit();
				sharedPrefsEditor.putStringSet(profileFileKey, new HashSet<String>(profileFileIds)).commit();
				sharedPrefsEditor.putStringSet(regionFileKey, new HashSet<String>(regionFileIds)).commit();
				
				Bundle b = new Bundle();
				b.putParcelableArrayList(ProcessFragment.FILES_KEY, rawDataFiles);
				
				Fragment fragment = new ProcessFragment();
				fragment.setArguments(b);
				getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();
			}
		});
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
		private ArrayList<GeneFile> files;

		/**
		 * Constructor for the FileListAdapter
		 * @param fileNames with the values to
		 * be displayed in list view. 
		 */
		public FileListAdapter(ArrayList<GeneFile> files) {			
			super(getActivity(), 0, files);	
			this.files = files;

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
				view = mInflater.inflate(R.layout.search_layout_experimentfiles_list_element, null);
			}
			TextView mFileName = (TextView) view.findViewById(R.id.search_experimentfiles_tv_fileName);
			CheckBox mFileCheckBox = (CheckBox) view.findViewById(R.id.search_experimentfiles_cb_fileCheckBox);

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
					selectedFilesMap.put(files.get(position), !selectedFilesMap.get(files.get(position)));
				}
			});

			return view;
		}
	}
}
