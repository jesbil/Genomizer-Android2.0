package se.umu.cs.pvt151.processStatus;

import java.io.IOException;
import java.util.ArrayList;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.com.ComHandler;
import se.umu.cs.pvt151.model.ProcessStatus;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProcessStatusFragment extends Fragment {
	private static final String DOWNLOADING_PROCESSING_INFORMATION = "Downloading processing information";

	private ListView processList;
	private ArrayList<ProcessStatus> processes;
	private ProgressDialog loadScreen;
	private ProcessListAdapter processListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		processes = new ArrayList<ProcessStatus>();

		View rootView = inflater.inflate(R.layout.processstatus_layout_processlist,container, false);

		processList = (ListView) rootView.findViewById(R.id.process_status_listv_processList);


		Button refreshButton = (Button) rootView.findViewById(R.id.process_status_btn_refresh);

		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				processes.clear();
				showLoadScreen(DOWNLOADING_PROCESSING_INFORMATION);
				new ProcessFetchAsyncTask().execute();

			}
		});

		return rootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		processes.clear();
		showLoadScreen(DOWNLOADING_PROCESSING_INFORMATION);
		new ProcessFetchAsyncTask().execute();
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

	private class ProcessFetchAsyncTask extends AsyncTask<Void, Void, Void>{

		private IOException exception;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				processes = ComHandler.getProcesses();
				Log.d("PROCESSSTATUS", "NR OF PROCESSES: "+processes.size());
				for(ProcessStatus ps : processes){
					Log.d("processSTATUS", ps.getExperimentName());
				}
			} catch (IOException e) {
				exception = e;
			}
			return null;
		}

		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			loadScreen.dismiss();
			if(exception==null){
				processListAdapter = new ProcessListAdapter(processes, getActivity());
				processList.setAdapter(processListAdapter);
			}else{
				Log.e("EXCEPTIONIIONOMIONIN",exception.getMessage());
			}
		}

	}

	private class ProcessListAdapter extends ArrayAdapter<ProcessStatus> {
		private ArrayList<ProcessStatus> processes;
		private Context context;

		public ProcessListAdapter(ArrayList<ProcessStatus> processes, Context context) {
			super(context, 0, processes);
			this.processes = processes;
			this.context = context;
		}


		/**
		 * Returns the view of an object in the listview at specified position.
		 * This method is called by the system to build and visualize the
		 * listview.
		 */
		@Override
		public View getView(final int position, View view, ViewGroup parent) {

			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.processstatus_layout_processlist_element, null);
			}
			
			ImageView mDeleteProcess = (ImageView) view.findViewById(R.id.process_status_iv_delete);
			mDeleteProcess.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new ProcessHaltTask(processes.get(position)).execute();
					
				}
			});
			
			TextView mExperimentName = (TextView) view.findViewById(R.id.process_status_tv_experiment);
			mExperimentName.setText(processes.get(position).getExperimentName());
			
			TextView mAuthor = (TextView) view.findViewById(R.id.process_status_tv_author);
			mAuthor.setText(processes.get(position).getAuthor());
			
			TextView mTimeAdded = (TextView) view.findViewById(R.id.process_status_tv_timeAdded);
			mTimeAdded.setText(String.valueOf(processes.get(position).getTimeAdded()));
			
			TextView mTimeStarted = (TextView) view.findViewById(R.id.process_status_tv_timeStarted);
			mTimeStarted.setText(String.valueOf(processes.get(position).getTimeStarted()));
			
			TextView mTimeFinished = (TextView) view.findViewById(R.id.process_status_tv_timeFinished);
			mTimeFinished.setText(String.valueOf(processes.get(position).getTimeFinnished()));
			
			TextView mStatus = (TextView) view.findViewById(R.id.process_status_tv_status);
			mStatus.setText(processes.get(position).getStatus());


			return view;
		}

	}
	
	private class ProcessHaltTask extends AsyncTask<Void, Void, ProcessStatus>{
		private ProcessStatus process;
		
		public ProcessHaltTask(ProcessStatus process){
			this.process = process;
		}
		
		
		@Override
		protected ProcessStatus doInBackground(Void... params) {
			try {
				if(ComHandler.HaltProcessing(process.getID())){
					return process;
				}
			} catch (IOException e) {
				Toast.makeText(getActivity(), "Cant stop process: "+e.getMessage(), Toast.LENGTH_LONG).show();
			}
			return null;
		}
		
		protected void onPostExecute(ProcessStatus result) {
			if(result!=null){
				processes.remove(process);
				processListAdapter.notifyDataSetChanged();
			
			}
		}
		
	}

}
