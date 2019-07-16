package model.statistics;

import model.Customer;
import model.Market;

/**
* @author Juan Francisco Robles Fuentes.
**/

/**
* Statistics class.
* 
* This class stores the statistics obtained after a market simulation 
* and calculates the final value of some metrics.  
* 
*/
public class Statistics {
	/**
	 * Total execution time.
	 */
	private long executiontime = 0;
	/**
	 * Gini coefficient measures dominance of products on the market. The 
	 * higher this indicator, the more the market is dominated by one or a few products. 
	 * A value of zero means that all products have an equal market share.
	 */
	private double [] gini = new double [0];
	/**
	 * Turbulence is the average change of product choice during the simulation by all agents. 
	 * Thus turbulence 1.0 means that all agents change their choice each period, and 0.0 means 
	 * that no agent changes its choice after its initial choice.
	 */
	private double [] turbulence = new double [0];
	/**
	 * Product selection by step. 
	 */
	private int [][] productSelectionByStep; 
	/**
	 * The product selection by agent for each step. 
	 */
	private int [][] consumerChoiceByStep = new int [0][0];
	/**
	 * Stores the probability for being selected for each market 
	 * product at each simulation step. 
	 */
	private double [][] finalbuyprob = new double [0][0];
	/**
	 * Stores the use of decision heuristics by simulation step.
	 */
	private double [][] heuristicsuse = new double [0][0];
	/**
	 * Stores the awareness of the products in the market by simulation
	 * step. 
	 */
	private double [][] awarenessRatio = new double [0][0];
	/**
	 * Stores the number of customers for each simulation step. 
	 */
	private double [] meancustomers = new double [0];
	/**
	 * Stores multi-objective optimization statistics. 
	 */
	private OptimizationStatistics optimizationStatistics; 
	
	/** BUILDERS **/
	public Statistics(Market m) {
		int marketSteps = (int)Math.floor(m.getSteps() / m.getStationality());
		int marketProducts = m.getProducts().length;
		int marketConsumers = m.getCustomers().length;
		// Setting indicators (Gini coefficient and turbulence). 
		gini = new double[marketSteps];
		turbulence = new double[marketSteps];
		// Setting structures to store statistics. 
		consumerChoiceByStep = new int[marketConsumers][marketSteps];
		productSelectionByStep = new int[marketProducts][marketSteps];
		finalbuyprob = new double[marketProducts][marketSteps];
		heuristicsuse = new double[marketProducts][marketSteps];
		awarenessRatio = new double[marketProducts][marketSteps];
		meancustomers = new double[marketSteps];
		// Initializaing optimization statistics. 
		if(m.toOptimize()) {
			optimizationStatistics = new OptimizationStatistics();
		}
	}
	/** Getters and setters **/
	/**
	 * Sets execution time. 
	 * @param t
	 */
	public void setExecutionTime(long t){
		this.executiontime = t;
	}
	/**
	 * Sets Gini coefficient. 
	 * @param g
	 */
	public void setGini(double g, int step){
		this.gini[step] = g;
	}
	/**
	 * Sets turbulence.
	 * @param t
	 */
	public void setTurbulence(double t, int step){
		this.turbulence[step] = t;
	}
	/**
	 * Sets the number of selections by product in a step.  
	 * @param cs
	 */
	public void setProductSelection(Customer [] cs, int step){
		int [] buys = new int [cs[0].getPreferences().length];			
		for(Customer c: cs){
			buys[c.getPurchase()] += 1;
		}
		for(int product=0; product<buys.length; product++) {
			productSelectionByStep[product][step] = buys[product];
		}
	}
	
	public void setFinalBuyProb(Customer [] cs, int step){
		double [] buyprobs = new double [cs[0].getPreferences().length];
		double totalsales = (double) cs.length;
		
		for(Customer c: cs){
			buyprobs[c.getPurchase()] += 1.0;
		}
		for(int p = 0; p < buyprobs.length; p++){
			this.finalbuyprob[p][step] = buyprobs[p] / totalsales;;
		}
	}
	
	public void setHeuristicsUse(int [] hs, int step){
		int total = 0;
		for(Integer v: hs){
			total += v;
		}
		for(int p = 0; p < hs.length; p++) {
			this.heuristicsuse[p][step] = (double) hs[p] / (double) total;
		}
	}
	
	public void setAwarenessRatio(Customer [] cs, int step){
		
		for(Customer c: cs){
			for(int prod = 0; prod < c.getAwareness().length; prod++){
				if(c.getProductAwareness(prod))
					this.awarenessRatio[prod][step] += 1.0;
			}
		}
		for(int val = 0; val < this.awarenessRatio.length; val++){
			this.awarenessRatio[val][step] /= (double) cs.length;
		}
	}
	public void setMeanCustomers(double cs, int step){
		this.meancustomers[step] = cs;
	}
	public void setConsumerChoiceByStep(Customer [] cs, int step) {
		for(Customer c: cs){
			consumerChoiceByStep[c.getIdentifier()][step] = c.getPurchase();
		}
	}
	public double getExecutionTime(){
		return this.executiontime;
	}
	
	public double getGini(int step){
		return this.gini[step];
	}
	
	public double getTurbulence(int step){
		return this.turbulence[step];
	}
	
	public int [][] getConsumerChoiceByStep(){
		return this.consumerChoiceByStep;
	}

	public double [][] getFinalBuyProbs(){
		return this.finalbuyprob;
	}
	
	public double [][] getHeuristicsUse(){
		return this.heuristicsuse;
	}
	public double [][] getAwarenessRatio(){
		return this.awarenessRatio;
	}
	public double [] getMeanCustomers(){
		return this.meancustomers;
	}
	public OptimizationStatistics getOptimizationStatistics() {
		return optimizationStatistics;
	}
	public void setOptimizationStatistics(OptimizationStatistics optimizationStatistics) {
		this.optimizationStatistics = optimizationStatistics;
	}
	/** AGGREGATION METHODS **/
	public double getAggregatedGiniCoefficient() {
		double aggregatedGini = 0.0;
		for(int step=0; step<gini.length; step++) {
			aggregatedGini += gini[step];
		}
		return aggregatedGini; 
	}
	public double getAggregatedTurbulence() {
		double aggregatedTurbulence = 0.0;
		for(int step=0; step<turbulence.length; step++) {
			aggregatedTurbulence += turbulence[step];
		}
		return aggregatedTurbulence; 
	}
	
	public double getAggregatedAvgCustomers() {
		double aggregatedAvgCustomers = 0.0;
		for(int step=0; step<meancustomers.length; step++) {
			aggregatedAvgCustomers += meancustomers[step];
		}
		return aggregatedAvgCustomers; 
	}
	public int [] lastProductSelection() {
		int [] lastProductsSelection = new int[productSelectionByStep.length];
		for(int p=0; p<productSelectionByStep.length; p++) {
			lastProductsSelection[p] = productSelectionByStep[p][productSelectionByStep.length-1];
		}
		return lastProductsSelection;
	}
	public double [] lastBuyProbability() {
		double [] lastProductsSelection = new double[finalbuyprob.length];
		for(int p=0; p<finalbuyprob.length; p++) {
			lastProductsSelection[p] = finalbuyprob[p][finalbuyprob.length-1];
		}
		return lastProductsSelection;
	}
	public double [] aggregateHeuristicUse() {
		double [] aggregatedHeuristicUse = new double [heuristicsuse.length];
		for(int h=0; h<heuristicsuse.length; h++) {
			for(int step=0; step<heuristicsuse[h].length; step++) {
				aggregatedHeuristicUse[h] += heuristicsuse[h][step];
			}
			aggregatedHeuristicUse[h] /= heuristicsuse[h].length;
		}
		return aggregatedHeuristicUse;
	}
	public double [] lastAwarenessRatio() {
		double [] lastAwarenessRatio = new double[awarenessRatio.length];
		for(int p=0; p<finalbuyprob.length; p++) {
			lastAwarenessRatio[p] = awarenessRatio[p][awarenessRatio.length-1];
		}
		return lastAwarenessRatio;
	}
}
