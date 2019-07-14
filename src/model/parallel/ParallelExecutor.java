package model.parallel;

import model.Market;

public class ParallelExecutor implements Runnable {
	/**
	 * Market model configuration file. 
	 */
	private String configurationFile; 
	/**
	 * Execution index for Monte-Carlo executions. 
	 */
	private int executionIndex;
	/** BUILDER **/
	public ParallelExecutor(String configuration, int executionIndex) {
		this.configurationFile = configuration; 
		this.executionIndex = executionIndex;
	}
	/** METHODS **/
	public setOPtimizationVariables(double [] optVars) {
		
	}
	/** CONCURRENT **/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Market m = new Market(configurationFile);
	}
	
}
