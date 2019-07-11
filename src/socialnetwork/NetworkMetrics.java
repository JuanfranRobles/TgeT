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

import java.util.Arrays;

/**
 * This class store some social network metrics to use them easily.
 * @author Juan Francisco Robles <juanfrarobles@gmail.com>
 */

import com.google.common.primitives.Doubles;

public class NetworkMetrics {
    // Vectors where SN metrics will be stored.
    static double [] d;
    static double [] ts;
    static double [] cc;
    static double [] apl;
    static double [] ecc;
    static double [] bc;
    static double [] clc;
    // Vector for normalized (max-min) metrics.
    static double [] nd;
    static double [] nts;
    static double [] ncc;
    static double [] napl;
    static double [] necc;
    static double [] nbc;
    static double [] nclc;
    /**
     * Constructor.
     * <p>
     * Creates a SNMetrics object from a given SN calculating:
     * 1) Degree.
     * 2) Number of nodes at two steps for each node.
     * 3) Clustering coefficient.
     * @param sn 
     */
    public NetworkMetrics(SocialNetwork sn){
        
        int size = sn.getNumNodes();
        d = new double[size];
        System.arraycopy(sn.getDegreeDistribution(), 0, d, 0, size);
        
        ts = new double[size];
        System.arraycopy(sn.getTwoStepNodes(), 0, ts, 0, size);
        
        cc = new double[size];
        System.arraycopy(sn.getClusteringCoefficients(), 0, cc, 0, size);
        
//        apl = new double[size];
//        System.arraycopy(sn.getAveragePathLengths(), 0, apl, 0, size);
        
//        ecc = new double[size];
//        System.arraycopy(sn.getEccentricities(), 0, ecc, 0, size);
        
//        bc = new double[size];
//        System.arraycopy(sn.getBetweennessCentrality(), 0, bc, 0, size);
        
//        clc = new double[size];
//        System.arraycopy(sn.getClosenessCentrality(), 0, clc, 0, size);
        
        // Obtaining normalized metrics.
        nd = new double[size];
        nts = new double[size];
        ncc = new double[size];
//        napl = new double[size];
//        necc = new double[size];
//        nbc = new double[size];
//        nclc = new double[size];
        
        calculateMetrics(sn);
    }
    /* ----------------- Getters ------------------------------*/
    /**
     * Get degree metric.
     * @return Node degrees array for SN.
     */
    public double [] getDegMetric(){
        return d;
    }
    /**
     * Get two step metric.
     * @return Node two steps metric array for SN.
     */
    public double [] getTstepMetric(){
        return ts;
    }
    /**
     * Get clustering coefficioent metric.
     * @return Clustering coefficient metric array.
     */
    public double [] getCcMetric(){
        return cc;
    }
    /**
     * Get normalized degree metric.
     * @return Normalized degree metric array.
     */
    public double [] getNormDegMetric(){
        return nd;
    }
    /**
     * Get normalized two steps metric.
     * @return Normalized two steps metric array.
     */
    public double [] getNormTstepMetric(){
        return nts;
    }
    /**
     * Get normalized clustering coefficient metric.
     * @return Normalized clustering coefficient metric array.
     */
    public double [] getNormCcMetric(){
        return ncc;
    }
    /**
     * Get degree for a single node.
     * @param node
     * @return 
     */
    public double getNodeDegMetric(int node){
        return d[node];
    }
    /**
     * Get two steps metric value for a single node.
     * @param node
     * @return 
     */
    public double getNodeTstepMetric(int node){
        return ts[node];
    }
    /**
     * Get clustering coefficient for a single node.
     * @param node
     * @return 
     */
    public double getNodeCcMetric(int node){
        return cc[node];
    }
    /**
     * Get normalized degree for a single node.
     * @param node
     * @return 
     */
    public double getNormNodeDegMetric(int node){
        return nd[node];
    }
    public double getNormAplMetric(int node){
    	return napl[node];
    }
    public double getNormEccentricityMetric(int node){
    	return necc[node];
    }
    public double getNormBetweenessMetric(int node){
    	return nbc[node];
    }
    public double getNormClosenessMetric(int node){
    	return nclc[node];
    }
    /**
     * Get normalized two steps metric value for a single node.
     * @param node
     * @return 
     */
    public double getNormNodeTstepsMetric(int node){
        return nts[node];
    }
    /**
     * Get normalized clustering coefficient metric value for a single node.
     * @param node
     * @return 
     */
    public double getNormNodeCcMetric(int node){
        return ncc[node];
    }
    
    /* ----------------- Class methods ----------------------- */
    /**
     * Normaliza las medidas asociadas a los agentes de la clase para la ponderación de su influencia en la red. 
     * Estas medidas son 3: 
     * - Grado del nodo. 
     * - Número de vecinos a 2 pasos del nodo (distancia 2 o dos arcos entre ellos). 
     * - Coeficiente de clustering del nodo. 
     * @param network Red social del modelo de compra.
     */
    private void calculateMetrics(SocialNetwork network){
        // Maximums:
        //double maxD = Doubles.max(d);
        //double maxTs = Doubles.max(ts);
        double maxD = (double) network.getNumNodes() - 1.0;
        double maxTs = (double) network.getNumNodes() - 1.0;
        //double maxCc = Doubles.max(cc);
        //double maxApl = Doubles.max(apl);
        //double maxEcc = Doubles.max(ecc);
        //double maxBc = Doubles.max(bc);
        //double maxClc = Doubles.max(clc);
        
        // Normalization. 
        for(int num_node = 0; num_node < d.length; num_node++){
            nd[num_node] = d[num_node] / maxD;
            nts[num_node] = ts[num_node] / maxTs;
            // ncc[num_node] = 1.0 - cc[num_node] / maxCc;
            ncc[num_node] = 1.0 - cc[num_node]; // Better if is low.
            //napl[num_node] = (maxApl - apl[num_node]) / maxApl;
            //necc[num_node] = ecc[num_node] / maxEcc; // Better if is low.
            //nbc[num_node] = bc[num_node] / maxBc; // 
            //nclc[num_node] = clc[num_node] / maxClc;
        }
    }
    public void display(){
    	System.out.println("| -- Network Metrics --|");
    	
    	System.out.println("| -- Not normalized netwrok metrics --|");
    	
    	System.out.println("Node degrees");
    	System.out.println(Arrays.toString(d));
    	System.out.println("Friends of friends");
    	System.out.println(Arrays.toString(ts));
    	System.out.println("Clustering coefficients");
    	System.out.println(Arrays.toString(cc));
    	/*System.out.println("Average path lenghts");
    	System.out.println(Arrays.toString(apl));
    	System.out.println("Eccentricity centrality");
    	System.out.println(Arrays.toString(ecc));
    	System.out.println("Betweeness centrality");
    	System.out.println(Arrays.toString(bc));
    	System.out.println("Closeness centrality");
    	System.out.println(Arrays.toString(clc));*/
    	
    	System.out.println("| -- Normalized network metrics --|");
    	
    	System.out.println("Normalized node degrees");
    	System.out.println(Arrays.toString(nd));
    	System.out.println("Normalized friends of friends");
    	System.out.println(Arrays.toString(nts));
    	System.out.println("Normalized clustering coefficients");
    	System.out.println(Arrays.toString(ncc));
    	/*System.out.println("Normalized average path lenghts");
    	System.out.println(Arrays.toString(napl));
    	System.out.println("Normalized eccentricity centrality");
    	System.out.println(Arrays.toString(necc));
    	System.out.println("Normalized betweeness centrality");
    	System.out.println(Arrays.toString(nbc));
    	System.out.println("Normalized closeness centrality");
    	System.out.println(Arrays.toString(nclc));*/
    	
    	System.out.println("| -- End Network Metrics --|");
    }
    
}