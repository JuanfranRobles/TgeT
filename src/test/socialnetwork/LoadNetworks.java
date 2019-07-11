package test.socialnetwork;

import socialnetwork.SocialNetwork;

public class LoadNetworks {
	
	public static void testChargeNetwork(String path, int numEvaluations){
		
		double sT, eT;
		double tT = 0.0;
		
		for(int eval = 0; eval < numEvaluations; eval++){
			sT = System.currentTimeMillis();
			SocialNetwork nw = new SocialNetwork(path);
			eT = System.currentTimeMillis();
			tT += (eT - sT) / 1000.0;
		}
		
		System.out.println("Total time spent loading the network stored in " + path + " is: " + 
		Double.toString(tT / (double) numEvaluations));
		
	}
	
	public static void testCalculateNetworkMetrics(String path, int numEvaluations){
		
		double sT, eT;
		double tT = 0.0;
		SocialNetwork nw = new SocialNetwork(path);
		
		for(int eval = 0; eval < numEvaluations; eval++){
			sT = System.currentTimeMillis();
			nw.setNetworkMetrics();
			eT = System.currentTimeMillis();
			tT += (eT - sT) / 1000.0;
		}
		nw.getNetworkMetrics().display();

		System.out.println("Total time spent calculating the metrics of the social network stored in " + path + " is: " + 
		Double.toString(tT / (double) numEvaluations));
		
	}
	public static void main (String args []){
		
		if(args.length < 2){
			System.err.println("Error!!! \n This program needs the path of a graph in order to properly charge a social network");
		}
		else{
			System.out.println("Starting validation");
			System.out.println("Estimating the average time for load networks");
			testChargeNetwork(args[0], Integer.parseInt(args[1]));
			System.out.println("Ending");
			System.out.println("Estimating the average time needed to calculate metrics related to a social network");
			testCalculateNetworkMetrics(args[0], Integer.parseInt(args[1]));
			System.out.println("Ending");
		}
		
	}
}
