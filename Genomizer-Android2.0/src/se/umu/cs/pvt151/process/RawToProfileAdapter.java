package se.umu.cs.pvt151.process;

import java.util.ArrayList;

import se.umu.cs.pvt151.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class RawToProfileAdapter extends ArrayAdapter<RawToProfileParameters> {
	private final static String BOWTIE_TITLE = "Bowtie parameters";
	
	private ArrayList<RawToProfileParameters> objects;

	private Context mContext;

	public RawToProfileAdapter(Context context, ArrayList<RawToProfileParameters> objects) {
		super(context, 0, objects);
		mContext = context;
		this.objects = objects;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		
		final RawToProfileParameters object = objects.get(position);
		
		final AlertDialog mBowtieParametersDialog = buildBowtieParamsDialog(position);
		
		if (view == null) {
			view = View.inflate(mContext,R.layout.process_layout_bowtie_list_element, null);
		}
		
		final Spinner mInputFileSpinner = (Spinner) view.findViewById(R.id.process_spinner_input_file);
		ArrayAdapter<String> mInputFileAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, object.getGeneFileNames());
		mInputFileAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		mInputFileSpinner.setAdapter(mInputFileAdapter);
		mInputFileSpinner.setSelection(object.getGeneFileNames().indexOf(object.getInputFileName()));
		
		mInputFileSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				object.setInputFileName((String) mInputFileSpinner.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			
		});
		
		final Spinner mGenomeReleaseSpinner = (Spinner) view.findViewById(R.id.process_spinner_genomeRelease);
		ArrayAdapter<String> mGenomeReleaseAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, object.getGrVersions());
		mGenomeReleaseAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		mGenomeReleaseSpinner.setAdapter(mGenomeReleaseAdapter);
		mGenomeReleaseSpinner.setSelection(object.getGrVersions().indexOf(object.getGrVersion()));
		
		mGenomeReleaseSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				object.setGrVersion((String) mGenomeReleaseSpinner.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			
		});
		

		
		Button mParametersButton = (Button) view.findViewById(R.id.process_btn_parameters);
		
		mParametersButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBowtieParametersDialog.show();
			}
			
		});
		

		ImageButton mDeleteButton = (ImageButton) view.findViewById(R.id.process_ib_delete);
		
		mDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				remove(getItem(position));
			}
			
		});
		
		EditText mOutputFileEditText = (EditText) view.findViewById(R.id.process_et_output_file);
		mOutputFileEditText.setText(object.getOutputFileName());
		
		mOutputFileEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				object.setOutputFileName(s.toString());
			}
			
		});
		
		final CheckBox keepSam = (CheckBox) view.findViewById(R.id.process_cb_keepsam);
		keepSam.setChecked(object.willKeepSam());
		
		keepSam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				object.setKeepSam(keepSam.isChecked());
			}
			
		});
		
		return view;
	}
	
	@Override
	public int getCount() {
		return objects.size();
	}
	
	@Override
	public RawToProfileParameters getItem(int position) {
		return objects.get(position);
	}
	
	private AlertDialog buildBowtieParamsDialog(final int position) {
		
		EditText mBowtieEditText = new EditText(mContext);
		
		mBowtieEditText.setText(objects.get(position).getBowtieParameters());
		
		mBowtieEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				objects.get(position).setBowtieParameters(s.toString());
			}
			
		});
		
		return new AlertDialog.Builder(mContext)
				.setTitle(BOWTIE_TITLE)
				.setView(mBowtieEditText)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				})
				.create();
	}
	
}
