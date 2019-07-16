package optimization;
/**
 * Stores and manages Viral Marketing 
 * optimization parameters. 
 * 
 * @author jfrobles
 *
 */
public class OptimizationParameters {
	/**
	 * The number of consumers to target. 
	 */
	private int numTargets; 
	/**
	 * The weight for consumers' degree importance
	 * when targeting. 
	 */
	private double degreeWeight; 
	/**
	 * The weight for consumers' neighborhood degree
	 * importance when targeting.  
	 */
	private double neighborhoudWeight; 
	/**
	 * The weight for consumers' clustering coefficient 
	 * importance when targeting. 
	 */
	private double clusteringCoefficientWeight;
	/** BUILDER **/
	/**
	 * Initializes the optimization parameters setting 
	 * the number of consumers to target and the weights 
	 * for degree, neighborhood degree and clustering coefficient 
	 * with the same weigh value. 
	 * @param numTargets
	 */
	public OptimizationParameters(int numTargets) {
		this.numTargets = numTargets;
		this.degreeWeight = 0.33;
		this.neighborhoudWeight = 0.33;
		this.neighborhoudWeight = 0.34; 
	}
	/** GETTERS & SETTERS **/
	/**
	 * Gets the number of consumers to target. 
	 * @return
	 */
	public int getNumTargets() {
		return numTargets;
	}
	/**
	 * Sets the number of consumers to target. 
	 * @param numTargets
	 */
	public void setNumTargets(int numTargets) {
		this.numTargets = numTargets;
	}
	/**
	 * Gets the weight for degree parameter. 
	 * @return degree weight. 
	 */
	public double getDegreeWeight() {
		return degreeWeight;
	}
	/**
	 * Sets the weight for degree parameter. 
	 * @param degreeWeight weight for degree [0,1].
	 */
	public void setDegreeWeight(double degreeWeight) {
		this.degreeWeight = degreeWeight;
	}
	/**
	 * Gets the weight for neighborhood parameter. 
	 * @return neighborhood weight. 
	 */
	public double getNeighborhoudWeight() {
		return neighborhoudWeight;
	}
	/**
	 * Sets the weight for neighborhood parameter. 
	 * @param neighborhoudWeight weight for neighborhood degree [0,1].
	 */
	public void setNeighborhoudWeight(double neighborhoudWeight) {
		this.neighborhoudWeight = neighborhoudWeight;
	}
	/**
	 * Gets the weight for clustering coefficient parameter. 
	 * @return clustering coefficient weight. 
	 */
	public double getClusteringCoefficientWeight() {
		return clusteringCoefficientWeight;
	}
	/**
	 * Sets the weight for neighborhood parameter. 
	 * @param clusteringCoefficientWeight weight for clustering coefficient degree [0, 1].
	 */
	public void setClusteringCoefficientWeight(double clusteringCoefficientWeight) {
		this.clusteringCoefficientWeight = clusteringCoefficientWeight;
	}
}
