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
import android.content.res.Resources;
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
	protected final static String PUBMED_QUERY = "pubmedquery";

	private static final String DOWNLOADING_SEARCH_RESULTS = "Downloading search results";
	
	private HashMap<String, String> searchValues;
	private ArrayList<String> annotationNamesList;
	private String pubmedQuery;
	
	private ProgressDialog loadScreen;
	private SearchHandler startSearch;
	private ArrayList<Experiment> experiments;
	private ArrayList<String> displaySearchResult;
	private ListView experimentListView;
	private ArrayAdapter<String> experimentListAdapter;
	

	private ArrayList<GeneFile> rawFiles;
	private ArrayList<GeneFile> profileFiles;
	private ArrayList<GeneFile> regionFiles;

	private static HashMap<String,Boolean> visibleAnnotations;
	private static String sortBy;


	public SearchResultFragment() {
	}

	@SuppressWarnings("unchecked") // serialized to hashMap<String, String)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		Bundle bundle = getArguments();
		this.searchValues = (HashMap<String, String>) 
				bundle.getSerializable(SEARCH_VALUES);

		this.pubmedQuery = bundle.getString(PUBMED_QUERY);
		
		this.annotationNamesList = bundle.getStringArrayList(ANNOTATION_NAMES);
		visibleAnnotations = new HashMap<String,Boolean>();
		for(String annotationName : annotationNamesList){
			visibleAnnotations.put(annotationName, true);
		}
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_result_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.search_result_settings){
			Bundle bundle = new Bundle();
			Resources res = getActivity().getResources();
			String annotationsKey = res.getString(R.string.annotationsKey);
			String visibleAnnotationsKey = res.getString(R.string.visibleAnnotationsKey);
			String annotationSortByKey = res.getString(R.string.annotationSortByKey);
			bundle.putStringArrayList(annotationsKey, annotationNamesList);
			bundle.putSerializable(visibleAnnotationsKey, visibleAnnotations);
			bundle.putString(annotationSortByKey, sortBy);
			Fragment fragment = new SearchResultSettingsFragment();
			fragment.setArguments(bundle);
			getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ArrayList<Experiment> sortExperimentList(ArrayList<Experiment> experimentsToSort,String sortBy){
		if(sortBy==null){
			return experimentsToSort;
		}

		ArrayList<Experiment> result = new ArrayList<Experiment>();
		
		for(Experiment experimentToSort : experimentsToSort){
			for(Annotation annotationToSort : experimentToSort.getAnnotations()){
				if(annotationToSort.getName().equals(sortBy)){
					boolean added = false;
					resultloop:
						for(Experiment experiment : result ){
							for(Annotation annotation : experiment.getAnnotations()){
								if(annotation.getName().equals(sortBy)){
									if(annotationToSort.getValue().get(0).compareToIgnoreCase(annotation.getValue().get(0))>=0){
										result.add(result.indexOf(experiment), experimentToSort);
										Log.d("ADDED 1",experimentToSort.getName());
										added = true;
										break resultloop;
									}
								}
							}
						}if(!added){
							result.add(experimentToSort);
							Log.d("ADDED 2",experimentToSort.getName());
						}
				}
			}
		}
		for(Experiment experimentToSort : experimentsToSort){
			if(!result.contains(experimentToSort)){
				result.add(experimentToSort);
				Log.d("ADDED 3",experimentToSort.getName());
			}
		}
		return reverseExperiments(result);
	}

	private ArrayList<Experiment> reverseExperiments(ArrayList<Experiment> experiments){
		ArrayList<Experiment> reversed = new ArrayList<Experiment>();
		for(int i=experiments.size()-1; i>=0; i--){
			reversed.add(experiments.get(i));
		}
		return reversed;

	}

	protected static void setSortBy(String by){
		sortBy=by;
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

	private ArrayList<String> getDisplayValues(ArrayList<Experiment> forExperiments) {
		ArrayList<String> searchResult = new ArrayList<String>();
		for(Experiment experiment : forExperiments){
			List<Annotation> annotations = experiment.getAnnotations();
			String temp = "";
			for(String annotationName : annotationNamesList){
				boolean found=false;
				if(visibleAnnotations.get(annotationName)){
					for(Annotation annotation  : annotations){
						if(annotation.getName().equals(annotationName)){
							temp = temp + annotationName+" ["+annotation.getValue().get(0)	+"]\n";
							found=true;
						}

					}
					if(!found){
						temp = temp + annotationName+" [ - ]\n";

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

		@Override
		protected ArrayList<Experiment> doInBackground(Void...arg0) {

			try {
				if(searchValues!=null){
					experiments = ComHandler.search(searchValues);	
				}else if(pubmedQuery!=null){
					experiments = ComHandler.search(pubmedQuery);
				}

			} catch (IOException e) {
				//TODO server communication failed
			} 
			return experiments;
		}

		protected void onPostExecute(ArrayList<Experiment> experiments) {
			/*Creating list with right looking information
			 * used to be displayed in search.*/

			ArrayList<Experiment> sortedExperiments = sortExperimentList(experiments, sortBy);
			displaySearchResult = getDisplayValues(sortedExperiments);

			//Creating adapter for displaying search results
			experimentListAdapter = new ArrayAdapter<String>(
					getActivity().getApplicationContext(), 
					R.layout.search_layout_experimentlist_text_view,
					R.id.search_tv_experiment, 
					displaySearchResult);	
			//Setting adapter to view
			experimentListView.setAdapter(experimentListAdapter);
			experimentListAdapter.notifyDataSetChanged();
			loadScreen.dismiss();
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
			List<GeneFile> files = experiments.get(
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
