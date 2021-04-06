package main.java;

import java.util.ArrayList;

public class Evaluated extends ArrayList<Evaluator> {

	private String firstName;
	private String lastName;

	public Evaluated(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public double getNote() {
		double total = 0.0;
		for (Evaluator e : this) {
			double note = e.getEvaluatorScore();
			total += note;
		}
		return total;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
