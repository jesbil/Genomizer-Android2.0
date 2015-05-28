package se.umu.cs.pvt151.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import se.umu.cs.pvt151.R;
import se.umu.cs.pvt151.com.ComHandler;
import se.umu.cs.pvt151.model.Annotation;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchPubmedFragment extends Fragment {
	private String pubmedQuery;
	private EditText searchField;
	private ArrayList<String> annotationNamesList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		new AnnotationsTask().execute();
		
		View rootView = inflater.inflate(R.layout.search_layout_pubmed,container, false);
		searchField = (EditText) rootView.findViewById(R.id.search_pubmed_input_field);



		Button mSearchButton = (Button) rootView.findViewById(
				R.id.search_pubmed_btn_search);

		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pubmedQuery = searchField.getText().toString().replace("\n", "");
				if(pubmedQuery==null || pubmedQuery.length()==0){
					Toast.makeText(getActivity(), "Search field is empty", Toast.LENGTH_SHORT).show();
				}else if(!matchingPerentheses(pubmedQuery)){
					Toast.makeText(getActivity(), "Search has unmatched perentheses", Toast.LENGTH_SHORT).show();
				}
				else{
					pubmedQuery = substituteLowerCaseConnectives(pubmedQuery);
					try {
						pubmedQuery = URLEncoder.encode(pubmedQuery,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						Toast.makeText(getActivity(), "Malformed pubmed query", Toast.LENGTH_SHORT).show();
					}
					
					Fragment fragment = new SearchResultFragment();
					Bundle bundle = new Bundle();
					bundle.putString(SearchResultFragment.PUBMED_QUERY, pubmedQuery);
					bundle.putStringArrayList(
							SearchResultFragment.ANNOTATION_NAMES,
							annotationNamesList);
					fragment.setArguments(bundle);
					getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();

				}

			}
		});

		return rootView;
	}
	
	private String substituteLowerCaseConnectives(String str) {		
		str = str.replaceAll("or", "OR");
		str = str.replaceAll("and", "AND");
		str = str.replaceAll("not", "NOT");
		return str;
	}
	
	private boolean matchingPerentheses(String str) {
		int left = 0, right = 0;
		
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == '(') left++;
			else if(str.charAt(i) ==')') right++;
		}
		return left == right;
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
			AsyncTask<Void, Void, ArrayList<String>> {

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
		protected ArrayList<String> doInBackground(Void... params) {
			ArrayList<Annotation> annotations;
			try {
				annotations = ComHandler.getServerAnnotations();
			} catch (IOException e) {
				ioe = e;
				annotations = new ArrayList<Annotation>();
			}
			ArrayList<String> annotationNames = new ArrayList<String>();
			for(Annotation annotation : annotations){
				annotationNames.add(annotation.getName());
			}
			return annotationNames;
		}

		/**
		 * After the collection of data is complete setup the adapter with the
		 * information about annotations found in the database contacted.
		 */
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			mLoadScreen.dismiss();
			annotationNamesList = result;
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

}
