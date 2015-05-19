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

public class SelectedfilesProfileFragment extends Fragment {

	private ListView mFileList;

	private FileListAdapter listAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
				
		View rootView = inflater.inflate(R.layout.selectedfiles_layout_profile,container, false);
		mFileList = (ListView) rootView.findViewById(R.id.selectedfiles_lv_profile);
		
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
			String profileFileKey = res.getString(R.string.profileFiles);		
			String FILE_IDS = res.getString(R.string.FILE_IDS);
			ArrayList<GeneFile> profileFiles = new ArrayList<GeneFile>();
			SharedPreferences sharedPrefs = getActivity().getSharedPreferences(FILE_IDS, Context.MODE_PRIVATE);
			
			ArrayList<String> profileFileIds = new ArrayList<String>();
			if(sharedPrefs.getStringSet(profileFileKey, null)!=null){
				profileFileIds = new ArrayList<String>(sharedPrefs.getStringSet(profileFileKey, null));
			}
			
			for(String fileId : profileFileIds){
				Log.d("SELECTEDPROFILE", fileId);
				try {
					GeneFile file = ComHandler.getFile(fileId);
					if(file!=null){
						profileFiles.add(file);
					}else{
						Log.d("SELECTEDPROFILE", "file " +  fileId + " not found");
					}
				} catch (IOException e) {
				}
			}
			return profileFiles;
			
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
