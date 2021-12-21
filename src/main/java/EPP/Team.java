package main.java.EPP;

import java.util.ArrayList;

/**
 * Team of students for EPP
 * @author Vincent Lacasse
 */
public class Team extends ArrayList<Evaluated> {

	public static final int NB_FIELDS_DISPLAYED = 6;
	public static final int D_GROUPE = 0;
	public static final int D_NOM = 1;
	public static final int D_PRENOM = 2;
	public static final int D_NOTE_EPP = 3;
	public static final int D_MNG = 4;
	public static final int D_FACTEUR = 5;

	public static final int NB_FIELDS_SAVED = 9;
	public static final int S_GROUPE = 0;
	public static final int S_NOM = 1;
	public static final int S_PRENOM = 2;
	public static final int S_COURRIEL = 3;
	public static final int S_NOTE_EPP = 4;
	public static final int S_MNG = 5;
	public static final int S_FACTEUR = 6;
	public static final int S_NOTE_EQUIPE = 7;
	public static final int S_NOTE_ETUDIANT = 8;

	public static final String[] D_HEADER =
			{"Groupe", "Nom", "Prenom", "Note_EPP", "MNG", "Facteur"};

	public static final String[] S_HEADER =
			{"Groupe", "Nom", "Prenom", "Courriel", "Note_EPP", "MNG", "Facteur", "Note_equipe", "Note_etudiant"};

	private String name;
	private double mean;

	public Team(String name) {
		this.name = name;
	}

	/**
	 * Computes means, notes and factors for all Students
 	 * @return the number of evaluated students in the team
	 */
	public int compute(int minScale, int maxScale) {
		// compute evaluated students notes
		double total = 0.0;
		for (Evaluated e : this) {
			e.compute(minScale, maxScale);
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
