/*
 * Copyright (C) 2017 Juan Francisco Robles Fuentes. 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package socialnetwork;

/**
 * @author Juan Francisco Robles Fuentes <juanfrarobles@gmail.com>.
 * @version 1.0
 * @since 0.0
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.APSP.APSPInfo;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.measure.ClosenessCentrality;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import static org.graphstream.algorithm.Toolkit.*;
import org.graphstream.stream.file.FileSinkGEXF;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceGEXF;
import org.graphstream.stream.file.FileSourceDGS;

/**
 * Class Social Network.
 * <p>
 * This class creates a social network topology. These social network represents
 * the conections between customers in a market model.  
 * There are four main types of social networks implemented in the class: 
 * - Random network -> following the Baràbasi-Albert model.
 * - Scale free network -> following the Ërdos-Renyi model.
 * - Small world network -> following the Watts-Strogatz, Badham-Stocker or Newman models.
 * - Regular lattice network -> following a square grid model.
 * - Dorogovtsev-Mendes network.
 * - Random euclidean network.
 * 
 * All the above-mentioned networks are implemented in the GraphStream library.
 * For more information visit the following link: http://graphstream-project.org/
 * <p>
 */

public class SocialNetwork {
    // Red Social. Objeto graph de la librería GraphStream.
    /**
     * Graph structure where nodes and connections are stored. 
     */
    private Graph network;
    /**
     * Number of nodes in social network.
     */
    private int numNodes;
    /**
     * Number of edges in social network.
     */
    private int numEdges;
    /**
     * Metrics related to the social network.
     */
    private NetworkMetrics metrics;
    
    // Flags 
    static String filePath = "";
    static boolean convert = false;
    static boolean display = false;
    static boolean info = false;
    
    /* ----------------------------------- Constructors. ---------------------------------------- */
    /**
     * Default constructor. 
     * <p>
     * Creates an empty social network.
     * <p>
     */
    public SocialNetwork(){
        this.network = new SingleGraph("Network");
        this.numNodes = 0;
        this.numEdges = 0;
    }
    /**
     * Constructor.
     * <p>
     * Creates a social network given a graph as parameter.
     * <p>
     * @param graph Social network graph.
     */
    public SocialNetwork(Graph graph){
        network = graph;
        numNodes = graph.getNodeCount();
        numEdges = graph.getEdgeCount();
    }
    /**
     * Constructor.
     * <p>
     * Creates a social network object given the graph file path as parameter.
     * The input file require an .gexf or .dgs format. 
     * These file formats are required as two of the mos spreaded formats in 
     * social network. 
     * 
     * For more information visit:
     * 1) https://gephi.org/gexf/format/
     * 2) http://graphstream-project.org/doc/Advanced-Concepts/The-DGS-File-Format/
     * <p>
     * @param filePath Input file (.gexf or .dgs).
     */
    public SocialNetwork(String filePath){
        try {
            initializeNetwork(filePath); // Initialize the social network graph (@see consumermodel.SocialNetwork.initializeNetwork.java). 
        } catch (IOException ex) {
            Logger.getLogger(SocialNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        numNodes = this.network.getNodeCount(); // Setting the number of edges and nodes.
        numEdges = this.network.getEdgeCount();
        setNetworkMetrics();
    }
    /* ----------------------------------- Setters. ------------------------------------------- */
    /**
     * Set the number of nodes in social network.
     * @param numNodes The number of nodes in social network.
     */
    public void setNumNodes(int numNodes){
        this.numNodes = numNodes;
    }
    /**
     * Set the number of edges in social network.
     * @param numEdges The number of edges in social network.
     */
    public void setNumEdges(int numEdges){
        this.numEdges = numEdges;
    }
    /**
     * Set the social network graph.
     * @param graph The social network graph.
     */
    public void setGraph(Graph graph){
        this.network = graph;
    }
    /**
     * Set the social network metrics. 
     */
    public void setNetworkMetrics(){
    	metrics = new NetworkMetrics(this);
    }
    /* -------------------------------------- Getters. --------------------------------------- */
    /**
     * Get the number of nodes of social network.
     * @return The number of nodes of the social network.
     */
    public int getNumNodes(){
        return this.numNodes;
    }
    /**
     * Get the number of edges of the social network.
     * @return The number of edges of the social network.
     */
    public int getNumEdges(){
        return this.numEdges;
    }
    /**
     * Get a node of the social network graph.
     * @param nodePos The number of node in the social network.
     * @return Node object that store information about the node.
     */
    public Node getNode(int nodePos){
        return this.network.getNode(nodePos);
    }
    /**
     * Get the social network graph.
     * @return Social network graph.
     */
    public Graph getGraph(){
        return this.network;
    }
    /**
     * Get the social network's average degree. 
     * @return Average degree.
     */
    public double getAverageDegree(){
        return averageDegree(this.network);
    }
    /**
     * Get the social network's density.
     * @return Density. 
     */
    public double getDensity(){
        return density(this.network);
    }
    /**
     * Get social network's clustering coefficient. 
     * @return Average clustering coefficient.
     */
    public double getAverageCC(){
        return averageClusteringCoefficient(this.network);
    }
    /**
     * Get social network's diameter.
     * @return Diameter.
     */
    public double getDiameter(){
        return diameter(this.network);
    }
    /**
     * Get social network metrics. 
     * @return metrics
     */
    public NetworkMetrics getNetworkMetrics(){
    	return metrics;
    }
    
    /* ------------------------------------ Class Methods. ---------------------------------- */
    /**
     * Initializes the social network attributes.
     * <p>
     * This method reads a social network file in format .gexf or .dgs and initializes
     * the social network graph, number of nodes and edges.
     * <p>
     * 
     * @param filePath File path with social network's graph.
     * @throws IOException To deal with read problems.
     */
    private void initializeNetwork(String filePath) throws IOException{
        this.network = new SingleGraph("Network"); // Creates an empty social network object.
        //System.out.println("Loading social network graph stored in: " +  filePath);
        String [] format = filePath.split("gex"); // Confirming file format.
        String f = "dgs";
        
        if(format.length > 1){
            f = format[1];
        }
        
        FileSource fsink; 
        
        if("f".equals(f)){
            fsink = new FileSourceGEXF();
        }
        else{
            fsink = new FileSourceDGS();
        }
        
        fsink.addSink(this.network); // Loading graph using FileSource object.
        fsink.readAll(filePath);
    }
    /**
     * Get the neighbors of a social network's node. 
     * @param node Number of node.
     * @return Node neighbors.
     */
    public List<Integer> getNeighborsOfNodeFromGS (int node) {
        List<Integer> neighbors = new ArrayList<>();
        Iterator<Node> it = this.network.getNode(node).getNeighborNodeIterator();
        while(it.hasNext()) {
            int tmpInd = it.next().getIndex();
            neighbors.add(tmpInd);
        }		
		//Collections.sort(neighbors);	// Uncomment to sort the neighbors before return.	
		return neighbors;
    } 
    /**
     * Calculte the degree distribution of social network.
     * @return Social network's degree distribution.
     */
    public double[] getDegreeDistribution(){
        double[] netDegrees = new double[this.getNumNodes()];
        for(int node =0; node < this.getNumNodes(); node++){
            netDegrees[node] = (double)this.network.getNode(node).getDegree();
        }
        return netDegrees;
    }
    /**
     * Get the number of nodes reachable to two edges of the node.
     * @return Number of nodes reachable at two steps from the given node.
     */
    public double[] getTwoStepNodes(){
        double[] netTwoSteps = new double[this.getNumNodes()];
        for(int node = 0; node < this.getNumNodes(); node++){
            netTwoSteps[node] = this.calculateTwoSteps(node);
        }
        return netTwoSteps;
    }
    /**
     * Get the clustering coefficient of the social network's nodes.
     * @return Clustering coefficients.
     */
    public double[] getClusteringCoefficients(){
        double[] netCCs = clusteringCoefficients(this.network);
        return netCCs;
    }
    /**
     * Average path length is a concept in network topology that is defined as the 
     * average number of steps along the shortest paths for all possible pairs of 
     * network nodes.
     * @return
     */
    public double [] getAveragePathLengths(){
    	double[] avpls = new double[this.getNumNodes()];

    	for (int node = 0; node < this.getNumNodes(); node++){
	        BreadthFirstIterator<Node> k = new BreadthFirstIterator<>(this.getGraph().getNode(node), false);
	        while (k.hasNext()) {
	                k.next();
	        }
	        for (int nodeaux = 0; nodeaux < this.getNumNodes(); nodeaux++){
                if (nodeaux != node) {
		            int depth = k.getDepthOf(this.getGraph().getNode(nodeaux));
		            if (depth >0) {
		            	avpls[node] += (double) depth;
		            }
                }
                avpls[node] /= ((double)this.getNumNodes() - (double)(this.getNumNodes()-1));
	        }
    	}
	    return avpls;
    }
    /* Centrality measures refer to a group of metrics that aim to quantify the 
     * "importance" or "influence" (in a variety of senses) of a particular node 
     * (or group) within a network.
     * */
    /**
     * Eccentricity  is the largest of the elementary paths from a node to any other vertex of the connected graph.
     * @return
     */
    public double [] getEccentricities(){
    	double [] eccentricities = new double[this.getNumNodes()];
    	
    	//APSP apsp = new APSP();
 		//apsp.init(this.getGraph()); // registering APSP as a sink for the graph
 		//apsp.setDirected(false); // undirected graph
 		//apsp.compute(); // the method that actually computes shortest paths
 		
    	for(int node = 0; node < this.getNumNodes(); node++){
    		//APSPInfo info = this.getGraph().getNode(node).getAttribute(APSPInfo.ATTRIBUTE_NAME);
    		//eccentricities[node] = 1.0 / (double) info.getMaximumLength();
    		eccentricities[node] = 1.0 / (double) unweightedEccentricity(this.getGraph().getNode(node), false);
    	}
    	
    	return eccentricities;
    }
    /**
     * The betweenness centrality counts how many shortest paths between each pair of nodes 
     * of the graph pass by a node. 
     * It does it for all nodes of the graph.
     * @return
     */
    public double [] getBetweennessCentrality(){
    	double [] betweeness = new double[this.getNumNodes()];
    	
    	BetweennessCentrality bc = new BetweennessCentrality();
    	bc.init(this.getGraph());
    	bc.compute();
    	
    	for(int node = 0; node < this.getNumNodes(); node++){
    		betweeness[node] = this.getGraph().getNode(node).getAttribute("Cb");
    	}
    	return betweeness;
    }
    /**
     * The closeness centrality in a graph refers to a possible measure of a vertex in said graph, 
     * which determines its relative importance within it.
     * @return
     */
    public double [] getClosenessCentrality(){
    	double [] closeness = new double [this.getNumNodes()];
    	
    	ClosenessCentrality cc = new ClosenessCentrality();
    	cc.init(this.getGraph());
    	cc.compute();
    	
    	for(int node = 0; node < this.getNumNodes(); node++){
    		closeness[node] = this.getGraph().getNode(node).getAttribute("closeness");
    	}
    	return closeness;
    }
    /**
     * Shows the social network information in screen 
     * @param mode True if the graph remains static and else if not.
     */
    public void displayNetwork(boolean mode){
        this.network.display(mode);
    }
    /**
     * Visualize social network information.
     */
    public void displayInformation(){
        System.out.println("Number of network nodes: " + this.getNumNodes());
        System.out.println("Number of network edges: " + this.getNumEdges());
        System.out.println("Network Average Degree: " + this.getAverageDegree());
        System.out.println("Network Density: " + this.getDensity());
        System.out.println("Network Average Clustering Coefficient: " + this.getAverageCC());
        System.out.println("Network Diameter: " + this.getDiameter());
    }
    /* --------------------------------------- Node methods. -------------------------------------- */
    /**
     * tulate a single node degree. 
     * @param node Node. 
     * @return Real Node degree. 
     */
    public double calculateDegree(Node node){
        return (double)node.getDegree();
    }
    /**
     * Calculate the number of nodes reachable at two steps from the given node. 
     * @param numNode Origin node. 
     * @return Number of nodes at two steps from the origin node.
     */
    public double calculateTwoSteps(int numNode){
        Set<Integer> neighboursTwoSteps = new HashSet<>();
        List<Integer> neighbours = getNeighborsOfNodeFromGS(numNode);
        neighboursTwoSteps.addAll(neighbours);

        for(int neighbour = 0; neighbour < neighbours.size(); neighbour++){
            neighboursTwoSteps.addAll(this.getNeighborsOfNodeFromGS(neighbour));
        }
        
        return (double)neighboursTwoSteps.size();
    }
    /**
     * Calculate the clustering coefficient of the given node.
     * @param node Node.
     * @return Clusterig coefficient.
     */
    public double calculateClusteringCoefficient(Node node){
        double nodeCc = clusteringCoefficient(node);
        return nodeCc;
    }
    /* ----------- Clean and free memory methods. ----------------*/
    /**
     * Free memory by deleting the social network. 
     */
    public void cleanNetwork(){
        this.network.clear();
        this.network = null;
    }
    /**
     * Utility that converts an .dgs file to .gexf file.
     * @param outputFile 
     */
    public void toGexf(String outputFile){
        try {
            FileSinkGEXF output = new FileSinkGEXF(); // Creamos el fichero de salida.
            this.network.write(output, outputFile + ".gexf"); // Escribimos la red en el fichero con el formato deseado.
        } catch (IOException ex) {
            Logger.getLogger(SocialNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
    * Create an options class to store all the arguments of the command-line call of the program
    * 
    * @param options the class containing the options, when returned. It has to be created before calling
    */
    private static void createArguments (Options options) {

           // add the options to a global one
           options.addOption("inputFile", true, "File to load the SN graph");
           options.addOption("toGEXF", true, "Convert file to .dgs file");
           options.addOption("displaySN", true, "Display the loaded network");
           options.addOption("displayInfo", true, "Display information about SN");
           
           options.addOption("help", false, "Show help information");	

    }
    
    /**
	 * The main function to call SN class
	 * 
	 * @param args
	 */
    public static void main(String [] args){
        
        // parsing the options to create the SN
        Options options = new Options();

        createArguments (options);
        
        CommandLineParser parser = new DefaultParser();
        
        try {

            // parse the command line arguments for the given options
            CommandLine line = parser.parse( options, args );

            if( line.hasOption( "inputFile" ) )		    
                filePath = line.getOptionValue("inputFile");
            else 		    	
                System.err.println( "Undefined input file for SN" );

            if( line.hasOption( "toGEXF" ) )			    
                convert = Boolean.parseBoolean(line.getOptionValue("toGEXF"));		    	  	
            else		    	
                convert = false;
            
            if( line.hasOption( "displaySN" ) )			    
                convert = Boolean.parseBoolean(line.getOptionValue("displaySN"));		    	  	
            else		    	
                convert = false;
            
            if( line.hasOption( "displayInfo" ) )			    
                convert = Boolean.parseBoolean(line.getOptionValue("displayInfo"));		    	  	
            else		    	
                convert = false;
        }

        catch( ParseException exp ) {

            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
        
        long  time1  = System.currentTimeMillis( );
        
        SocialNetwork sn = new SocialNetwork(filePath);
        
        if( convert ){
            String outputFile = filePath.replace(".dgs", ".gexf");
            sn.toGexf(outputFile);
        }
        
        if( info )
            sn.displayInformation();
        
        if( display )
            sn.displayNetwork(true);
        
        long  time2  = System.currentTimeMillis( );
        System.out.println((double)(time2 - time1)/1000 + "s for loading SN ");
        
    }
        
}

