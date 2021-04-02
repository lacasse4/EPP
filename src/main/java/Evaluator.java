package main.java;

import java.util.ArrayList;
import java.util.List;

public class Evaluator extends Student {

	private List<Double> scores = new ArrayList<Double>();
	
	public Evaluator(String firstName, String lastName) {
		super(firstName, lastName);
	}
	
	public void addScore(double score) {
		scores.add(score);
	}
	
	public double getEvaluatorScore() {
		double total = 0.0;
		for (Double noteAspect : scores) {
			total += noteAspect; 
		}
		return total;
	}
}
