package simulator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import configuration.Reader;
import model.parallel.ParallelExecutor;
import model.statistics.MonteCarloStatistics;

public class Simulator {
	private MonteCarloStatistics mcStats; 
	private double [] optParameters; 
	private String configFile; 
	
	public Simulator(String configFile) {
		this.configFile = configFile;
	}
	public void setOptParameters(double [] optParams) {
		this.optParameters = optParams; 
	}
	public void simulateModel() {
		Reader r = new Reader(configFile);
		int numMC = r.getParameterInteger("monte_carlos");
		mcStats = new MonteCarloStatistics(numMC);
		int cores = Runtime.getRuntime().availableProcessors();
		//If enough cores are available, reduce in one to avoid overloading the computer.
		if(cores > 2) {
			cores--;
		}
		ExecutorService pool = Executors.newFixedThreadPool(cores);
		try {
			for (int i=0; i<numMC; i++) {
				ParallelExecutor worker = new ParallelExecutor(configFile, i, mcStats);
				worker.setOPtimizationVariables(optParameters);
				pool.execute(worker);
			}
			//Close pool (no more tasks will be scheduled).
			pool.shutdown();
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}catch(Exception e) {
			System.out.println("Simulation failed");
		}finally {
//			System.out.println("Done");
		}
	}
	public double getBenefits() {
		return mcStats.computeCampaignBenefitByMC();
	}
	public double getCosts() {
		return mcStats.computeCampaignCostByMC();
	}
	public double getBenefistsMinusCostsCombination() {
		return mcStats.computeBenefCostsCombination();
	}
	public double getTotalsimulationTime() {
		return mcStats.computeTotalSimulationTime();
	}
}
