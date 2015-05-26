package se.umu.cs.pvt151.process;

import java.util.ArrayList;

public class RawToProfileParameters {
	private final static String WIG = ".wig";
	public final static String BOWTIE_PARAMS = "-a -m 1 --best -p 10 -v 2 -q -S";
	
	private final ArrayList<String> geneFileNames;
	private final ArrayList<String> grVersions;
	
	private String outputFileName;
	private String bowtieParameters;
	private String inputFileName;
	private String grVersion;
	
	public RawToProfileParameters(ArrayList<String> geneFileNames, 
			ArrayList<String> grVersions) {
		this.geneFileNames = geneFileNames;
		this.grVersions = grVersions;
		inputFileName = geneFileNames.get(0);
		grVersion = grVersions.get(0);
		outputFileName = removeExtension(inputFileName) + WIG;
		bowtieParameters = BOWTIE_PARAMS;
	}

	public ArrayList<String> getGeneFileNames() {
		return geneFileNames;
	}

	public ArrayList<String> getGrVersions() {
		return grVersions;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getBowtieParameters() {
		return bowtieParameters;
	}

	public void setBowtieParameters(String bowtieParameters) {
		this.bowtieParameters = bowtieParameters;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public String getGrVersion() {
		return grVersion;
	}

	public void setGrVersion(String grVersion) {
		this.grVersion = grVersion;
	}
	
	public static String removeExtension(String s) {

	    String separator = System.getProperty("file.separator");
	    String filename;

	    // Remove the path upto the filename.
	    int lastSeparatorIndex = s.lastIndexOf(separator);
	    if (lastSeparatorIndex == -1) {
	        filename = s;
	    } else {
	        filename = s.substring(lastSeparatorIndex + 1);
	    }

	    // Remove the extension.
	    int extensionIndex = filename.lastIndexOf(".");
	    if (extensionIndex == -1)
	        return filename;

	    return filename.substring(0, extensionIndex);
	}

}
