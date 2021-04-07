package main.java;

import java.util.ArrayList;

/**
 * Student evaluated for EPP
 * @author Vincent Lacasse
 */
public class Evaluated extends ArrayList<Evaluator> {

	private String firstName;
	private String lastName;
	private double note;
	private double factor;

	public Evaluated(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	 * Returns the evaluated student EPP note (sum of all evaluations)
 	 * @return
	 */
	public double getNote() {
		return note;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public double getFactor() {
		return factor;
	}

	public void computeFactor(double mean) {
		factor = note / mean;
	}

	public void compute() {
		double total = 0.0;
		for (Evaluator e : this) {
			e.compute();
			total += e.getScore();
		}
		note = total;
	}
}
