package se.umu.cs.pvt151.com;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;


/**
 * This class handles the core communication with a server.
 * When a request is to be sent the 'setUpConnection' method
 * should be called first. This sets up a connection with the server.
 * To send the package, call the 
 * 
 * @author Rickard dv12rhm
 *
 */
public class Communicator {

	//The number of tries on getting response code
	private static final int RESPONSE_TRIES = 3;

	private static HttpURLConnection connection;
	private static String urlString;
	private static String token = "";
	private static Communicator staticSelfReference = null;


	/**
	 * Creates a new Communicator object.
	 * 
	 * @param urlString - the adress to which packages will be sent
	 */
	private Communicator(String urlString) {		
		Communicator.urlString = urlString;
	}


	/**
	 * Returns a new Communicator object if there are no Communicator
	 * object created at an earlier stage, otherwise it returns the
	 * old object.
	 * 
	 * @param serverURL
	 * @return A Communicator object
	 */
	public static Communicator initCommunicator(String serverURL) {
		if(staticSelfReference == null) {
			staticSelfReference = new Communicator(serverURL);
			return staticSelfReference;
		} else {
			Communicator.urlString = serverURL;
			return staticSelfReference;
		}
	}


	/**
	 * Sets the token which will be sent to the server with each package.
	 * The token works as an identifier for the server.
	 * 
	 * @param token
	 */
	public static void setToken(String token) {
		Communicator.token = token;
	}

	public static String getServerURL() {
		return urlString;
	}


	/**
	 * Sets up a connection and sends a package to the server.
	 * The response code and body will be returned in the form of a
	 * GenomizerHttpPackage object if the request succeeds. 
	 * If the request fails a IOException will be thrown.
	 * 
	 * @param jsonPackage 
	 * @param requestType -HTTP methods {GET,POST,PUT,DELETE}
	 * @param urlPostfix
	 * @return GenomizerHttpPackage - contains response code & body
	 * @throws IOException
	 */
	public static GenomizerHttpPackage sendHTTPRequest(JSONObject jsonPackage, RESTMethod requestType, String urlPostfix) throws IOException {
		setupConnection(requestType, urlPostfix);
		return sendRequest(jsonPackage, urlPostfix);
	}
	
	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}


	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub
				
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sets up a connection to a server.
	 * 
	 * @param requestType
	 * @param urlPostfix
	 * @throws IOException
	 */
	private static void setupConnection(RESTMethod requestType, String urlPostfix) throws IOException  {
		URL url = new URL(urlString + urlPostfix);
		if (url.getProtocol().equals("https")) {
		    trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			connection = https;
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}
		
		if (!requestType.equals(RESTMethod.GET)) {
			connection.setDoOutput(true);
		}
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod(requestType.toString());
		connection.setRequestProperty("Content-Type", "application/json");

		if (!urlPostfix.equals("login")) {			
			connection.setRequestProperty("Authorization", token);
		}
		
		if(urlPostfix.equals("login") && requestType.equals(RESTMethod.DELETE)){
			connection.setRequestProperty("Authorization", token);
			connection.setDoOutput(false);
		}

		connection.setChunkedStreamingMode(100);
		connection.setConnectTimeout(4000);
		connection.setReadTimeout(15000);
		connection.setRequestProperty("connection", "close");
	}	


	/**
	 * Sends a request to the server. A JSON package which has to be
	 * includes as an parameter will be sent as body.
	 * 
	 * @param jsonPackage
	 * @return GenomizerHttpPackage - The response code and body
	 * @throws IOException
	 */
	private static GenomizerHttpPackage sendRequest(JSONObject jsonPackage, String urlPostfix) throws IOException {
		writePackage(jsonPackage);	
		int responseCode = recieveResponse(urlPostfix);
		GenomizerHttpPackage hp =  validateCode(responseCode);
		return hp;

	}


	/**
	 * Writes a package to the server. If no working
	 * connection is established, an IOException will be thrown.
	 * 
	 * @param jsonPackage
	 * @throws IOException
	 */
	private static void writePackage(JSONObject jsonPackage) throws IOException {
		if (connection.getDoOutput()) {
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());						
			byte[] pack = jsonPackage.toString().getBytes("UTF-8");							
			out.write(pack);
			out.flush();				
		}
	}


	/**
	 * Gets and returns a response code from the server.
	 * If no working connection is established or if no 
	 * response code is sent from the server, a IOException
	 * will be thrown.
	 * 
	 * @return Response code
	 * @throws IOException
	 */
	private static int recieveResponse(String urlPostfix) throws IOException {

		for(int i = 0; i < RESPONSE_TRIES; i++) {

			int response = connection.getResponseCode();
			
			if(response != -1) {
				return response;
			}

		}

		throw new IOException("Server is not respondning.");

	}


	/**
	 * Validates a response code and creates a GenomizerHttpPackage
	 * included response body (if there are any), and returns it.
	 * If the response code is outside of the range 200-299 the return package will
	 * have an empty body.
	 * 
	 * @param responseCode
	 * @param in
	 * @return HttpPackage response from server
	 * @throws IOException
	 */
	private static GenomizerHttpPackage validateCode(int responseCode) throws IOException {
		if (responseCode >= 200 && responseCode < 300) {
			BufferedReader inStream = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			StringBuffer response = new StringBuffer();
			String inputLine;

			//Read response body
			while ((inputLine = inStream.readLine()) != null) {
				response.append(inputLine);
			}
			return new GenomizerHttpPackage(responseCode, response.toString());
		} else {
			return new GenomizerHttpPackage(responseCode, "");
		}
	}
}
