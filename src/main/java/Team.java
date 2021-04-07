package main.java;

import java.util.ArrayList;

/**
 * team of students for EPP
 * @author Vincent Lacasse
 *
 */
public class Team extends ArrayList<Evaluated> {

	public static final int NB_FIELDS = 6;
	public static final int GROUPE = 0;
	public static final int NOM = 1;
	public static final int PRENOM = 2;
	public static final int NOTE_EPP = 3;
	public static final int MNG = 4;
	public static final int FACTEUR = 5;
	public static final int NOTE_EQUIPE = 6;
	public static final int NOTE_ETUDIANT = 7;
	public static final String[] HEADER =
//			{"Groupe", "Nom", "Prenom", "Note_EPP", "MNG", "Facteur"};
			{"Groupe", "Nom", "Prenom", "Note_EPP", "MNG", "Facteur", "Note_equipe", "Note_etudiant"};

	private String name;
	private double mean;

	public Team(String name) {
		this.name = name;
	}

	/**
	 * Computes means, notes and factors for all Students
 	 * @return the number of evaluated students in the team
	 */
	public int compute() {
		// compute evaluated students notes
		double total = 0.0;
		for (Evaluated e : this) {
			e.compute();
			double note = e.getNote();
			total += note;
		}

		// compute and team mean
		mean = total / size();

		// compute evaluated students factors
		for (Evaluated e : this) {
			e.computeFactor(mean);
		}
		return size();
	}

	public String getName() {
		return name;
	}

	public double getMean() {
		return mean;
	}
}
