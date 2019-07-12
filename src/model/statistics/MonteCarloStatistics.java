package model.statistics;
/**
 * Stores Monte-Carlo statistics for market simulations. 
 * 
 * @author Juan Francisco Robles Fuentes
 *
 */
public class MonteCarloStatistics {
	/**
	 * Monte-Carlo statistics. 
	 */
	private Statistics [] monteCarloStatistics; 
	/**
	 * Builders.
	 */
	public MonteCarloStatistics(int numMCs) {
		this.monteCarloStatistics = new Statistics[numMCs];
	}
	/**
	 * Computes benefit for all Monte-Carlo executions. 
	 */
	public double computeCampaignBenefitByMC() {
		double benefits = 0.0;
		for(int mc=0; mc<monteCarloStatistics.length; mc++) {
			benefits += monteCarloStatistics[mc].getOptimizationStatistics().getBenefit();
		}
		return benefits/(double)monteCarloStatistics.length;
	}
	/**
	 * Computes benefit for all Monte-Carlo executions. 
	 */
	public double computeCampaignCostByMC() {
		double cost = 0.0;
		for(int mc=0; mc<monteCarloStatistics.length; mc++) {
			cost += monteCarloStatistics[mc].getOptimizationStatistics().getCost();
		}
		return cost/(double)monteCarloStatistics.length;
	}
}
