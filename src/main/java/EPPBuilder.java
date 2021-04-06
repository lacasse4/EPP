package main.java;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

/**
 * Utility class that reads a CSV file (Export des evaluation, sans multiligne)
 * and builds an EPP structure
 * @author Vincent Lacasse
 *
 */
public class EPPBuilder {
	
	// input fields
	public static final int NB_FIELDS = 12;
	public static final int GROUPE = 0;
	public static final int NOM = 1; 
	public static final int PRENOM = 2;
	public static final int BAREME = 3;
	public static final int NOTE_ASPECT = 4;
	public static final int NOTE = 5;
	public static final int MNG = 6;
	public static final int FACTEUR = 7;
	public static final int COMMENTAIRES = 8;
	public static final int NOM_EV = 9;
	public static final int PRENOM_EV = 10;
	public static final int COMMENTAIRES_GENERAUX = 11;

	
	/**
	 * reads and buids a EPP from a CSV file (Export des evaluation, sans multiligne)
	 * @param CSVFileName - the File pointing to the input CSV file
	 * @return an EPP instance
	 */	
	public static EPP build(String CSVFileName) {
		List<String[]> csvData = readCSV(CSVFileName);
		if (csvData == null) return null;
		return buildEPP(csvData);
	}

	
	/**
	 * reads a CSV File (Export des evaluation, sans multiligne)
	 * @param CSVFileName - the File pointing to the input CSV file
	 * @return CSV info in a list of String[]. Each item of the list is a CSV line.
	 */
	private static List<String[]> readCSV(String CSVFileName) {
		List<String[]> csvData = null;

		// create CSV parser using a semicolon as a seperator
		CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); 

		try {
			CSVReader reader = new CSVReaderBuilder(
					new FileReader(CSVFileName))
					.withCSVParser(csvParser)   
					.withSkipLines(1)           // skip the first line, header info
					.build();
			csvData = reader.readAll();
			reader.close();

		} catch (FileNotFoundException e) {
			System.err.println("File :" + CSVFileName + "not found");
			csvData = null;
		} catch (IOException e) {
			System.err.println("Error reading file " + CSVFileName +", Probably not a CSV file with ';' separators.");
			csvData = null;
		} catch (CsvException e) {
			System.err.println("CSV format error in file " + CSVFileName);
			csvData = null;
		}
		
		return csvData;
	}
	
	
	/**
	 * Populates an EPP structure from a csvData structure.
	 * Performs EPP calculations to obtain "Facteur".
	 * @param csvData - content of the CSV file as read by a CSVReader
	 * @return a populated EPP structure
	 */
	private static EPP buildEPP(List<String[]> csvData) {
		EPP epp = new EPP();
		Team team = null;
		Evaluated studentEvaluated = null;
		Evaluator studentEvaluator = null;
		
		String[] v = new String[NB_FIELDS];
		Arrays.fill(v, "");
		
		for (int line = 0; line < csvData.size(); line++) {
			
			// detect team changes
			if (!v[GROUPE].equals(csvData.get(line)[GROUPE])) {
				v[GROUPE] = csvData.get(line)[GROUPE];
				team = new Team(v[GROUPE]);
				epp.add(team);
			}
			
			// detect student evaluated changes
			if (!v[NOM].equals(csvData.get(line)[NOM]) || !v[PRENOM].equals(csvData.get(line)[PRENOM])) {
				v[NOM] = csvData.get(line)[NOM];
				v[PRENOM] = csvData.get(line)[PRENOM];
				studentEvaluated = new Evaluated(v[NOM], v[PRENOM]);
				team.add(studentEvaluated);
			}
			
			// detect student evaluator changes
			if (!v[NOM_EV].equals(csvData.get(line)[NOM_EV]) || !v[PRENOM_EV].equals(csvData.get(line)[PRENOM_EV])) {
				v[NOM_EV] = csvData.get(line)[NOM_EV];
				v[PRENOM_EV] = csvData.get(line)[PRENOM_EV];
				studentEvaluator = new Evaluator(v[NOM_EV], v[PRENOM_EV]);
				studentEvaluated.add(studentEvaluator);
			}
			
//			studentEvaluator.addScore(Double.parseDouble(inputCSV.get(line)[NOTE_ASPECT]));
			studentEvaluator.add(Double.parseDouble(csvData.get(line)[NOTE_ASPECT]) - 1.0);
		}
		
		return epp;
	}
}
