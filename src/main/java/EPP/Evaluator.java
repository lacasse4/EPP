package main.java.EPP;

import java.util.ArrayList;

/**
 * Evaluator for EPP
 * @author Vincent Lacasse
 */
public class Evaluator extends ArrayList<Double> {

	private String firstName;
	private String lastName;
	private double evaluatorScore;

	public Evaluator(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public double getScore() {
		return evaluatorScore;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void compute(int minScale, int maxScale) {
		double total = 0.0;
		double offset = minScale == 1 ? 0.0 : 1.0;
		for (Double noteAspect : this) {
			total += noteAspect - offset;
		}
		evaluatorScore =  total * 100.0 / (maxScale * size());
	}
}
