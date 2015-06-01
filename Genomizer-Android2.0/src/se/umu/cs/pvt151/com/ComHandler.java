package se.umu.cs.pvt151.com;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import se.umu.cs.pvt151.model.Annotation;
import se.umu.cs.pvt151.model.Experiment;
import se.umu.cs.pvt151.model.GeneFile;
import se.umu.cs.pvt151.model.GenomeRelease;
import se.umu.cs.pvt151.model.ProcessStatus;
import se.umu.cs.pvt151.process.RawToProfileParameters;

/**
 * This class takes care of the communication with the server.
 * 
 * @TODO Throw all exceptions back to the calling fragments for toasts
 * @author Rickard dv12rhm
 *
 */
public class ComHandler {

	private final static String LOGIN = "login";
	private final static String TOKEN = "token";
	private final static String ANNOTATION = "annotation";
	private final static String PROCESS = "process";
	private final static String GENOME_RELEASE = "genomeRelease";
	private final static String FILE = "file/";

	private final static String SEARCH_ANNOTATIONS = "search/?annotations=";
	private final static String RAW_TO_PROFILE = "process/processCommands";

	public final static int OK = 200;
	public final static int NO_CONTENT = 204;
	public final static int BAD_REQUEST = 400;
	public final static int UNAUTHORIZED = 401;
	public final static int FORBIDDEN = 403;
	public final static int NOT_ALLOWED = 405;
	public final static int TOO_MANY_REQUESTS = 429;
	public final static int SERVICE_UNAVAILIABLE = 503;

	public final static int NO_CONNECTION_WITH_SERVER = -1;
	public final static int NO_INTERNET_CONNECTION = -2;
	public final static int PARSING_ERROR = -3;

	/**
	 * Used to change the targeted server URL.
	 * 
	 * @param serverURL The URL of the server.
	 */
	public static void setServerURL(String serverURL) {				
		Communicator.initCommunicator(serverURL);
	}

	/**
	 * Returns the targeted URL
	 * 
	 * @return serverURL The URL of the server.
	 */
	public static String getServerURL() {		
		return Communicator.getServerURL();
	}


	/**
	 * Visualizes a toast with a message based on which
	 * responsecode is given as parameter.
	 * 
	 * @param requestType
	 * @param responseCode
	 */
	private static void responseDecode(String requestType, int responseCode) {
		switch(responseCode) {		
		case 204: 
			Genomizer.makeToast(requestType + ": No Content.");
			break;
		case 400:
			Genomizer.makeToast(requestType + ": Bad Request.");
			break;
		case 401:
			Genomizer.makeToast("Invalid username or password");
			break;
		case 403:
			Genomizer.makeToast(requestType + ": Forbidden - "
					+ "access denied.");
			break;
		case 404:
			Genomizer.makeToast(requestType + ": Not Found - "
					+ "resource was not found.");
			break;
		case 405:
			Genomizer.makeToast(requestType + ": Method Not Allowed - "
					+ "requested method is not supported for resource.");
			break;
		case 429:
			Genomizer.makeToast(requestType + ": Too Many Requests - "
					+ "please try again later.");
			break;
		case 503:
			Genomizer.makeToast(requestType + ": Service Unavailable - "
					+ "service is temporarily unavailable.");
			break;
		}

	}

	/**
	 * A method that sends a login request to the specified server.
	 * If the login succeeds (200 OK / ComHandler.OK), the 
	 * authorization token is saved in the Communicator.
	 * <br><br/>
	 * The method returns the code that the request returns. If no
	 * connection can be established or something else goes wrong, the correct
	 * error code is returned instead.
	 * 
	 * @param username The username
	 * @param password The password
	 * @return Either a HTTP response code, or an error code. Both are found
	 * 		   as fields in ComHandler.
	 * @throws IOException When no connection to the server can be
	 * 		   established
	 */
	public static int login(String username, String password) 
			throws IOException {
		int result;

		if (Genomizer.isOnline()) {

			try {
				JSONObject msg = MsgFactory.createLogin(username, password);
				GenomizerHttpPackage loginResponse = Communicator
						.sendHTTPRequest(msg, RESTMethod.POST, LOGIN);

				result = loginResponse.getCode();

				if (result == OK) {
					String jsonString = loginResponse.getBody();
					JSONObject jsonObject = new JSONObject(jsonString);
					Communicator.setToken(jsonObject.get(TOKEN).toString());
				}

			} catch (JSONException je) {
				result = PARSING_ERROR;
			}

		} else {
			result = NO_INTERNET_CONNECTION;
		}

		return result;
	}

	/**
	 * Sends a search request to the server. The search is based on annotations,
	 * added as a parameter.
	 * 
	 * @param annotations HashMap with the name of the field as key and the 
	 * 		  value of the field as value.
	 * @return JSONArray Contains an arbitrary amount of JSONObjects. 
	 *         Each object is information about a file.
	 * @throws IOException
	 */
	public static ArrayList<Experiment> search(HashMap<String, String> annotations) throws IOException {
		if(Genomizer.isOnline()) {
			try {					
				GenomizerHttpPackage searchResponse = Communicator.sendHTTPRequest
						(null, RESTMethod.GET, SEARCH_ANNOTATIONS + generatePubmedQuery(annotations));

				if (searchResponse.getCode() == OK) {
					JSONArray jsonPackage = new JSONArray(searchResponse.getBody());
					return MsgDeconstructor.deconSearch(jsonPackage);

				} else { 
					Log.d("EXPERIMENTFETCHERROR", searchResponse.getCode()+"");
					/*
					 * TODO Remove the responseDecode from all
					 * methods.
					 */
					responseDecode("Search response", searchResponse.getCode());
					return new ArrayList<Experiment>();
				} 

			} catch (JSONException e) {
				throw new IOException("Unable to understand server response. Has response messages been modified?");
			}		
		}
		throw new IOException("Internet access unavailable.");

	}


	/**
	 * Search with existing Pubmed Query String
	 * @param pubmedQuery
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Experiment> search(String pubmedQuery) throws IOException {
		if(Genomizer.isOnline()) {
			try {						
				GenomizerHttpPackage searchResponse = Communicator.sendHTTPRequest
						(null, RESTMethod.GET, SEARCH_ANNOTATIONS + pubmedQuery);

				if (searchResponse.getCode() >= 200 && searchResponse.getCode() < 300) {
					JSONArray jsonPackage = new JSONArray(searchResponse.getBody());
					return MsgDeconstructor.deconSearch(jsonPackage);

				} else {
					responseDecode("Search response", searchResponse.getCode());
					return new ArrayList<Experiment>();
				}
			} catch (JSONException e) {
				throw new IOException("Unable to understand server response. "
						+ "Has response messages been modified? " + e.getMessage());
			}

		}
		throw new IOException("Internet access unavailable.");
	}


	/**
	 * Returns the Annotations of the server.
	 * 
	 * @return An ArrayList of all Annotations.
	 * @throws IOException If communication with the server fails.
	 */
	public static ArrayList<Annotation> getServerAnnotations() throws IOException {
		if(Genomizer.isOnline()) {
			try {
				GenomizerHttpPackage annotationResponse = Communicator.sendHTTPRequest(null, RESTMethod.GET, ANNOTATION);

				if (annotationResponse.getCode() == OK) {
					String jsonString = annotationResponse.getBody();
					JSONArray jsonPackage = new JSONArray(jsonString);

					return MsgDeconstructor.deconAnnotations(jsonPackage);
				} else {
					responseDecode("Requesting database annotations", annotationResponse.getCode());				
					return new ArrayList<Annotation>();
				}

			} catch (JSONException e) {
				throw new IOException("Unable to understand server response. "
						+ "Has response messages been modified? " + e.getMessage());
			}
		}
		throw new IOException("Internet connection unavailable.");		
	}	


	/**
	 * Sends a convertion-task to the server were a specified file is to be converted
	 * from raw to profile data.
	 * 
	 * @param file
	 * @param parameters
	 * @return true if the task was recieved and validated by the server, false otherwise
	 * @throws IOException
	 */
	public static boolean rawToProfile(GeneFile file, ArrayList<String> parameters, 
			String meta, String release) throws IOException {
		if(Genomizer.isOnline()) {
			try {			
				JSONObject msg = MsgFactory.createConversionRequest(parameters, file, meta, release);
				GenomizerHttpPackage response = Communicator.sendHTTPRequest(msg, RESTMethod.PUT, RAW_TO_PROFILE);

				if(response.getCode() == OK) {
					return true;
				} else {
					responseDecode("Raw to profile", response.getCode());
					return false;
				}
			} catch (JSONException e) {
				//This is only an issue if the server is changed.
				throw new IOException("Unable to understand server response. "
						+ "Has response messages been modified? " + e.getMessage());
			}

		}
		throw new IOException("Internet connection unavailable.");

	}

	public static boolean rawToProfile(ArrayList<RawToProfileParameters> parameters) throws IOException {

		if (Genomizer.isOnline()) {
			try {
				JSONObject msg = MsgFactory.createRawToProfileRequest(parameters);
				GenomizerHttpPackage response = Communicator.sendHTTPRequest(msg, RESTMethod.PUT, RAW_TO_PROFILE);

				if (response.getCode() == OK) {
					return true;
				} else {
					responseDecode("RawToProfile", response.getCode());
					Log.d("RawToProfile", String.valueOf(response.getCode()));
					return false;
				}
			} catch (JSONException e) {
				throw new RuntimeException();
			}
		}

		throw new IOException("Genomizer is offline");
	}


	/**
	 * Gets and returns the genomereleases from the server as an ArrayList.
	 * 
	 * @return An ArrayList with GenomeRelease objects
	 * @throws IOException
	 */
	public static ArrayList<GenomeRelease> getGenomeReleases() throws IOException {
		if(Genomizer.isOnline()) {
			try {
				GenomizerHttpPackage genomeResponse = Communicator.sendHTTPRequest(null, RESTMethod.GET, GENOME_RELEASE);

				if (genomeResponse.getCode() == OK) {
					String jsonString = genomeResponse.getBody();
					JSONArray jsonPackage = new JSONArray(jsonString);

					return MsgDeconstructor.deconGenomeReleases(jsonPackage);
				} else {
					responseDecode("Requesting genome releases", genomeResponse.getCode());
					return new ArrayList<GenomeRelease>();
				}

			} catch (JSONException e) {
				throw new IOException("Unable to understand server response. "
						+ "Has response messages been modified? " + e.getMessage());
			}
		}
		throw new IOException("Internet connection unavailable.");
	}


	/**
	 * Gets and returns the states of all processes that are currently 
	 * running on the server.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<ProcessStatus> getProcesses() throws IOException {
		if(Genomizer.isOnline()) {
			try {
				GenomizerHttpPackage genomeResponse = Communicator.sendHTTPRequest(null, RESTMethod.GET, PROCESS);

				if (genomeResponse.getCode() == OK) {
					String jsonString = genomeResponse.getBody();
					JSONArray jsonPackage = new JSONArray(jsonString);

					return MsgDeconstructor.deconProcessPackage(jsonPackage);
				} else {
					responseDecode("Requesting status of processes", genomeResponse.getCode());				
					return new ArrayList<ProcessStatus>();
				}

			} catch (JSONException e) {
				throw new IOException("Unable to understand server response. "
						+ "Has response messages been modified? " + e.getMessage());
			}
		}
		throw new IOException("Internet connection unavailable.");

	}

	public static GeneFile getFile(String fileId) throws IOException {
		if(Genomizer.isOnline()) {
			try {
				GenomizerHttpPackage fileResponse = Communicator.sendHTTPRequest(null, RESTMethod.GET, FILE+fileId);
				if (fileResponse.getCode() == OK) {
					String jsonString = fileResponse.getBody();
					JSONArray jsonPackage = new JSONArray(jsonString);

					return MsgDeconstructor.deconFiles(jsonPackage).get(0);
				} else { 
					Genomizer.makeToast(fileResponse.getCode()+"");
					responseDecode("Requesting file", fileResponse.getCode());
					return null;
				}

			} catch (JSONException e) {
				throw new IOException("Unable to understand server response. "
						+ "Has response messages been modified? " + e.getMessage());
			}
		}
		throw new IOException("Internet connection unavailable.");
	}


	public static boolean HaltProcessing(String PID) throws IOException{
		if(Genomizer.isOnline()) {
			JSONObject haltObject = new JSONObject();
			try {
				haltObject.put("PID", PID);
				GenomizerHttpPackage haltResponse = Communicator.sendHTTPRequest(haltObject, RESTMethod.DELETE, PROCESS);
				if(haltResponse.getCode() == OK){
					return true;
				}else{
					return false;
				}
			} catch (JSONException e) {
				return false;
			}
		}
		throw new IOException("Internet connection unavailable.");
	}

	public static void logout() throws IOException {
		if(Genomizer.isOnline()) {
			try {
				Communicator.sendHTTPRequest(null, RESTMethod.DELETE, LOGIN);
			} catch (IOException e) {
				throw new IOException(e.getMessage());
			}

		}
	}

	/**
	 * Returns a pubmed query string ready to be put in a URL. It is encoded for URLs so it cannot be used elsewhere.
	 * 
	 * @param annotations HashMap with the annotation type as key and the value of the annotation as value.
	 * @return An encoded pubmed query string based on the parameter.
	 * @throws UnsupportedEncodingException If the device cannot encode the query.
	 */
	public static String generatePubmedQuery(HashMap<String, String> annotations) 
			throws UnsupportedEncodingException {
		String pubmedQuery = "";

		Set<String> keys = annotations.keySet();
		int i = 0;
		for (String key : keys) {
			String value = annotations.get(key);
			pubmedQuery += value + "[" + key + "]";
			i++;
			if (i != keys.size()) {
				pubmedQuery+=" AND ";
			}			
		}
		return URLEncoder.encode(pubmedQuery, "UTF-8");
	}





}
