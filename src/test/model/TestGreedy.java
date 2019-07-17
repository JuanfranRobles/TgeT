package test.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import configuration.Reader;
import simulator.Simulator;
import socialnetwork.SocialNetwork;

public class TestGreedy {
	
	private static final String CONFIGURATION_FILES_PATH = "./configuration_files/";
	private static final String [] NETWORK_DIRS = new String [] {"Random", "RegularLattice", 
																 "SmallWorld", "ScaleFree", 
																 "Bimodal"};
	private static final String FILE_EXTENSION = ".properties";
	// Random network configuration files. 
	public static String [] randomNetworkConfigurationFiles = new String [] {CONFIGURATION_FILES_PATH + 
																	   NETWORK_DIRS[0] + "/config_erdos_renyi_I" + 
																	   FILE_EXTENSION, 
																	   CONFIGURATION_FILES_PATH + 
																	   NETWORK_DIRS[0] + "/config_erdos_renyi_II" + 
																	   FILE_EXTENSION,
																	   CONFIGURATION_FILES_PATH + 
																	   NETWORK_DIRS[0] + "/config_erdos_renyi_III" + 
																	   FILE_EXTENSION};
	// Regular lattice network configuration files. 
	public static String [] regularLatticeNetworkConfigurationFiles = new String [] {CONFIGURATION_FILES_PATH + 
																			   NETWORK_DIRS[1] + "/config_regular_lattice_I" + 
																			   FILE_EXTENSION, 
																			   CONFIGURATION_FILES_PATH + 
																			   NETWORK_DIRS[1] + "/config_regular_lattice_II" + 
																			   FILE_EXTENSION,
																			   CONFIGURATION_FILES_PATH + 
																			   NETWORK_DIRS[1] + "/config_regular_lattice_III" + 
																			   FILE_EXTENSION};
	// Small world network configuration files. 
	public static String [] smallWorldNetworkConfigurationFiles = new String [] {CONFIGURATION_FILES_PATH + 
																		   NETWORK_DIRS[2] + "/config_small_world_I" + 
																		   FILE_EXTENSION, 
																		   CONFIGURATION_FILES_PATH + 
																		   NETWORK_DIRS[2] + "/config_small_world_II" + 
																		   FILE_EXTENSION,
																		   CONFIGURATION_FILES_PATH + 
																		   NETWORK_DIRS[2] + "/config_small_world_III" + 
																		   FILE_EXTENSION};
	// Scale free network configuration files. 
	public static String [] scaleFreeNetworkConfigurationFiles = new String [] {CONFIGURATION_FILES_PATH + 
																		  NETWORK_DIRS[3] + "/config_scale_free_I" + 
																		  FILE_EXTENSION, 
																		  CONFIGURATION_FILES_PATH + 
																		  NETWORK_DIRS[3] + "/config_scale_free_II" + 
																		  FILE_EXTENSION,
																		  CONFIGURATION_FILES_PATH + 
																		  NETWORK_DIRS[3] + "/config_scale_free_III" + 
																		  FILE_EXTENSION};
	// Bimodal network configuration files. 
	public static String [] bimodalNetworkConfigurationFiles = new String [] {CONFIGURATION_FILES_PATH + 
																	    NETWORK_DIRS[4] + "/config_animal_jam_I" + 
																	    FILE_EXTENSION, 
																	    CONFIGURATION_FILES_PATH + 
																	    NETWORK_DIRS[4] + "/config_animal_jam_II" + 
																	    FILE_EXTENSION,
																	    CONFIGURATION_FILES_PATH + 
																	    NETWORK_DIRS[4] + "/config_animal_jam_III" + 
																	    FILE_EXTENSION};
	
	// Experiments to perform. 
	public static String [][] experimentConfigurationFiles = new String [] [] {randomNetworkConfigurationFiles, 
																	  	 regularLatticeNetworkConfigurationFiles, 
																	  	 smallWorldNetworkConfigurationFiles,
																	  	 scaleFreeNetworkConfigurationFiles,
																	  	 bimodalNetworkConfigurationFiles};
	private static String RESULTS_PATH = "./results/";
	
	private static double [][] greedyParameters = new double [][] {{1.0,0.0,0.0}, {0.0, 1.0, 0.0},
																	{0.0, 0.0, 1.0}, {0.33,0.33,0.33}};
																	
	public static void main(String args[]){
		FileWriter fw = null;
		Simulator simulator; 
		Reader reader;
		int maxTargets;
		double [] weights;
		
		try {
		for(int network=0; network < experimentConfigurationFiles.length; network++) {    
			for(int exp=0; exp < experimentConfigurationFiles[network].length; exp++) {
				fw = new FileWriter(RESULTS_PATH + NETWORK_DIRS[network] + "/" + NETWORK_DIRS[network] + "_" + Integer.toString(exp) + ".xls");
//				fw.write("-------------------------------------------------------" + "\n");
//	            fw.write("Greedy experiments over " + NETWORK_DIRS[network] + "\n");
//	            fw.write("-------------------------------------------------------" + "\n");
//	            fw.write("\n");
	            simulator = new Simulator(experimentConfigurationFiles[network][exp]);
	            reader = new Reader(experimentConfigurationFiles[network][exp]);
	            maxTargets = (int)(new SocialNetwork(reader.getParameterString("network_path")).getNumNodes() *
	            				   reader.getParameterDouble("targets_ratio"));
	            for(int gredParams=0; gredParams < greedyParameters.length; gredParams++) {
//	            	fw.write("--- Parameters: " + Arrays.toString(greedyParameters[gredParams]) + " ------ " + "\n");
//	            	fw.write("--- Results found ---" + "\n");
//	            	fw.write("\n");
	            	fw.write("Seeds, Benefit, Cost," + "\n");
		            for(int numSeeds=1; numSeeds < maxTargets; numSeeds++) {
		            	weights = new double [] {greedyParameters[gredParams][0], 
		            							 greedyParameters[gredParams][1],
		            							 greedyParameters[gredParams][2],
		            							 (double) numSeeds};
		        		simulator.setOptParameters(weights);
		        		simulator.simulateModel();
		            	fw.write(Integer.toString(numSeeds) + ", " + Double.toString(simulator.getBenefits()) + ", " + Double.toString(simulator.getCosts()) + "\n");
		            }
		            fw.flush();
	            }
	            fw.write("\n");
	            fw.close();			
	            }
		}
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
        
	}
}
