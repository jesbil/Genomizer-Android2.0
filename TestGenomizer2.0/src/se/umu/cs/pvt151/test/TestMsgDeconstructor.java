/**
 * 
 */
package se.umu.cs.pvt151.test;



import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import se.umu.cs.pvt151.com.MsgDeconstructor;
import se.umu.cs.pvt151.model.Annotation;
import se.umu.cs.pvt151.model.Experiment;
import se.umu.cs.pvt151.model.GeneFile;
import se.umu.cs.pvt151.model.GenomeRelease;
import se.umu.cs.pvt151.model.ProcessStatus;

import junit.framework.TestCase;

/**
 * @author ens11pnn
 *
 */
public class TestMsgDeconstructor extends TestCase {

	private JSONArray packageArray;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		packageArray = new JSONArray();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		packageArray = null;
		super.tearDown();
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconAnnotations(org.json.JSONArray)}.
	 */
	public void testDeconOneAnnotation() {
		JSONObject json = new JSONObject();
		try {
			JSONArray testValues = new JSONArray();
			testValues.put("test1");
			testValues.put("test2");
			json.put("name", "testname");
			json.put("values", testValues);
			json.put("forced", "1");
			packageArray.put(json);
			ArrayList<Annotation> result = MsgDeconstructor.deconAnnotations(packageArray);
			assertNotNull(result.get(0));
		} catch (JSONException e) {
			
		}
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconFiles(org.json.JSONArray)}.
	 */
	public void testDeconOneFile() {
		try {
			JSONObject json = new JSONObject();
			json.put("id", "id");
			json.put("expId", "expId");
			json.put("fileSize", "fileSize");
			json.put("type", "type");
			json.put("filename", "filename");
			json.put("author", "author");
			json.put("uploader", "uploader");
			json.put("date", "date");
			json.put("url", "url");
			json.put("path", "path");
			json.put("grVersion", "grVersion");
			packageArray.put(json);
			ArrayList<GeneFile> result = MsgDeconstructor.deconFiles(packageArray);
			assertNotNull(result.get(0));
			
		} catch (JSONException je) {
			fail("API changed");
		}
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconSearch(org.json.JSONArray)}.
	 */
	public void testDeconSearch() {
		try {
			JSONObject experiment = new JSONObject();
			

			//fake name
			experiment.put("name", "expID");

			//fake fileArray
			JSONArray fileArray = new JSONArray();
			JSONObject file = new JSONObject();
			file.put("id", 10);
			file.put("path", "path");
			file.put("url", "url");
			file.put("type", "type");
			file.put("filename", "filename");
			file.put("date", "date");
			file.put("author", "author");
			file.put("uploader", "uploader");
			file.put("expId", "expId");
			file.put("grVersion", "grVersion");
			fileArray.put(file);
			experiment.put("files", fileArray);

			//fake AnnotationsArray
			JSONArray annotationArray = new JSONArray();
			JSONObject annotation = new JSONObject();
			annotation.put("name", "name");
			annotation.put("value", "value");
			annotationArray.put(annotation);
			experiment.put("annotations", annotationArray);
			
			
			packageArray.put(experiment);
			
			
		} catch (JSONException e){
			fail("Failed because of JSONException in construction");
		}		
		
		try{
			ArrayList<Experiment> experiments = MsgDeconstructor.deconSearch(packageArray);
			assertEquals("expID", experiments.get(0).getName());
			assertFalse(experiments.get(0).getFiles().isEmpty());
			assertEquals("10", experiments.get(0).getFiles().get(0).getFileId());
			assertFalse(experiments.get(0).getAnnotations().isEmpty());
			assertEquals("name", experiments.get(0).getAnnotations().get(0).getName());
		} catch (JSONException e) {
			fail("Failed because of JSONException in deconstruction");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconGenomeReleases(org.json.JSONArray)}.
	 */
	public void testDeconGenomeReleases() {
		try {
			JSONObject genome = new JSONObject();
			genome.put("genomeVersion", "hy17");
			genome.put("species", "fly");
			genome.put("folderPath", "pathToVersion");
			JSONArray fileArray = new JSONArray();
			fileArray.put("filename1");
			fileArray.put("filename2");
			fileArray.put("filename3");
			genome.put("files",fileArray);
			packageArray.put(genome);
			
			ArrayList<GenomeRelease> genomeReleases = MsgDeconstructor.deconGenomeReleases(packageArray);
			
			assertFalse(genomeReleases.isEmpty());
			assertEquals("hy17", genomeReleases.get(0).getGenomeVersion());
			assertEquals("fly", genomeReleases.get(0).getSpecie());
			assertEquals("pathToVersion", genomeReleases.get(0).getPath());
		} catch (JSONException e) {
			fail("Failed because of JSONException");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconProcessPackage(org.json.JSONArray)}.
	 */
	public void testDeconProcessPackage() {
		try {
			JSONObject process = new JSONObject();
			process.put("experimentName", "Exp1");
			process.put("status", "Finished");
			process.put("author", "yuri");
			process.put("timeAdded", 1400245668744L);
			process.put("timeStarted", 1400245668756L);
			process.put("timeFinished", 1400245669756L);
			JSONArray fileArray = new JSONArray();
			fileArray.put("file1");
			fileArray.put("file2");
			process.put("outputFiles",fileArray);
			packageArray.put(process);
			
			ArrayList<ProcessStatus> processes = MsgDeconstructor.deconProcessPackage(packageArray);
			
			assertFalse(processes.isEmpty());
			assertEquals("Exp1", processes.get(0).getExperimentName());
			assertTrue(processes.get(0).getOutputFiles().length>0);
			assertEquals("file1", processes.get(0).getOutputFiles()[0]);
			assertEquals(1400245668744L, processes.get(0).getTimeAdded());
			
		} catch (JSONException e) {
			fail("Failed because of JSONException");
			e.printStackTrace();
		}
	}

}
