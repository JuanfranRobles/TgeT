package model.parallel;

import model.Market;
import model.statistics.MonteCarloStatistics;
import optimization.OptimizationParameters;

public class ParallelExecutor implements Runnable {
	/**
	 * Market model configuration file. 
	 */
	private String configurationFile; 
	/**
	 * Execution index for Monte-Carlo executions. 
	 */
	private int executionIndex;
	/**
	 * Optimization parameter values. 
	 */
	private OptimizationParameters optParams; 
	/**
	 * Variable to store Monte-Carlo statistics. 
	 */
	private MonteCarloStatistics stats; 
	
	/** BUILDER **/
	public ParallelExecutor(String configuration, int executionIndex, MonteCarloStatistics stats) {
		this.configurationFile = configuration; 
		this.executionIndex = executionIndex;
		this.stats = stats;
	}
	/** METHODS **/
	public void setOPtimizationVariables(double [] weights) {
		this.optParams = new OptimizationParameters((int)weights[weights.length-1]);
		this.optParams.setDegreeWeight(weights[0]);
		this.optParams.setNeighborhoudWeight(weights[1]);
		this.optParams.setClusteringCoefficientWeight(weights[2]);
	}
	/** CONCURRENT **/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Market m = new Market(configurationFile);
		m.setOptimizationParameters(this.optParams);
		m.run(executionIndex);
		this.stats.setStatistics(m.getStatistics(), executionIndex);
	}
	
}
