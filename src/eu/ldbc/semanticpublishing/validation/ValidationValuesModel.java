package eu.ldbc.semanticpublishing.validation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import eu.ldbc.semanticpublishing.substitutionparameters.SubstitutionQueryParameters;

public class ValidationValuesModel {
	private static final String RESULTS_HEADER = "[Results]";
	private static final String RESULT_ACCEPT_TYPE = "[ResultsAcceptType]";
	private static final String EXPECTED_RESULTS_SIZE_HEADER = "[ExpectedResultsSize]";
	
	private SubstitutionQueryParameters substitutionParameters;
	private String queryName;
	private String resultAcceptType;
	private long expectedResultsSize;
	private List<String> validationResultsList;
	
	public ValidationValuesModel(String queryName) {
		this.queryName = queryName;
		expectedResultsSize = -1;
		substitutionParameters = new SubstitutionQueryParameters(queryName);
		validationResultsList = new ArrayList<String>();
	}
	
	public void initFromFile(String fullPath, boolean suppressErrorMessages) throws IOException, InterruptedException {
		substitutionParameters.initFromFile(fullPath, suppressErrorMessages, true);
		
		BufferedReader br = null;
		try {
			boolean canAddResultAcceptType = false;
			boolean canAddExpectedResultsCount = false;
			boolean canAddResultValues = false;
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fullPath), "UTF-8"));
			
			String line = br.readLine();
			while (line != null) {	
				if (canAddResultAcceptType) {			
					resultAcceptType = line;				
					canAddResultAcceptType = false;
				}
				
				if (canAddExpectedResultsCount) {
					expectedResultsSize = Integer.parseInt(line);
					canAddExpectedResultsCount = false;
				}
				
				if (canAddResultValues) {
					validationResultsList.add(line);
				}
				
				if (line.contains(EXPECTED_RESULTS_SIZE_HEADER)) {
					canAddExpectedResultsCount = true;
				}
				
				if (line.contains(RESULTS_HEADER)) {
					canAddResultValues = true;
				}
				
				if (line.contains(RESULT_ACCEPT_TYPE)) {				
					canAddResultAcceptType = true;
				}
				
				line = br.readLine();
			}
		} catch (IOException ioe) {
			if (!suppressErrorMessages) {
				System.out.println("\tFailed to initialize validation results from : " + fullPath);
			}
		} finally {
			try { br.close();} catch(Exception e) {}
		}
	}
	
	public String getQueryName() {
		return this.queryName;
	}
	
	public long getExpectedResultsSize() {
		return expectedResultsSize;
	}
	
	public String getResultAcceptType() {
		return resultAcceptType;
	}
	
	public String[] getSubstitutionParameters() {
		return substitutionParameters.get(0);
	}
	
	public List<String> getValidationResultsList() {
		return validationResultsList;
	}
	
	public String getValidationResultsAsString() {
		StringBuilder sb = new StringBuilder();
		for (String s : validationResultsList) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}
}
