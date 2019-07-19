package test.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import configuration.Reader;
import simulator.Simulator;
import socialnetwork.SocialNetwork;
import util.Util;

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
		
		int [] exps = new int [0]; 
		if(args != null) {
			exps = new int [args.length];
			for(int v=0; v < args.length; v++) {
				for(int n=0; n < NETWORK_DIRS.length; n++) {
					if(args[v].equals(NETWORK_DIRS[n])) {
						exps[v] = n;
					}
				}
			}
		}
		try {
			for(Integer exptID: exps) {
			// For each configuration file
			for(int exp=0; exp < exps.length; exp++) {
				// Create a new simulator using the configuration file of the desired network 
	            simulator = new Simulator(experimentConfigurationFiles[exptID][exp]);
	            reader = new Reader(experimentConfigurationFiles[exptID][exp]);
	            maxTargets = (int)(new SocialNetwork(reader.getParameterString("network_path")).getNumNodes() *
	            				   reader.getParameterDouble("targets_ratio"));
	            // For each weight configuration
	            for(int gredParams=0; gredParams < greedyParameters.length; gredParams++) {
	            	fw = new FileWriter(RESULTS_PATH + NETWORK_DIRS[exptID] + "/" + NETWORK_DIRS[exptID] + "_" + Integer.toString(exp) + "_ConfigParams_" + gredParams  + ".xls");
	            	fw.write("Seeds, Benefit, Cost," + "\n");
	            	double [][] results = new double [maxTargets][2];
		            for(int numSeeds=1; numSeeds <= maxTargets; numSeeds++) {
		            	weights = new double [] {greedyParameters[gredParams][0], 
		            							 greedyParameters[gredParams][1],
		            							 greedyParameters[gredParams][2],
		            							 (double) numSeeds};
		        		simulator.setOptParameters(weights);
		        		simulator.simulateModel();
		        		results[numSeeds-1][0] = simulator.getBenefits();
		        		results[numSeeds-1][1] = simulator.getCosts();
		            }
		            ArrayList<Integer> sols = Util.fastNonDominatedSorting(results).get(0);
		            for(Integer s: sols) {
		            	fw.write(Integer.toString(s) + ", " + Double.toString(results[s][0]) + ", " + Double.toString(results[s][1]) + "\n");
		            }
		            fw.close();		
	            }
	      
            }
		}
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
        
	}
}
