package main.java;

import java.util.ArrayList;
import java.util.List;

/**
 * EPP class. Contains a list of Team 
 * @author Vincent Lacasse
 * Usage: add() teams one by one. Then get an iterator to get results.
 */
public class EPP extends ArrayList<Team> {

	private boolean[] grouping;
	private int nEvaluated = 0;

	public EPP() {
	}

	/**
	 * Computes means, notes and factors for all Students
	 */
	public void compute() {
		for (Team t : this) {
			nEvaluated += t.compute();
		}
		createGrouping();
	}

	/**
	 * Returns a header to be displayed prior to EPP data
 	 * @return the header
	 */
	public String[] getHeader() {
		return Team.HEADER;
	}

	/**
	 * Returns team groupings in an array of boolean
	 * @return an array of boolean
	 */
	public boolean[] getGrouping() {
		return grouping;
	}

	/**
	 * returns EPP data in a format suitable for a TableModel
 	 * @return EPP data in 2D array format
	 */
	public Object[][] getTableData() {
		List<Object[]> list = new ArrayList<Object[]>();
		for (Team t : this) {
			for (Evaluated e : t) {
				Object[] line = new Object[Team.NB_FIELDS];
				list.add(line);

				line[Team.GROUPE] = t.getName();
				line[Team.NOM] = e.getLastName();
				line[Team.PRENOM] = e.getFirstName();
				line[Team.NOTE_EPP] = e.getNote();
				line[Team.MNG] = t.getMean();
				line[Team.FACTEUR] = e.getFactor();
			}
		}
		return list.toArray(new Object[0][0]);
	}

	/**
	 * Creates Team groupings.
	 * Must be called after results have been computed
	 */
	private void createGrouping() {
		grouping = new boolean[nEvaluated];
		boolean state = true;
		int student = 0;

		for (Team team : this) {
			for (Evaluated e : team) {
				grouping[student++] = state;
			}
			state = !state;
		}
	}
}
