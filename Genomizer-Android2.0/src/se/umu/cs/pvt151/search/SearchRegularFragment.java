package se.umu.cs.pvt151.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.com.ComHandler;
import se.umu.cs.pvt151.model.Annotation;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SearchRegularFragment extends Fragment {

	private static final String NO_SEARCH_VALUES = "No annotations choosen for search";

	private FragmentActivity mActivity;
	private ArrayList<String> annotationNamesList;
	private ArrayList<Annotation> annotations;
	private ArrayList<SearchViewHolder> mViewHolderList;
	private ListView mAnnotationsList;

	/**
	 * Static searchViewHolder class for keeping items in the searchList in
	 * memory when scrolled out of the screen.
	 * 
	 * @author Anders Lundberg, dv12alg
	 * @author Erik Åberg, c11eag
	 */
	static class SearchViewHolder {
		protected EditText editText;
		protected TextView textView;
		protected Spinner spinner;
		protected int position;
		protected int selectedPosition;
		protected String freetext;
		protected boolean isDropDown;
		protected CheckBox checkBox;
		protected boolean isChecked;
		protected View convertView;
	}

	@Override
	public void onResume() {
		super.onResume();
		mViewHolderList = new ArrayList<SearchViewHolder>();
		hideKeyboard();
		new AnnotationsTask().execute();

	} 
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (FragmentActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.search_layout_regular,
				container, false);

		mAnnotationsList = (ListView) rootView
				.findViewById(R.id.search_regular_lv_annotationList);

		Button mSearchButton = (Button) rootView
				.findViewById(R.id.search_regular_btn_search);

		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				HashMap<String, String> searchValues = generateSearchMap();

				if (searchValues.isEmpty()) {
					Toast.makeText(getActivity(), NO_SEARCH_VALUES,
							Toast.LENGTH_LONG).show();
				} else {
					Fragment fragment = new SearchResultFragment();
					Bundle bundle = new Bundle();

					bundle.putSerializable(SearchResultFragment.SEARCH_VALUES,
							generateSearchMap());

					bundle.putStringArrayList(
							SearchResultFragment.ANNOTATION_NAMES,
							annotationNamesList);

					fragment.setArguments(bundle);

					getActivity().getSupportFragmentManager()
							.beginTransaction().addToBackStack(null)
							.replace(R.id.frame_container, fragment).commit();

				}
			}
		});

		return rootView;
	}

	protected HashMap<String, String> generateSearchMap() {
		HashMap<String, String> search = new HashMap<String, String>();
		String key;
		String value;

		for (SearchViewHolder vh : mViewHolderList) {
			if (vh.isChecked) {
				key = vh.textView.getText().toString();
				if (vh.isDropDown) {
					value = vh.spinner.getSelectedItem().toString();
				} else {
					value = vh.freetext;
				}
				search.put(key, value);
			}
		}

		return search;
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	/**
	 * Initializes a new SearchListAdapter for the listView in the fragment
	 * containing the generated annotations from the database and setup a footer
	 * for the search button to generate a search string.
	 */
	private void setupListView() {
		annotationNamesList = new ArrayList<String>();

		for (int i = 0; i < annotations.size(); i++) {
			annotationNamesList.add(annotations.get(i).getName());
		}

		ArrayAdapter<String> adapter = new SearchListAdapter(
				annotationNamesList);
		adapter.setNotifyOnChange(true);
		mAnnotationsList.setAdapter(adapter);
	}

	/**
	 * AsyncTask for connecting to the server and generate annotations for the
	 * search view in the Genomizer android application.
	 * 
	 * @author Anders Lundberg, dv12alg
	 * @author Erik Åberg, c11ean
	 * 
	 */
	private class AnnotationsTask extends
			AsyncTask<Void, Void, ArrayList<Annotation>> {

		private static final String DOWNLOAD_ANNOTATIONS = "Downloading server annotations";
		private static final String LOADING = "Loading";

		private IOException ioe;
		private ProgressDialog mLoadScreen;

		public AnnotationsTask() {
			ioe = null;
			showLoadScreen();
		}

		/**
		 * Connects to the server, collects annotation data from the database
		 * and sets the retrieved values into corresponding lists.
		 */
		@Override
		protected ArrayList<Annotation> doInBackground(Void... params) {
			try {
				return ComHandler.getServerAnnotations();
			} catch (IOException e) {
				ioe = e;
				return new ArrayList<Annotation>();
			}
		}

		/**
		 * After the collection of data is complete setup the adapter with the
		 * information about annotations found in the database contacted.
		 */
		@Override
		protected void onPostExecute(ArrayList<Annotation> result) {
			mLoadScreen.dismiss();
			annotations = result;
			setupListView();
			if (ioe != null) {
				ioe.printStackTrace();
			}

		}

		/**
		 * Presents the user with a loading screen while data transfer is
		 * occuring in the application.
		 * 
		 */
		private void showLoadScreen() {
			mLoadScreen = new ProgressDialog(getActivity());
			mLoadScreen.setTitle(LOADING);
			mLoadScreen.setMessage(DOWNLOAD_ANNOTATIONS);
			mLoadScreen.show();
		}

	}

	/**
	 * Implementation of ArrayAdapter made for the genomizer app. Will use the
	 * searchViewHolder as memory of the list scrolled out of view.
	 * 
	 * @author Anders Lundberg, dv12alg
	 * @author Erik Åberg, c11ean
	 */
	private class SearchListAdapter extends ArrayAdapter<String> {

		/**
		 * Creates a new SearchListAdapter, with the annotationlist passed into
		 * the adapter.
		 * 
		 * @param annotationNames
		 *            List with annotations to be displayed in the adapter.
		 */
		public SearchListAdapter(ArrayList<String> annotationNames) {
			super(mActivity, 0, annotationNames);
		}

		/**
		 * Creates new views for each annotation category that is set in the
		 * adapter to the listView. Creates either a freetext field or a
		 * dropdown menu, depending on which is specified for the annotation
		 * category. Stores the views information in a viewholder for a memory
		 * management when the view is out of screen.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SearchViewHolder viewHolder;
			ArrayAdapter<String> spinAdapter;
			Spinner spinner;

			if (convertView == null) {
				final ArrayList<String> mSpinnerList = annotations.get(
						position).getValue();

				if (mSpinnerList.size() == 1
						&& mSpinnerList.get(0).compareTo("freetext") == 0) {
					convertView = getActivity().getLayoutInflater().inflate(
							R.layout.search_layout_regular_field, null);
					viewHolder = new SearchViewHolder();

					makeFreeTextHolder(position, convertView, viewHolder);
				} else {
					convertView = getActivity()
							.getLayoutInflater()
							.inflate(
									R.layout.search_layout_regular_dropdown_field,
									null);

					spinAdapter = new ArrayAdapter<String>(
							convertView.getContext(),
							android.R.layout.simple_spinner_item, mSpinnerList);
					spinAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner = (Spinner) convertView
							.findViewById(R.id.searchRegular_spinner_search);

					spinner.setAdapter(spinAdapter);
					viewHolder = new SearchViewHolder();
					makeSpinnerHolder(position, convertView, viewHolder,
							spinner);
				}
				mViewHolderList.add(viewHolder);
				viewHolder.convertView = convertView;

			} else {
				if (position >= mViewHolderList.size())
					return getView(position, null, parent);
				viewHolder = mViewHolderList.get(position);
				convertView = viewHolder.convertView;

				if (viewHolder.isDropDown) {
					viewHolder.textView.setText(annotationNamesList
							.get(viewHolder.position));
					viewHolder.spinner
							.setSelection(viewHolder.selectedPosition);
					viewHolder.checkBox.setChecked(viewHolder.isChecked);
				} else {
					viewHolder.editText.setText(viewHolder.freetext);
					viewHolder.editText.clearFocus();
					viewHolder.textView.setText(annotationNamesList
							.get(viewHolder.position));
					viewHolder.checkBox.setChecked(viewHolder.isChecked);
				}
			}
			return convertView;
		}

		/**
		 * Gets the corresponding spinner from the layout and setup the
		 * viewholder for the spinner. Also sets onCheckedChangedListener for
		 * the checkBox connected to the layout.
		 * 
		 * @param position
		 *            for the actual view
		 * @param convertView
		 *            the view of the selection-field
		 * @param viewHolder
		 *            the viewholder to create and setup for the view
		 * @param spinner
		 *            the spinner that is a part of the selection-field
		 */
		private void makeSpinnerHolder(int position, View convertView,
				SearchViewHolder viewHolder, Spinner spinner) {
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.searchRegular_tv_search);
			viewHolder.textView.setText(annotations.get(position).getName());
			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.searchRegular_cb_checkbox);
			viewHolder.checkBox.setTag(viewHolder);
			viewHolder.checkBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						/**
						 * Marks the Selection_field as marked or unmarked.
						 * depending on the users clicked choice.
						 */
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							SearchViewHolder vh = (SearchViewHolder) buttonView
									.getTag();
							vh.isChecked = isChecked;

						}
					});
			viewHolder.spinner = (Spinner) convertView
					.findViewById(R.id.searchRegular_spinner_search);
			viewHolder.isDropDown = true;
			viewHolder.position = position;

			spinner.setTag(viewHolder);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					SearchViewHolder vh = (SearchViewHolder) parent.getTag();
					vh.selectedPosition = position;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}

			});
		}

		/**
		 * Gets the corresponding textView from the layout and setup the
		 * viewholder for the textView. Also sets onCheckedChangedListener for
		 * the checkBox connected to the layout.
		 * 
		 * @param position
		 *            the position for the actual view
		 * @param convertView
		 *            the view of the layout inflated
		 * @param viewHolder
		 *            the viewholder to create and setup for the view
		 */
		private void makeFreeTextHolder(int position, View convertView,
				SearchViewHolder viewHolder) {
			viewHolder.isDropDown = false;
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.searchRegular_tv_fieldSearch);
			viewHolder.textView.setText(annotations.get(position).getName());
			viewHolder.position = position;

			viewHolder.editText = (EditText) convertView
					.findViewById(R.id.searchRegular_et_expid);
			viewHolder.editText.setHint(annotations.get(position).getName());

			viewHolder.editText.addTextChangedListener(new TheTextWatcher(
					viewHolder));

			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.searchResult_cb_checkbox);
			viewHolder.checkBox.setTag(viewHolder);
			viewHolder.checkBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							SearchViewHolder vh = (SearchViewHolder) buttonView
									.getTag();
							vh.isChecked = isChecked;

						}
					});
			viewHolder.editText.clearFocus();
		}
	}

	/**
	 * Implementation of TextWatcher for the Genomizer android application,
	 * collects user input from freetextfields.
	 * 
	 * @author Anders Lundberg, dv12alg
	 * @author Erik Åberg, c11ean
	 */
	private class TheTextWatcher implements TextWatcher {

		private SearchViewHolder viewHolder;

		/**
		 * Creates a new TheTextWatcher object.
		 * 
		 * @param viewHolder
		 *            the viewHolder that contains the EditText that is
		 *            connected with the textWatcher.
		 */
		public TheTextWatcher(SearchViewHolder viewHolder) {
			this.viewHolder = viewHolder;
		}

		/**
		 * After text is changed, updates the freeText field in the viewHolder
		 * connected to the specific field.
		 */
		@Override
		public void afterTextChanged(Editable s) {
			viewHolder.freetext = s.toString();
		}

		/**
		 * Unimplemented
		 */
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		/**
		 * Unimplemented
		 */
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

}
