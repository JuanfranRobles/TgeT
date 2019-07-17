package test.model;

import simulator.Simulator;

public class TestParallel {

	public static void main(String[] args) {
		String configFile = "./configuration_files/Random/config_erdos_renyi_I.properties";
		Simulator sim = new Simulator(configFile);
		double [] nwt = new double [] {0.0, 0.0, 1.0, 10.0};
		sim.setOptParameters(nwt);
		sim.simulateModel();
		System.out.println("Benefs: " + sim.getBenefits() + " | Costs: " + sim.getCosts());
		System.out.println("Simulation time: " + sim.getTotalsimulationTime());
	}

}
