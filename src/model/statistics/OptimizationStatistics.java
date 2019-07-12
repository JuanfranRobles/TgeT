package model.statistics;
/**
 * Stores optimization statistics from optimization 
 * algorithms execution. 
 * 
 * @author root
 *
 */
public class OptimizationStatistics {
	/**
	 * Final benefit for a Viral Marketing campaign. 
	 */
	private double benefit; 
	/**
	 * Final cost for a Viral Marketing campaign. 
	 */
	private double cost;
	/**
	 * Builders
	 */
	public OptimizationStatistics() {
		this.benefit = 0.0; 
		this.cost = 0.0;
	}
	/**
	 * Getters and setters. 
	 */
	public double getBenefit() {
		return benefit;
	}
	public void setBenefit(double benefit) {
		this.benefit = benefit;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	} 
}
