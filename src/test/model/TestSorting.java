package test.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Util;

public class TestSorting {
	
	public static void main(String [] args) {
		Map<double[], Integer> toSort = new HashMap<>();
		double [][] vals = new double [][] {{3.0, 4.0}, {5.0, 2.0}, {1.0, 4.0},{3.0, 4.0}, {8.0,7.0},{1.8, 4.3}, {3.2, 3.8}};
		
		ArrayList<ArrayList<Integer>> front = Util.fastNonDominatedSorting(vals);
		
		for(int i=0; i<front.size(); i++) {
			System.out.println("Front: " + (i+1));
			System.out.println("Solutions: " + front.get(i).toString());
		}
	}
}
