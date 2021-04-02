package main.java;

import java.util.ArrayList;
import java.util.List;

/**
 * EPP class. Contains a list of Team 
 * @author Vincent Lacasse
 * Call addTeam() to add teams one by one. Then, call evaluate() to get EPP results.
 */
public class EPP {

	private List<Team> teams;
	private List<Object[]> results;
	private boolean[] grouping;
	private boolean resultsComputed;


	public EPP() { 
		teams = new ArrayList<Team>();
		results = new ArrayList<Object[]>();
		resultsComputed = false;
	}

	/**
	 * Adds a team to the EPP structure
	 * @param team - Team to be added to this EPP
	 */
	public void addTeam(Team team) { 
		if (!resultsComputed) {
			teams.add(team); 
		}
		else {
			throw new UnsupportedOperationException("Can not add a Team after EPP results are computed.");
		}
	}

	
	/*
	 * Getters
	 */
	
	public String[] getHeader() {
		return Team.HEADER;
	}

	public List<Object[]> getResults() {
		enforceComputing();
		return results;
	}

	public boolean[] getGrouping() {
		enforceComputing();
		return grouping;
	}
	

	/**
	 * Enforces that results and groupings are computed
	 */
	private void enforceComputing() {
		if (!resultsComputed) {
			computeResults();
			resultsComputed = true;
		}
	}

	
	/**
	 * Computes EPP results (including Facteur) and grouping
	 */
	private void computeResults() {
		for (Team t : teams) {
			results.addAll(t.getTeamResults());
		}	
		createGrouping();
		
	}


	/**
	 * Creates Team groupings.
	 * Must be called after the results have been computed
	 */
	private void createGrouping() {
		grouping = new boolean[results.size()];
		boolean state = false;
		String currentGroup = "";
		int compteur = 0;

		for (Object[] x : results) {
			String group = (String)x[Team.GROUPE];
			if (!currentGroup.equals(group)) {
				state = !state;
				currentGroup = group;
			}
			grouping[compteur++] = state;
		}
	}
}
