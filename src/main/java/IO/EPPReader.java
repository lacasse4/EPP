package main.java.IO;

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
import main.java.EPP.EPP;
import main.java.EPP.Evaluated;
import main.java.EPP.Evaluator;
import main.java.EPP.Team;

import javax.swing.*;

/**
 * Utility class that reads a CSV file (Export des evaluations, sans multiligne)
 * and builds an EPP structure
 * @author Vincent Lacasse
 */
public class EPPReader {
	
	public static final int NB_FIELDS = 15;
	public static final int GROUPE = 0;
	public static final int NOM = 1; 
	public static final int PRENOM = 2;
	public static final int COURRIEL = 3;
	public static final int BAREME = 4;
	public static final int NOTE_ASPECT = 5;
	public static final int NOTE_CALCULE = 6;
	public static final int NOTE_MODIFIE = 7;
	public static final int NOTE_FINALE =8;
	public static final int MNG = 9;
	public static final int FACTEUR = 10;
	public static final int COMMENTAIRES = 11;
	public static final int NOM_EV = 12;
	public static final int PRENOM_EV = 13;
	public static final int COMMENTAIRES_GENERAUX = 14;

	/**
	 * reads a CSV file (Export des evaluations, sans multiligne) and create an EPP structure
	 * @param CSVFileName - the File pointing to the input CSV file
	 * @return an EPP instance, no computation performed
	 */	
	public static EPP read(String CSVFileName) {
		List<String[]> csvData = readCSV(CSVFileName);
		if (csvData == null) return null;
		return parseCSV(csvData);
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
					.withSkipLines(1)           // skip the first line which contains header info
					.withRowValidator(rv)
					.build();
			csvData = reader.readAll();
			reader.close();

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Le fichier " + CSVFileName + " n'a pas ??t?? trouv??.", "Erreur", JOptionPane.ERROR_MESSAGE);
			csvData = null;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erreur lors de la du fichier" + CSVFileName, "Erreur", JOptionPane.ERROR_MESSAGE);
			csvData = null;
		} catch (CsvException e) {
			JOptionPane.showMessageDialog(null, "Erreur ?? la ligne " + e.getLineNumber() + " du fichier " + CSVFileName + "\nLigne lu: " + e.getLine(), "Erreur", JOptionPane.ERROR_MESSAGE);
			csvData = null;
		}
		
		return csvData;
	}
	
	
	/**
	 * Populates an EPP structure from a csvData structure.
	 * Computation are not performed
	 * @param csvData - content of the CSV file as read by a CSVReader
	 * @return a populated EPP structure
	 */
	private static EPP parseCSV(List<String[]> csvData) {
		EPP epp = new EPP();
		Team team = null;
		Evaluated studentEvaluated = null;
		Evaluator studentEvaluator = null;
		boolean teamAdded;
		boolean evaluatedAdded;
		
		String[] last = new String[NB_FIELDS];
		Arrays.fill(last, "");
		
		for (String[] line : csvData) {
			teamAdded = false;
			evaluatedAdded = false;

			// detect team changes
			if (!last[GROUPE].equals(line[GROUPE])) {
				last[GROUPE] = line[GROUPE];
				team = new Team(line[GROUPE]);
				epp.add(team);
				teamAdded = true;
			}
			
			// detect student evaluated changes
			if (teamAdded || !last[NOM].equals(line[NOM]) || !last[PRENOM].equals(line[PRENOM])) {
				last[NOM] = line[NOM];
				last[PRENOM] = line[PRENOM];
				studentEvaluated = new Evaluated(line[NOM], line[PRENOM], line[COURRIEL]);
				team.add(studentEvaluated);
				evaluatedAdded = true;
			}
			
			// detect student evaluator changes
			if (teamAdded || evaluatedAdded || !last[NOM_EV].equals(line[NOM_EV]) || !last[PRENOM_EV].equals(line[PRENOM_EV])) {
				last[NOM_EV] = line[NOM_EV];
				last[PRENOM_EV] = line[PRENOM_EV];
				studentEvaluator = new Evaluator(line[NOM_EV], line[PRENOM_EV]);
				studentEvaluated.add(studentEvaluator);
			}

			// use the overridden note as the aspect score if the overridden note is not 0
			// otherwise use aspect score as is.
			if (Integer.parseInt(line[NOTE_MODIFIE]) > 0) {
				studentEvaluated.setModifiedNote(Double.parseDouble(line[NOTE_MODIFIE]));
			}
			else {
				studentEvaluator.add(Double.parseDouble(line[NOTE_ASPECT]));
			}
		}
		
		return epp;
	}

	/**
	 * Validator class
	 * Performs a basic check on CSV row elements as they are read
	 */
	private static class Validator implements RowValidator {

		// from https://www.baeldung.com/java-check-string-number
		final private Pattern pattern1 = Pattern.compile("-?\\d+(\\,\\d+)?");
		final private Pattern pattern2 = Pattern.compile("-?\\d+(\\.\\d+)?");

		public boolean isNumeric(String strNum) {
			if (strNum == null) {
				return false;
			}
			return pattern1.matcher(strNum).matches()
				|| pattern2.matcher(strNum).matches();
		}

		@Override
		public boolean isValid(String[] row) {
			return row.length == NB_FIELDS &&
			row[GROUPE].length() != 0 &&
			row[NOM].length() !=0 &&
			row[PRENOM].length() != 0 &&
			row[COURRIEL].length() != 0 &&
			row[BAREME].length() !=0 &&
			isNumeric(row[NOTE_ASPECT]) &&
			isNumeric(row[NOTE_CALCULE]) &&
			isNumeric(row[NOTE_MODIFIE]) &&
			isNumeric(row[NOTE_FINALE]) &&
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