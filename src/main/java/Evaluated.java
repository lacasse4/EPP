package main.java;

import java.util.ArrayList;
import java.util.List;

public class Evaluated extends Student {
	
	private List<Evaluator> studentsEvaluator = new ArrayList<Evaluator>();

	public Evaluated(String firstName, String lastName) {
		super(firstName, lastName);
	}
	
	public void addEvaluator(Evaluator e) {
		studentsEvaluator.add(e);
	}
	
	public double getNote() {
		double total = 0.0;
		for (Evaluator e : studentsEvaluator) {
			double note = e.getEvaluatorScore();
			total += note;
		}
		return total;
	}
}
