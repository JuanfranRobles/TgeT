package socialnetwork;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSourceDGS;

import org.apache.commons.cli.*;   // for CLI parsing arguments


// http://graphstream-project.org/doc/Tutorials/Reading-files-using-FileSource_1.0/


/**
 * This class if an external util class for the project.
 * The goal is to create a social network using GraphStream and save it as a file
 * to be used during the simulation
 *
 * @date 4/07/2016
 * @author mchica
 * 
 */

/** Class for creating N social networks and save into files  */
public class SNGenerator {

	public final static int EXISTING_NETWORK = -1;
	public final static int SF_NETWORK = 0;
	public final static int ER_NETWORK = 1;
	public final static int SW_NETWORK = 2;
	public final static int REGULAR_GRID = 3;
	
	static int nrNetworks;
	static String outputFile;
	static int nrAgents; 
	
	static int typeSN;
	static double density;	

	
	// specific parameters for SF
	static int initNodes = 2;
	static int m;
	static boolean strictNumLinks = false;

	// specific parameters for ER
	

	// specific parameters for SW
	static int kNNs = 2; // must be even as it is the k nearest neighbours of the ring (k/2 on each side)
	static double probRewiring = 0.1;
	
	// random number generator
	static ec.util.MersenneTwisterFast random;
	 	
	
	/**
	 * Create an options class to store all the arguments of the command-line call of the program
	 * 
	 * @param options the class containing the options, when returned. It has to be created before calling
	 */
	private static void createArguments (Options options) {
				
		// add the options to a global one
		
		options.addOption("nrAgents", true, "Number of agents of the simulation");
		options.addOption("nrNetworks", true, "Number of different SN to create with the same configuration (different random seeds)");
		options.addOption("typeSN", true, "Types of SN to create: Lattice, ER, Scale-Free");
		options.addOption("density", true, "Density to create the SN");
		options.addOption("outputFile", true, "File to store the created SN");
		options.addOption("m", true, "m parameter for scale-free SN");
		options.addOption("probRewiring", true, "Prob. of rewiring for the Small World network");

		options.addOption("help", false, "Show help information");	
		
		// set as optional those arguments they are optional
		/*options.getOption("density").setOptionalArg(true);
		options.getOption("probRewiring").setOptionalArg(true);
		options.getOption("m").setOptionalArg(true);
		
		options.getOption("density").setRequired(false);
		options.getOption("probRewiring").setRequired(false);
		options.getOption("m").setRequired(false);*/
					
		
	}
	
	// auxiliary function 
	private static void compareDD(int[] dd1, int[] dd2, int numberOfNodes) { 
	  
		int dMax = Math.max(dd1.length, dd2.length); 
		
		for (int d = 0; d < dMax; d++) { 
		   int d1 = d < dd1.length ? dd1[d] : 0; 
		   int d2 = d < dd2.length ? dd2[d] : 0; 
			
		   System.out.println("Difference between distr. degree is " + ((d1 - d2 + 0.0) / numberOfNodes)); 
		}		  
	 } 
	
	/**
	 * The main function to call the SN generator
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
				
		// parsing the options to create the SN
		Options options = new Options();
		
		createArguments (options);		

		// create the parser
	    CommandLineParser parser = new DefaultParser();
	    
	    try {
	    	
	        // parse the command line arguments for the given options
	        CommandLine line = parser.parse( options, args );
	        
	        // retrieve the arguments
	        		    
		    if( line.hasOption( "nrAgents" ) )		    
		    	nrAgents = Integer.parseInt(line.getOptionValue("nrAgents"));
		    else 		    	
		    	nrAgents = 1000;
		    
		    if( line.hasOption( "nrNetworks" ) )			    
		    	nrNetworks = Integer.parseInt(line.getOptionValue("nrNetworks"));		    	  	
		    else		    	
		    	nrNetworks = 1;		
		    
		    if( line.hasOption( "outputFile" ) )
		    	outputFile = line.getOptionValue("outputFile");
		    else		    	
		    	outputFile = "./outputSN";		    	
		    
		    if( line.hasOption( "density" ) ) 			    
		    	density = Float.parseFloat(line.getOptionValue("density"));
		    else 
		    	density = 0.0001;
		    
		    if( line.hasOption( "m" ) ) 			    
		    	m = Integer.parseInt(line.getOptionValue("m"));
		    else 				
				m = 2;
		    
		    if( line.hasOption( "probRewiring" ) )
		    	probRewiring = Float.parseFloat(line.getOptionValue("probRewiring"));
		    else		    	
		    	probRewiring = 0.1;	
		    
		    if( line.hasOption( "typeSN" ) ) {
			    
		    	typeSN = Integer.parseInt(line.getOptionValue("typeSN"));		    	  	
		    	
		    	if (typeSN != SF_NETWORK && typeSN != ER_NETWORK && typeSN != SW_NETWORK  && typeSN != REGULAR_GRID && typeSN != EXISTING_NETWORK) {
			        System.err.println( "Undefined type of SN: " + " typeSN is not defined as a type of SN" );		    		
		    	}		    	
		    }
		    
		    // help information
		    if( line.hasOption("help") ) {
			    	
			    // automatically generate the help statement
			    HelpFormatter formatter = new HelpFormatter();
			    formatter.printHelp( "SNGenerator. October 2016. Manuel Chica. UOC / UON", options );			   	
			}
		    
	    }
	    
	    catch( ParseException exp ) {
	    	
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	   
	    // when all the parameters are retrieved, we create the SN using GraphStream
		
		FileSinkDGS fileSink = new FileSinkDGS();
		FileSourceDGS fileSource = new FileSourceDGS();
		
		// create the random number generator
		random = new ec.util.MersenneTwisterFast();
	
		
        System.out.println("\n****** GENERATING " + nrNetworks + " SOCIAL NETWORKS: ******\n");
		
        System.out.println("Param nrAgents: "+ nrAgents);
        System.out.println("Param Density: "+ density);
        
        if (typeSN == SF_NETWORK)
        	System.out.println("Param m for SF: "+ m);
        
        System.out.println("Param typeSN: " + typeSN);
        System.out.println("Param outputFile: " + outputFile);
        
        Graph graph = null;
        
		for (int net = 0; net < nrNetworks; net++) {

			// i-th SEED 
			long time1 = System.currentTimeMillis ();
			random.setSeed(System.currentTimeMillis ());
			
			// check the type of SN and generate it in consequence
			
			switch (typeSN) {
			
				case SF_NETWORK:
					
					// arguments example: -typeSN 0 -nrAgents 1024 -m 2 -outputFile './SF-1024nodes_m_2_0.dgs' -nrNetworks 1
					
					// calculating 'm' for SF
			        if(m < 1) 
						m = 1;
					
					graph = new SingleGraph("Barabàsi-Albert");
										
					BarabasiAlbertGenerator genSF = new BarabasiAlbertGenerator(m);
					genSF.setExactlyMaxLinksPerStep(true);

					System.out.println("Generating standard GraphStream BA [strictLinks = " + 
					genSF.produceExactlyMaxLinkPerStep() + "]");
					
					genSF.addSink(graph);
					genSF.begin();
					 
					for(int k=0; k<(nrAgents - initNodes); k++) {
						genSF.nextEvents();
					}
					 
					genSF.end();
											
					break;
					
				case ER_NETWORK:
					
					/* running parameters:  -nrAgents 1000 -nrNetworks 1 -density 0.004 -outputFile './ER-1000nodes_k_4' -typeSN 1
					 * */
										
					// creating an ER SN given an average degree k
					// this generator creates random graphs of any size n with given average degree 'k'
					// and binomial degree distribution B(n, k / (n - 1))
		    				    
				    // density = 2E / N(N-1) &  <K> = 2	E/N  THEN, D = <K>/(N-1)  AND  <K> = (N-1)D  
				    // AND prob = <K>/(N-1) = density
				    				    
				    // we use <K> = (N-1)D for the SN generation				    		    
				       
					graph = new AdjacencyListGraph("test");

					int avgDegree = Math.round((float)((nrAgents-1) * density));
					
					
					// FROM http://www.programcreek.com/java-api-examples/index.php?api=org.graphstream.algorithm.generator.RandomGenerator
				    				   				
					Generator gen = new RandomGenerator(avgDegree);
					gen.addSink(graph);
					
					gen.begin();
					while (graph.getNodeCount() < nrAgents) {
						gen.nextEvents();
					}
					gen.end();
					gen.removeSink(graph);
					
					// check if the average degree is k
					System.out.println("Expected avgDegree is " + avgDegree 
							+ ", real value of ER SN is " + Toolkit.averageDegree(graph));
										
					// compare the distributions
					// SNGenerator.compareDD(ddR, ddE1, N);					
					
					break;
					
				case SW_NETWORK:
					
					// Small World Watts-Strogatz generator 
					// Watts, D.J. and Strogatz, S.H. Collective dynamics of ‘small-world’ networks. Nature 393 (6684): 409–10

					graph = new SingleGraph("SW");
										
					kNNs = Math.round((float)((nrAgents-1)*density));
					
					WattsStrogatzGenerator genSW = new WattsStrogatzGenerator(nrAgents, kNNs, probRewiring);

					genSW.addSink(graph);
					genSW.begin();
					
					while(genSW.nextEvents()) {}
					
					genSW.end();
					
					break;	
					
				case REGULAR_GRID:
					 
					/* running parameters: -nrAgents 1000 -nrNetworks 1 -outputFile './Regular-1024nodes' -typeSN 3  
					 */
					
					
					/* At the contrary of most generators, this generator does not produce only one node 
					 * when you call nextEvents(). It adds a row and column to the grid, making the side 
					 * of the square grow by one. 
					 * Therefore if you call the nextEvents() methode n times you will have n^2 nodes.
					 * 
					 At each call to nextEvents() ((n+1)2)* new nodes are generated with n the size of a side of the grid.
					 */
					
					graph = new SingleGraph("grid");
					
					// we set a torus by setting second boolean parameter to true (all the nodes have 4 links). 
					// First parameter is false as we don't want diagonal links
					GridGenerator genRegular = new GridGenerator(false, true);
					  
					genRegular.addSink(graph);
					genRegular.begin();

					int numEvents = (int) Math.round(Math.sqrt(nrAgents)) - 1;
					
					System.out.println("\nSetting events to "  + numEvents 
							+ " as " + (numEvents+1) + "^2 = " + (numEvents+1)*(numEvents+1) + " is the closest value to " + nrAgents);
					
				    for(int k = 0; k < numEvents; k++) {
				    	genRegular.nextEvents();				    	
				    }
					
					genRegular.end();
					
					// Nodes already have a position.

					break;	
				
				case EXISTING_NETWORK:
					
					// load existing SN
					System.out.println("\nLoading SN from a file (" + outputFile + ") to show statistics... \nWARNING: We are not creating a new SN but loading show stats.\n\n" );
					
					break;
					
				default:					
					System.err.println( "Undefined type of SN: " + " typeSN is not defined as a type of SN" );	
					
					break;
						
			}
			
			try {
				
				if (typeSN == EXISTING_NETWORK) {
				
					
					graph = new SingleGraph("SNFromFile");
	
					fileSource.addSink(graph);
					fileSource.readAll(outputFile);
	
					fileSource.removeSink(graph);
					
				} else {
	
					// the SN has been generated for i-th run
	
			        System.out.println("\n****** " + (net + 1) + "-th SN GENERATED. STATS ARE: ******\n");
			        
					long  time2  = System.currentTimeMillis( );
					System.out.println((double)(time2 - time1)/1000 + "s for generating SN #" + net);
					
					// save the created SN in a file
					
					time1 = System.currentTimeMillis( );
	
					fileSink.writeAll(graph, outputFile  + "_" + net + ".dgs");
					
					time2  = System.currentTimeMillis( );
	
			        System.out.println("\n****** STORED SUCCESSFUL. SN STORED IN FILE " + outputFile  + "_" + net + ".dgs IN  " + 
			        		(double)(time2 - time1)/1000 + " ******\n");
			        
			        graph.clear();
					
					time1 = System.currentTimeMillis( );
	
					fileSource.addSink(graph);
					fileSource.readAll(outputFile  + "_" + net + ".dgs");
					time2  = System.currentTimeMillis( );
					
	
			        System.out.println("****** AFTER STORING, TESTING READING SN FROM FILE IN  " + 
			        		(double)(time2 - time1)/1000 + " ******\n");
			        
					fileSource.removeSink(graph);
					
				}
				
			} catch (FileNotFoundException e) {
				
				System.err.println("Error when reading file for serializing SN: " + e.getMessage());
				
			} catch (IOException e) {
				
				System.err.println("Error when serializing SN: " + e.getMessage());
				
			} 
						

			System.out.println("---- STATS SUMMARY ABOUT THE SN: -----");
			
			System.out.println("Number of nodes: "+ graph.getNodeCount());
			System.out.println("Density: "+ Toolkit.density(graph));
			System.out.println("Avg. degree: " + Toolkit.averageDegree(graph));
			System.out.println("Dev. avg. degree: " + Toolkit.degreeAverageDeviation(graph));
			System.out.println("CC: " + Toolkit.averageClusteringCoefficient(graph));
			
			
			int distr[] = Toolkit.degreeDistribution(graph);
			System.out.println("\nDistribution degree: "); 
			for (int k = 0; k < distr.length; k++)
				System.out.println("degree "  + k + "; " + distr[k]);
			
					
		}
            	
	}

}
