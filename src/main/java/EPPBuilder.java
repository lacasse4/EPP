package main.java;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.validators.RowValidator;

import javax.swing.*;

/**
 * Utility class that reads a CSV file (Export des evaluations, sans multiligne)
 * and builds an EPP structure
 * @author Vincent Lacasse
 */
public class EPPBuilder {
	
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
	 * reads and buids a EPP from a CSV file (Export des evaluations, sans multiligne)
	 * @param CSVFileName - the File pointing to the input CSV file
	 * @return an EPP instance
	 */	
	public static EPP build(String CSVFileName) {
		List<String[]> csvData = readCSV(CSVFileName);
		if (csvData == null) return null;
		return buildEPP(csvData);
	}

	
	/**
	 * reads a CSV File (Export des evaluations, sans multiligne)
	 * @param CSVFileName - the File pointing to the input CSV file
	 * @return CSV info in a list of String[]. Each item of the list is a CSV line.
	 */
	private static List<String[]> readCSV(String CSVFileName) {
		List<String[]> csvData = null;

		// create CSV parser using a semicolon as a seperator
		CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); 

		try {
			RowValidator rv = new Validator();
			CSVReader reader = new CSVReaderBuilder(
					new FileReader(CSVFileName))
					.withCSVParser(csvParser)   
					.withSkipLines(1)           // skip the first line, header info
					.withRowValidator(rv)
					.build();
			csvData = reader.readAll();
			reader.close();

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Le fichier " + CSVFileName + " n'a pas été trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
			csvData = null;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erreur lors de la du fichier" + CSVFileName, "Erreur", JOptionPane.ERROR_MESSAGE);
			csvData = null;
		} catch (CsvException e) {
			JOptionPane.showMessageDialog(null, "Erreur à la ligne " + e.getLineNumber() + " du fichier " + CSVFileName + "\nLigne lu: " + e.getLine(), "Erreur", JOptionPane.ERROR_MESSAGE);
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
		
		String[] last = new String[NB_FIELDS];
		Arrays.fill(last, "");
		
		for (String[] line : csvData) {
			
			// detect team changes
			if (!last[GROUPE].equals(line[GROUPE])) {
				last[GROUPE] = line[GROUPE];
				team = new Team(line[GROUPE]);
				epp.add(team);
			}
			
			// detect student evaluated changes
			if (!last[NOM].equals(line[NOM]) || !last[PRENOM].equals(line[PRENOM])) {
				last[NOM] = line[NOM];
				last[PRENOM] = line[PRENOM];
				studentEvaluated = new Evaluated(line[NOM], line[PRENOM]);
				team.add(studentEvaluated);
			}
			
			// detect student evaluator changes
			if (!last[NOM_EV].equals(line[NOM_EV]) || !last[PRENOM_EV].equals(line[PRENOM_EV])) {
				last[NOM_EV] = line[NOM_EV];
				last[PRENOM_EV] = line[PRENOM_EV];
				studentEvaluator = new Evaluator(line[NOM_EV], line[PRENOM_EV]);
				studentEvaluated.add(studentEvaluator);
			}

//			studentEvaluator.addScore(Double.parseDouble(line[NOTE_ASPECT]));
			// subtract 1 to note_aspect to have Module Atelier match EPP results
			studentEvaluator.add(Double.parseDouble(line[NOTE_ASPECT]) - 1.0);
		}
		
		return epp;
	}

//	private static class Validator implements LineValidator {
//
//		@Override
//		public boolean isValid(String line) {
//			if (line == null) return true;
//
//			int semicolonCount = 0;
//			for (int i = 0; i < line.length(); i++) {
//				if (line.charAt(i) == ';') {
//					semicolonCount++;
//				}
//			}
//			return semicolonCount >= 10;
//		}
//
//		@Override
//		public void validate(String line) throws CsvValidationException {
//			if (!isValid(line)) {
//				throw new CsvValidationException("Validator Exception");
//			}
//		}
//	}

	private static class Validator implements RowValidator {

		// from https://www.baeldung.com/java-check-string-number
		final private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

		public boolean isNumeric(String strNum) {
			if (strNum == null) {
				return false;
			}
			return pattern.matcher(strNum).matches();
		}

		@Override
		public boolean isValid(String[] row) {
			return row.length == NB_FIELDS &&
			row[GROUPE].length() != 0 &&
			row[NOM].length() !=0 &&
			row[PRENOM].length() != 0 &&
			row[BAREME].length() !=0 &&
			isNumeric(row[NOTE_ASPECT]) &&
			isNumeric(row[NOTE]) &&
			isNumeric(row[MNG]) &&
			isNumeric(row[FACTEUR]) &&
			row[NOM_EV].length() != 0 &&
			row[PRENOM_EV].length() != 0;
		}

		@Override
		public void validate(String[] row) throws CsvValidationException {
			if (!isValid(row)) {
				throw new CsvValidationException("Row Validator Exception");
			}
		}
	}
}