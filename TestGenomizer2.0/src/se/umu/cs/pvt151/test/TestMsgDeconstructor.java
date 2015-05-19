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

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconAnnotations(org.json.JSONArray)}.
	 */
	public void testDeconAnnotations() {
		JSONObject annotation = new JSONObject();
		JSONArray annotationArray = new JSONArray();
		String name = "name";
		ArrayList<String> values = new ArrayList<String>();
		values.add("v1");
		values.add("v2");
		JSONArray valuesJSON = new JSONArray();
		valuesJSON.put("v1");
		valuesJSON.put("v2");
		boolean forced = true;
		try {
			annotation.put("name", name);
			annotation.put("values", valuesJSON);
			annotation.put("forced", forced);
			annotationArray.put(annotation);

			Annotation correct = new Annotation();
			correct.setName(name);
			correct.setValues(values);
			correct.setForced(forced);
			
			Annotation result = MsgDeconstructor.deconAnnotations(annotationArray).get(0);
			assertEquals(correct.toString(), result.toString());
			

		} catch (JSONException e) {
			fail("Failed because of JSONException in construction");
		}
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconFiles(org.json.JSONArray)}.
	 */
	public void testDeconFiles() {
		JSONObject file = new JSONObject();
		JSONArray fileArray = new JSONArray();

		try {
			file.put("id", "10");
			file.put("path", "path");
			file.put("url", "url");
			file.put("type", "type");
			file.put("filename", "filename");
			file.put("date", "date");
			file.put("author", "author");
			file.put("uploader", "uploader");
			file.put("expId", "expId");
			file.put("grVersion", "grVersion");
			file.put("fileSize", "fileSize");
			fileArray.put(file);
			
			GeneFile correct = new GeneFile();
			correct.setFileId("10");
			correct.setPath("path");
			correct.setUrl("url");
			correct.setType("type");
			correct.setName("filename");
			correct.setDate("date");
			correct.setAuthor("author");
			correct.setUploadedBy("uploader");
			correct.setExpId("expId");
			correct.setGrVersion("grVersion");
			correct.setFileSize("fileSize");
			
			GeneFile result = MsgDeconstructor.deconFiles(fileArray).get(0);
			assertEquals(correct, result);
		} catch (JSONException e) {
			fail("Failed because of JSONException in construction");
		}
		
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgDeconstructor#deconExperiments(org.json.JSONArray)}.
	 */
	public void testDeconExperiments() {
		JSONArray packageArray = new JSONArray();
		try {
			JSONObject experiment = new JSONObject();


			//fake name
			experiment.put("name", "expID");

			//fake fileArray
			JSONArray fileArray = new JSONArray();
			JSONObject file = new JSONObject();
			file.put("id", 10);
			file.put("path", "path");
			file.put("fileSize", "fileSize");
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
			annotation.put("forced", true);
			annotationArray.put(annotation);
			experiment.put("annotations", annotationArray);


			packageArray.put(experiment);


		} catch (JSONException e){
			fail("Failed because of JSONException in construction");
		}		

		try{
			ArrayList<Experiment> experiments = MsgDeconstructor.deconExperiments(packageArray);
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
			JSONArray packageArray = new JSONArray();
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
			JSONArray packageArray = new JSONArray();
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
