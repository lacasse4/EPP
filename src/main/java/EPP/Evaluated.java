package main.java.EPP;

import java.util.ArrayList;

/**
 * Student evaluated for EPP
 * @author Vincent Lacasse
 */
public class Evaluated extends ArrayList<Evaluator> {

	private String firstName;
	private String lastName;
	private String email;
	private double note;
	private double factor;
	private boolean modificatonProvided;

	public Evaluated(String lastName, String firstName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		modificatonProvided = false;
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

	public String getEMail() { return email; }

	public double getFactor() {
		return factor;
	}

	public void setModifiedNote(double modifiedNote) {
		this.note = modifiedNote;
		modificatonProvided = true;
	}

	public void computeFactor(double mean) {
		factor = note / mean;
	}

	public void compute(int minScale, int maxScale) {

		if (modificatonProvided)
			return;

		double total = 0.0;
		for (Evaluator e : this) {
			e.compute(minScale, maxScale);
			total += e.getScore();
		}
		note = total / size();
	}
}
