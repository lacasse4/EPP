package main.java.EPP;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EPP class. Contains a list of Team 
 * @author Vincent Lacasse
 * Usage: add() teams one by one. Then call compute() prior to get any results.
 */
public class EPP extends AbstractTableModel implements Iterable<Team> {

	private boolean[] grouping;
	private List<Team> teamList;
	private int nEvaluated = 0;

	public EPP() {
		teamList = new ArrayList<Team>();
	}

	/**
	 * Adds a team to the EPP structure
	 * @param t - team to be added
	 * @return true if team was added
	 */
	public boolean add(Team t) {
		return teamList.add(t);
	}

	/**
	 * Computes means, notes and factors for all Students
	 */
	public void compute(int minScale, int maxScale) {
		nEvaluated = 0;
		for (Team t : teamList) {
			nEvaluated += t.compute(minScale, maxScale);
		}
		createGrouping();
		fireTableDataChanged();
	}

	/**
	 * Returns team groupings in an array of boolean
	 * @return an array of boolean
	 */
	public boolean[] getGrouping() {
		return grouping;
	}

	/**
	 * Creates Team groupings.
	 * Must be called after results have been computed
	 */
	private void createGrouping() {
		grouping = new boolean[nEvaluated];
		boolean state = true;
		int student = 0;

		for (Team team : teamList) {
			for (Evaluated e : team) {
				grouping[student++] = state;
			}
			state = !state;
		}
	}

	/**
	 * Returns the number of row when EPP is displayed as a TableModel
	 * @return number of students evaluated
	 */
	@Override
	public int getRowCount() {
		return nEvaluated;
	}

	/**
	 * Returns the number of column when EPP is displayed as a TableModel
	 * @return
	 */
	@Override
	public int getColumnCount() {
		return Team.NB_FIELDS_DISPLAYED;
	}

	/**
	 * Gets single cel value when EPP is displayed as a TableModel (excluding header)
	 * @param rowIndex - row of the table cel to be displayed
	 * @param columnIndex - column of the table cel to be displayed
	 * @return cel to be displayed
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int teamIndex = 0;
		for (Team t : teamList) {
			teamIndex += t.size();
			if (teamIndex > rowIndex ) {
				int studentIndex = t.size() + rowIndex - teamIndex;

				switch(columnIndex) {
					case Team.D_GROUPE:   return t.getName();
					case Team.D_NOM:      return t.get(studentIndex).getLastName();
					case Team.D_PRENOM:   return t.get(studentIndex).getFirstName();
					case Team.D_NOTE_EPP: return t.get(studentIndex).getNote();
					case Team.D_MNG:      return t.getMean();
					case Team.D_FACTEUR:  return t.get(studentIndex).getFactor();
					default:            throw new IndexOutOfBoundsException();
				}
			}
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * Creates an Iterator to scan through Teams
	 * @return a Team Iterator
	 */
	@Override
	public Iterator<Team> iterator() {
		return teamList.iterator();
	}

	/**
	 * Returns a full header of EPP data
	 * @return the header
	 */
	public String[] getHeader() {
		return Team.S_HEADER;
	}

	/**
	 * Returns a column header item.
	 * @param column - index of the header item
	 * @return header item
	 */
	@Override
	public String getColumnName(int column) {
		return Team.D_HEADER[column];
	}
}
