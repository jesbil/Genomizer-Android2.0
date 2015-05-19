package se.umu.cs.pvt151.selected_files;

import java.io.IOException;
import java.util.ArrayList;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.com.ComHandler;
import se.umu.cs.pvt151.model.GeneFile;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SelectedfilesRegionFragment extends Fragment {

	private ListView mFileList;

	private FileListAdapter listAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
				
		View rootView = inflater.inflate(R.layout.selectedfiles_layout_region,container, false);
		mFileList = (ListView) rootView.findViewById(R.id.selectedfiles_lv_region);
		
		new FileTask().execute();
		
		
		return rootView;
	}
	
	/**
	 * AsyncTask for connecting to the server and fetch files.
	 * 
	 * @author Anders Lundberg, dv12alg 
	 * @author Erik Åberg, c11ean
	 *
	 */
	private class FileTask extends AsyncTask<Void, Void, ArrayList<GeneFile>> {
		

		/**
		 * Connects to the server, collects annotation data from the database
		 * and sets the retrieved values into corresponding lists.
		 */
		@Override
		protected ArrayList<GeneFile> doInBackground(Void... params) {
			Resources res = getActivity().getResources();
			String regionFileKey = res.getString(R.string.regionFiles);		
			String FILE_IDS = res.getString(R.string.FILE_IDS);
			ArrayList<GeneFile> regionFiles = new ArrayList<GeneFile>();
			SharedPreferences sharedPrefs = getActivity().getSharedPreferences(FILE_IDS, Context.MODE_PRIVATE);
			
			ArrayList<String> regionFileIds = new ArrayList<String>();
			if(sharedPrefs.getStringSet(regionFileKey, null)!=null){
				regionFileIds = new ArrayList<String>(sharedPrefs.getStringSet(regionFileKey, null));
			}
			
			for(String fileId : regionFileIds){
				Log.d("SELECTEDregion", fileId);
				try {
					GeneFile file = ComHandler.getFile(fileId);
					if(file!=null){
						regionFiles.add(file);
					}else{
						Log.d("SELECTEDregion", "file " +  fileId + " not found");
					}
				} catch (IOException e) {
				}
			}
			return regionFiles;
			
		}
		
		/**
		 * After the collection of data is complete setup the adapter with the
		 * information about files found in the database contacted.
		 */
		@Override
		protected void onPostExecute(ArrayList<GeneFile> result) {
			listAdapter = new FileListAdapter(result, getActivity());
			mFileList.setAdapter(listAdapter);
		}
		
	}
	
}
