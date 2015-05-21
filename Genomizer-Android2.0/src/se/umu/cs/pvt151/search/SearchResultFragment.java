package se.umu.cs.pvt151.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.com.ComHandler;
import se.umu.cs.pvt151.model.Annotation;
import se.umu.cs.pvt151.model.Experiment;
import se.umu.cs.pvt151.model.GeneFile;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultFragment extends Fragment {
	
	protected final static String SEARCH_VALUES = "searchValues";
	protected final static String ANNOTATION_NAMES = "annotationNames";
	
	private static final String DOWNLOADING_SEARCH_RESULTS = "Downloading search results";
	private HashMap<String, String> searchValues;
	private ArrayList<String> annotationNamesList;
	private ProgressDialog loadScreen;
	private SearchHandler startSearch;
	private ArrayList<Experiment> forExperiments;
	private ArrayList<String> displaySearchResult;
	private ListView experimentListView;
	
	private ArrayList<GeneFile> rawFiles;
	private ArrayList<GeneFile> profileFiles;
	private ArrayList<GeneFile> regionFiles;


	public SearchResultFragment() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		Bundle bundle = getArguments();
		this.searchValues = (HashMap<String, String>) 
				bundle.getSerializable(SEARCH_VALUES);
		
		this.annotationNamesList = bundle.getStringArrayList(ANNOTATION_NAMES);
		
		View rootView = inflater.inflate(R.layout.search_layout_experimentlist,
				container, false);
		
		experimentListView = (ListView) 
				rootView.findViewById(R.id.search_lv_searchResultList);
		
		rawFiles = new ArrayList<GeneFile>();
		profileFiles = new ArrayList<GeneFile>();
		regionFiles = new ArrayList<GeneFile>();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		startSearch = new SearchHandler();
		showLoadScreen(DOWNLOADING_SEARCH_RESULTS);
		startSearch.execute();
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_result_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.search_result_settings){
			Fragment fragment = new SearchResultSettingsFragment();
			getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * Displays a loading screen for the user, while downloading data from the
	 * server. Must be manually dismissed when data transfer is done.
	 * 
	 * @param msg the message to display to the user
	 */
	private void showLoadScreen(String msg) {
		loadScreen = new ProgressDialog(getActivity());
		loadScreen.setTitle("Loading");
		loadScreen.setMessage(msg);
		loadScreen.show();
	}
	
	public ArrayList<String> getDisplayValues(ArrayList<Experiment> forExperiments) {
		ArrayList<String> searchResult = new ArrayList<String>();
		for(Experiment experiment : forExperiments){
			List<Annotation> annotations = experiment.getAnnotations();
			String temp = "";
			for(String annotationName : annotationNamesList){
				for(Annotation annotation : annotations){
					if(annotationName.equals(annotation.getName())){
						temp = temp + annotation.getName() +" "+
					           annotation.getValue().toString()+"\n";	
					}
				}
			}
			searchResult.add("Experiment "+ experiment.getName() +"\n"+temp);
			
		}
		return searchResult;
	}
	
	/**
	 * SearchHandler
	 * ASyncTask to receive information
	 * from server, performed in background.
	 * @author Cecilia Lindmark
	 *
	 */
	private class SearchHandler extends AsyncTask<Void, Void, 
		ArrayList<Experiment>> {

		//@Override
		protected ArrayList<Experiment> doInBackground(Void...arg0) {
		
		try {
			/*If search string is null the HashMap with annotation is
			 * used to get search results from the server. Else if
			 * the search string is valid that string is used for 
			 * receiving search results instead.*/
//				if(searchString == null) {
					forExperiments = ComHandler.search(searchValues);
//				} else {
//					forExperiments = ComHandler.search(searchString);
//				}
			} catch (IOException e) {
				//TODO server communication failed
			} 
			return forExperiments;
		}
		
		protected void onPostExecute(ArrayList<Experiment> forExperiments) {
			/*Creating list with right looking information
			 * used to be displayed in search.*/
			loadScreen.dismiss();
			// TODO kolla inställningarna vilka annotationer som ska visas
			
			displaySearchResult = getDisplayValues(forExperiments);

			//Creating adapter for displaying search results
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity().getApplicationContext(), 
					R.layout.search_layout_experimentlist_text_view,
					R.id.search_tv_experiment, 
					displaySearchResult);	
			//Setting adapter to view
			experimentListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			//Set onItemclicklistener to list, used to detect clicks
			experimentListView.setOnItemClickListener(new ListHandler());
			
		}
	}



	
	/**
	 * Listener used to detect what happens
	 * when user clicks on an experiment in
	 * the search result list.
	 * @author Cecilia Lindmark
	 *
	 */
	private class ListHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> Adapter, View view, int position,
				long arg3) {
			//Getting list of files belonging to experiment
			setExperimentFiles(position);
			//Creating new intent for moving to FileListActivity
			
			Fragment fragment = new SearchResultExperimentFragment(rawFiles, profileFiles, regionFiles);
			getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();
		}
		
		/**
		 * Method used to get all different data files 
		 * for a specific experiment in separate lists.
		 * @param selectedExperiment position for experiment chosen
		 */
		private void setExperimentFiles(int selectedExperiment) {
			//Getting all files for a selected experiment
			List<GeneFile> files = forExperiments.get(
					selectedExperiment).getFiles();
			/*Sorting the files in right lists, all raw in one,
			 * all profile in one, all region in one.*/
			for(int i=0; i<files.size(); i++) {
				if(files.get(i).getType().equals("Raw")) {
					rawFiles.add(files.get(i));
				} else if(files.get(i).getType().equals("Profile")) {
					profileFiles.add(files.get(i));
				} else if(files.get(i).getType().equals("Region")) {
					regionFiles.add(files.get(i));
				}
			}
		}
		
	}
	
}
