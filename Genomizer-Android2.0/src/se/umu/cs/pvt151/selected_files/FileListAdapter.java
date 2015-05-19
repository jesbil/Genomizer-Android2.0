package se.umu.cs.pvt151.selected_files;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.model.GeneFile;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Adapter used for listviews. Its purpose is to store and view
 * GeneFile objects graphically.
 *
 */
public class FileListAdapter extends ArrayAdapter<GeneFile> {
	private ArrayList<GeneFile> files;
	private Context context;
	private HashMap<GeneFile, Boolean> selectedFilesMap;
	/**
	 * Constructor for the FileListAdapter
	 * @param fileNames with the values to
	 * be displayed in list view. 
	 */
	public FileListAdapter(ArrayList<GeneFile> files, Context context) {
		super(context, 0, files);
		this.files = files;
		this.context = context;
		selectedFilesMap = new HashMap<GeneFile, Boolean>();
		for(GeneFile file : files){
			selectedFilesMap.put(file, false);
		}
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
			view = inflater.inflate(R.layout.selectedfiles_layout_list_element, null);
		}

			TextView mFileName = (TextView) view.findViewById(R.id.selectedfiles_list_tv_fileName);
			CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.selectedfiles_list_cb_fileCheckBox);
			mFileName.setText(files.get(position).getName());
			mCheckBox.setChecked(false);
			
			mFileName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder build = new AlertDialog.Builder(context);		
					build.setTitle(files.get(position).getName());
					build.setMessage(files.get(position).toString());
					build.setNeutralButton("OK", null);
					build.show();
				}
			});
			
			
			mCheckBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					selectedFilesMap.put(files.get(position), !selectedFilesMap.get(files.get(position)));
					
				}
			});

			
			

		
		return view;
	}
}