package model.statistics;

import model.Customer;

/**
 * Statistics functions. 
 * @author root
 *
 */
public class StatisticsFunctions {
	/**
	 * Calculates Gini coefficient. 
	 * @param customers
	 * @return
	 */
	public static double calculateGini(Customer [] customers){
		// Calculate the products being consumed. 
		int [] products = new int [customers[0].getPreferences().length];
		int total = 0; // If in the future we need to deal with customers that 
					   // not consume we need to count the consumed products one by one.
		for(Customer c: customers){
			products[c.getPurchase()] += 1;
			total += 1; 
		}
		double gval = 0.0;
		for(int po = 0; po < products.length; po++){
			for(int pt = po+1; pt < products.length; pt++){
				gval += (Math.abs(products[po] - products[pt])) / ((double)products.length * (double)total);
			}
		}
		return gval;
	}
	/**
	 * Calculates Turbulence metric. 
	 * @param pchoices
	 * @param nchoices
	 * @return
	 */
	public static double calculateTurbulence(int [] pchoices, int [] nchoices){
		int diffchoices = 0;
		for(int c = 0; c < pchoices.length; c++){
			if((pchoices[c] - nchoices[c]) != 0)
				diffchoices += 1;
		}
		return (double)diffchoices / (double)pchoices.length;
	}
}
