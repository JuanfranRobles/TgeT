package test.model;

import simulator.Simulator;

public class TestParallel {

	public static void main(String[] args) {
		String configFile = "/home/jfrobles/git/TgeT/configuration_files/ScaleFree/config_scale_free_II.properties";
		Simulator sim = new Simulator(configFile);
		double [] nwt = new double [] {0.8, 0.1, 0.1, 50.0};
		sim.setOptParameters(nwt);
		sim.simulateModel();
		System.out.println("Benefs: " + sim.getBenefits() + " | Costs: " + sim.getCosts());
		System.out.println("Simulation time: " + sim.getTotalsimulationTime());
	}

}
