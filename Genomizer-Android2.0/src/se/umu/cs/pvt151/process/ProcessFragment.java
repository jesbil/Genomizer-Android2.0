package se.umu.cs.pvt151.process;

import java.io.IOException;
import java.util.ArrayList;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.com.ComHandler;
import se.umu.cs.pvt151.model.GeneFile;
import se.umu.cs.pvt151.model.GenomeRelease;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ProcessFragment extends Fragment {
	
	public final static String FILES_KEY = "filesKey";
	
	private ListView mBowtieListView;
	private ArrayAdapter<RawToProfileParameters> mAdapter;
	
	private ArrayList<GeneFile> geneFiles;
	private ArrayList<String> geneFileNames;
	
	private ArrayList<GenomeRelease> genomeReleases;
	private ArrayList<String> genomeReleaseNames;
	
	public ProcessFragment() {
		
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.process_layout_all_parameters,container, false);
	
		mBowtieListView = (ListView) rootView.findViewById(R.id.process_lv_bowtie);
		
		Bundle b = getArguments();
		geneFiles = b.getParcelableArrayList(FILES_KEY);
		
		new GenomeReleaseTask(getActivity()).execute();
		
		return rootView;
	}
	
	private void buildBowtieListViewAdapter() {
		genomeReleaseNames = new ArrayList<String>();
		geneFileNames = new ArrayList<String>();
		
		for (int i = 0; i < genomeReleases.size(); i++) {
			genomeReleaseNames.add(genomeReleases.get(i).getGenomeVersion());
		}
		
		for (int i = 0; i < geneFiles.size(); i++) {
			geneFileNames.add(geneFiles.get(i).getName());
		}
		
		mAdapter = new RawToProfileAdapter(getActivity(),new ArrayList<RawToProfileParameters>());
		mBowtieListView.setAdapter(mAdapter);
		addNewBowtieListItem();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.process_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.process_action_add){
			addNewBowtieListItem();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void addNewBowtieListItem() {
		mAdapter.add(new RawToProfileParameters(geneFileNames, genomeReleaseNames));
		mAdapter.notifyDataSetChanged();
	}
	
	private class GenomeReleaseTask extends AsyncTask<Void, Void, ArrayList<GenomeRelease>> {
		
		private final static String TITLE = "Loading";
		private final static String MESSAGE = "Downloading genome releases...";
		
		private IOException ioe;
		
		private ProgressDialog mProgressDialog;
		private Context mContext;
		
		public GenomeReleaseTask(Context context) {
			mContext = context;
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle(TITLE);
			mProgressDialog.setMessage(MESSAGE);
			mProgressDialog.show();
		}
		
		@Override
		protected ArrayList<GenomeRelease> doInBackground(Void... params) {
			try {
				return ComHandler.getGenomeReleases();
			} catch (IOException ioe) {
				this.ioe = ioe;
				return new ArrayList<GenomeRelease>();
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<GenomeRelease> result) {
			mProgressDialog.dismiss();
			
			if (ioe != null) {
				Toast.makeText(mContext, "blabla exception", Toast.LENGTH_LONG).show();
			}
			
			genomeReleases = result;
			buildBowtieListViewAdapter();
		}
	}

}
