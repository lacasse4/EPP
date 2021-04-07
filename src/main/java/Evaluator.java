package main.java;

import java.util.ArrayList;

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

	public void compute() {
		double total = 0.0;
		for (Double noteAspect : this) {
			total += noteAspect;
		}
		evaluatorScore = total;
	}
}
