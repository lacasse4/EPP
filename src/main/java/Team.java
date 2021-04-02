package main.java;

import java.util.ArrayList;
import java.util.List;

/**
 * team of students for EPP
 * @author Vincent Lacasse
 *
 */
public class Team {
	
	public static final int NB_FIELDS = 6;
	public static final int GROUPE = 0;
	public static final int NOM = 1;
	public static final int PRENOM = 2;
	public static final int NOTE_EPP = 3;
	public static final int MNG = 4;
	public static final int FACTEUR = 5;
	public static final int NOTE_EQUIPE = 6;
	public static final int NOTE_ETUDIANT = 7;
    public static final String[] HEADER = {"Groupe", "Nom", "Prenom", "Note_EPP", "MNG", "Facteur" };
	
	private String name;
	private List<Evaluated> studentsEvaluated = new ArrayList<Evaluated>();
	
	public Team(String name) { this.name = name; }
	public void addEvaluated(Evaluated e) { studentsEvaluated.add(e); }
	public String getName() { return name; }
	public List<Evaluated> getEvaluated() { return studentsEvaluated; }
	
	public List<Object[]> getTeamResults() {
		
		List<Object[]> results = new ArrayList<Object[]>();
		List<Double> notes = new ArrayList<Double>();
		double mean;
		
		double total = 0.0;
		for (Evaluated e : studentsEvaluated) {
			double note = e.getNote();
			total += note;
			notes.add(note);
		}
		mean = total / studentsEvaluated.size();
		
		for (int i = 0; i < studentsEvaluated.size(); i++) {
			Object[] line = new Object[NB_FIELDS];
			line[GROUPE] = name;
			line[NOM] = studentsEvaluated.get(i).getLastName();
			line[PRENOM] = studentsEvaluated.get(i).getFirstName();
			line[NOTE_EPP] = round(notes.get(i));
			line[MNG] = round(mean);
			line[FACTEUR] = round(notes.get(i)/mean);
			results.add(line);
		}
		
		return results;
	}
	
	/**
	 * round double number to 2 decimal places
	 * @param number - number to be rounded
	 * @return rounded number
	 */
	private double round(double number) {
		return Math.round(number * 100.0) / 100.0;
	}
}
