package se.umu.cs.pvt151.com;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import se.umu.cs.pvt151.model.GeneFile;
import se.umu.cs.pvt151.process.RawToProfileParameters;

/**
 * MsgFactory can be used to create a number of JSONObjects.
 * 
 * @author Rickard dv12rhm
 *
 */
public class MsgFactory {

	/**
	 * Creates a login JSONObject that contains username and password.
	 * 
	 * @param username
	 * @param password
	 * @return JSONObject
	 * @throws JSONException
	 */
	public static JSONObject createLogin(String username, String password) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("username", username);
		obj.put("password", password);		
		return obj;
	}

	
	/**
	 * Creates a conversion JSONObject that contains information about a file, 
	 * an arbitrary number of parameters, metadata, processtype and genomerelease.
	 * 
	 * @param param
	 * @param file
	 * @param metadata
	 * @param processType
	 * @param genomeRelease
	 * @return JSONObject
	 * @throws JSONException
	 */
	public static JSONObject createConversionRequest(ArrayList<String> param, GeneFile file, 
			String metadata, String genomeRelease) throws JSONException {
		JSONObject obj = new JSONObject();
		
		obj.put("expid", file.getExpId());
		Log.d("smurf", "Before parameters to json");
		obj.put("parameters", parametersToJSON(param));
		Log.d("smurf", "After parameters to json");
		obj.put("metadata", metadata);
		obj.put("genomeVersion", genomeRelease);
		obj.put("author", file.getAuthor());
		
		return obj;
	}
	
	public static JSONObject createRawToProfileRequest(ArrayList<RawToProfileParameters> parameters) throws JSONException {
		JSONObject object = new JSONObject();
		if (!parameters.isEmpty()) {

			object.put("expId", parameters.get(0).getExpId());
			
			JSONArray processCommands = new JSONArray();
			JSONObject processCommand = new JSONObject();
			processCommand.put("type","rawToProfile");
			
			JSONArray files = new JSONArray();
			for (int i = 0; i < parameters.size(); i++) {
				RawToProfileParameters parameter = parameters.get(i);
				JSONObject file = new JSONObject();
				file.put("infile", parameter.getInputFileName());
				file.put("outfile", parameter.getOutputFileName());
				file.put("params", parameter.getBowtieParameters());
				file.put("genomeVersion", parameter.getGrVersion());
				file.put("keepSam", parameter.willKeepSam());
				files.put(file);
			}
			
			processCommand.put("files", files);
			processCommands.put(processCommand);
			object.put("processCommands", processCommands);
		}
		
		return object;
	}
	
	
	/**
	 * Takes a ProcessingParameters object with information about an arbitrary number
	 * of parameters and returns it as an JSONArray.
	 * 
	 * @param param
	 * @return JSONArray
	 * @throws JSONException
	 */
	private static JSONArray parametersToJSON(ArrayList<String> param) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < param.size(); i++) {
			jsonArray.put(param.get(i));
		}
		return jsonArray;
	}
}
