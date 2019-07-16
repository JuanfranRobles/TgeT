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
	/**
	 * Initializes an empty optimization statistics 
	 * object. Empty means that benefit and cost are equal to 
	 * zero. 
	 */
	public OptimizationStatistics() {
		this.benefit = 0.0; 
		this.cost = 0.0;
	}
	/**
	 * Initializes an optimization statistics object with 
	 * a custom benefit and a custom cost. 
	 * @param cpBenefit campaign benefit. 
	 * @param cpCost campaign cost. 
	 */
	public OptimizationStatistics(double cpBenefit, double cpCost) {
		this.benefit = cpBenefit; 
		this.cost = cpCost;
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
	/** METHODS **/
	/**
	 * Combines campaign benefit and cost for single-objective 
	 * optimization algorithms. 
	 * @return combination of benefit and cost. 
	 */
	public double combineStatistics() {
		return benefit - cost;
	}
}
