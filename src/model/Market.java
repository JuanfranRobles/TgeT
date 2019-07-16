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

package model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import configuration.Reader;
import model.statistics.OptimizationStatistics;
import model.statistics.Statistics;
import model.statistics.StatisticsFunctions;
import optimization.OptimizationParameters;
import socialnetwork.SocialNetwork;
import util.SeedContainer;
import util.Util;
import util.random.Randomizer;
import util.random.RandomizerFactory;
import util.random.RandomizerFactory.RandomizerAlgorithm;

/**
 * @author Juan Francisco Robles Fuentes.
 */

/**
 * Market class.
 * 
 * This class serves to simulate the consumption environment (market): 
 * 
 * It is defined by the following set of parameters:
 * 	 -> sn: The social network which represent the connections between agents. 
 *   -> customers: The customers involved in the market.
 *   -> products: The products involved in the market.
 *   -> alpha: It is the degree to which a type of product ultimately can satisfy 
 *   an agent. Consumers are not easily satisfied with products having a low α 
 *   and spend more time reasoning about their decision in markets characterized by
 *    such products
 *    -> time: Simulation steps (days).
 *    -> type: The model type. false if we have a normal model (not awareness) and true
 *    if we have an extended awareness model. 
 *    
 */

public class Market {
	
	// Attributes for the basic market model.
	private SocialNetwork sn; // Social Network.
	private Customer [] customers; // Market consumers.
	private Product [] products; // Market products.
	// Consumat market variables. 
	private double alpha;
	private double b1;
	private double b2;
	private double buyprob; // Buy probability for consumers. 
	private int time; // Simulation steps. 
	private int stationality; // Consumption frequency. 
	private boolean type = false; // True agent's variables are initialized randomly. False if not. 
	private int product_visibility; // Product visibility among customers. 
	private boolean extended = false; // If market model includes an awareness mechanism for consumers or not. 
	// If this market will be used to optimize Viral Marketing campaigns. 
	// - True creates a new market model with P + 1 products to include a new product to optimize. 
	// - False creates a new market model with P products. 
	private boolean optimization = false; 
	private boolean mo = false; 
	// Random numbers generator. 
	private Randomizer g;
	// Configuration file reader. 
	private Reader configuration;
	// Structure to store simulation statistics. 
	private Statistics statistics; 
	
	private boolean randomSeedSelection = false; 
	// Optional parameters. Only activated when optimization 
	// flag is true.
	private OptimizationParameters optimizationParameters; 
	
	/* -------------- Constructors ------------------- */
	
	public Market(String configfile){
		// Loading social network from file.
		configuration = new Reader(configfile);
		sn = new SocialNetwork(this.configuration.getParameterString("network_path"));
		// Initializing customers and products.
		customers = new Customer[sn.getNumNodes()]; // The same number of customers as nodes in SN.
		optimization = this.configuration.getParameterBoolean("optimize");
		if (!this.toOptimize())
			products = new Product[this.configuration.getParameterInteger("num_prods")];
		else
			products = new Product[this.configuration.getParameterInteger("num_prods") + 1];
		// Initializing threshold.
		alpha = this.configuration.getParameterDouble("alpha_value");
		b1 = this.alpha / 2.0;
		b2 = 4.0;
		buyprob = this.configuration.getParameterDouble("buy_probability");
		time = this.configuration.getParameterInteger("days");
		stationality = this.configuration.getParameterInteger("stationality");
		type = this.configuration.getParameterBoolean("random_model");
		product_visibility = this.configuration.getParameterInteger("prod_visibility");
		extended = this.configuration.getParameterBoolean("extended_model");
		optimization = this.configuration.getParameterBoolean("optimize");
		mo = this.configuration.getParameterBoolean("multiobjective");
		statistics = new Statistics(this);	
	}
	
	/* -------------- Methods -------------------- */
	public void setCustomers(Customer [] cs){
		this.customers = new Customer [cs.length];
		System.arraycopy(cs, 0, this.customers, 0, this.customers.length);
	}
	
	private void setCustomer(int cid, Customer c){
		this.customers[cid] = c;
	}
	
	public void setProducts(Product [] ps){
		this.products = new Product [ps.length];
		System.arraycopy(ps, 0, this.products, 0, this.products.length);
	}
	
	private void setProduct(int pid, Product p){
		this.products[pid] = p;
	}
	
	public void setAlpha(double ap){
		this.alpha = ap;
	}
	
	public void setB1(double bo){
		this.b1 = bo;
	}
	
	public void setB2(double bt){
		this.b2 = bt;
	}
	
	public void setBuyProb(double bp){
		this.buyprob = bp;
	}
	
	public void setTime(int tm){
		this.time = tm;
	}
	
	public void setType(boolean tp){
		this.type = tp;
	}
	
	public void setExtended(boolean ext){
		this.extended = ext;
	}
	
	public void setReader(Reader r){
		this.configuration = r;
	}
	
	public void setSeed(long seed){
		g.setSeed(seed);
	}
	public SocialNetwork getSocialNetwork(){
		return this.sn;
	}
	
	public List<Integer> getContacts(int cid){
		return this.sn.getNeighborsOfNodeFromGS(cid);
	}
	
	public Customer [] getCustomers(){
		return this.customers;
	}
	
	public Customer getCustomer(int cid){
		return this.customers[cid];
	}
	
	public Product [] getProducts(){
		return this.products;
	}
	
	public Product getProduct(int pid){
		return this.products[pid];
	}
	
	public double getAlpha(){
		return this.alpha;
	}
	
	public double getB1(){
		return this.b1;
	}
	
	public double getB2(){
		return this.b2;
	}
	
	public double getBuyProb(){
		return this.buyprob;
	}
	
	public int getSteps(){
		return this.time;
	}
	
	public int getStationality() {
		return stationality;
	}

	public boolean getModelType(){
		return this.type;
	}
	
	public boolean getExtended(){
		return this.extended;
	}
	public boolean toOptimize(){
		return this.optimization;
	}
	public Reader getConfiguration(){
		return this.configuration;
	}
	
	/* ------------------------ Methods ---------------------- */
	
	public Statistics getStatistics() {
		return statistics;
	}

	public OptimizationParameters getOptimizationParameters() {
		return optimizationParameters;
	}

	public void setOptimizationParameters(OptimizationParameters optimizationParameters) {
		this.optimizationParameters = optimizationParameters;
	}

	public boolean isRandomSeedSelection() {
		return randomSeedSelection;
	}

	public void setRandomSeedSelection(boolean randomSeedSelection) {
		this.randomSeedSelection = randomSeedSelection;
	}

	private void setConsumptions(int [] consumptions){
		for(Customer c: this.getCustomers()){
			consumptions[c.getIdentifier()] = c.getPurchase();
		}
	}
	private int [] getConsumptions(){
		int [] consumptions = new int[this.getCustomers().length];
		for(Customer c: this.getCustomers()){
			consumptions[c.getIdentifier()] = c.getPurchase();
		}
		return consumptions;
	}
	private void setUpRandomGenerator(int seedId) {
		g = RandomizerFactory.createRandomizer(RandomizerAlgorithm.XOR_SHIFT_128_PLUS_FAST, SeedContainer.getSeed(seedId)); 
	}
	/**
	 * Initialization of the market model. 
	 * Agents preferences, product dimensions and visibility is initialized randomly.
	 * @param g Random number generator.
	 */
	public void setUpModel(){
		// Initializing customers and products
		// Products first as customer need to know how many are in the market.
		for(int p = 0; p < this.getProducts().length; p++){
			Product aux = new Product(Integer.toString(p));
			double pquality = g.nextDouble();
			aux.setQuality(pquality);
			aux.setVisibility(product_visibility);
			this.setProduct(p, aux);						
		}
		// Next, initialize customers.
		int purchase;
		for(int c = 0; c < this.getCustomers().length; c++){
			Customer aux = new Customer(c);
			aux.setContacts(this.getContacts(c));
			
			if(!this.toOptimize()){
				purchase = g.nextInt(this.getProducts().length);
			}
			else{
				purchase = g.nextInt(this.getProducts().length - 1);
			}

			aux.setPurchase(purchase);
			
			if (this.getModelType()){
				aux.setSocialPreference(g.nextDouble());
				aux.setUmin(g.nextDouble());
				aux.setUnct(g.nextDouble() * 0.5);
				aux.setAw(0.2 + (g.nextDouble() * (1.0 - 0.8)));
				aux.setDecay(0.0 + (g.nextDouble() * (0.5 - 0.0)));
			}
			else{
				aux.setSocialPreference(this.getConfiguration().getParameterDouble("Bi"));
				aux.setUmin(this.getConfiguration().getParameterDouble("Umin"));
				aux.setUnct(this.getConfiguration().getParameterDouble("Unct"));
				aux.setAw(this.getConfiguration().getParameterDouble("awareness_value"));
				aux.setDecay(this.getConfiguration().getParameterDouble("awareness_decay_value"));
			}
			// Setting values for product preferences and awareness (depending on the model type).
			aux.setPreferences(new double [this.products.length]);
			aux.setAwareness(new boolean [this.products.length]);
			double ppref = 0.0;
			for(int p = 0; p < this.getProducts().length; p++){
				ppref = g.nextDouble();
				aux.setPreference(p, ppref);
				// If we have a not awareness model, we set awareness of all the products of the market.
				if (!this.getExtended()){
					aux.setProductAwareness(p, true);
				}
					
				// If we have an awareness model, customers will be only aware of the product they are consuming.
				// This can be changed if does not work well.
				else{
					if (aux.getPurchase() == p){
						aux.setProductAwareness(p, true);
					}
					else {
						if(g.nextDouble() < 0.4){
						//aux.setProductAwareness(p, false);
							int prod = g.nextInt(this.products.length - 1);
							aux.setProductAwareness(prod, true);
						}
					}
				}
					
			}
			if (this.toOptimize()) {
				aux.setProductAwareness(this.getProducts().length - 1, false);
			}
				
			this.setCustomer(c, aux);
		}		
	}
	
	/* -------- Methods for calculate expected utility and uncertainty ------ */
	private double sameElection(int cid, int pid){
		// Depending on product visibility, the customer will explore different parts of its 
		// social network to determine the fraction of friends which are consuming the same 
		// product as he is consuming. The following cases are considered:
		// * If the product visibility is low, the customer will only look at those friends having 
		// similar preferences (values of pi), which we call close friends.
		// * If the product visibility is normal, will look at this network of friends to determine the 
		// social utility.
		// If the product visibility is high, products may generate social processes among agents who are
		// not even friends. Here, information is also obtained from friends of friends; thus a meta-network 
		// is being used that consists of the summed networks of all friends.
		
		int numcontacts = 0;
		int consumingthesame = 0;
		// First case: Low visibility.
		
		if (this.getProduct(pid).getVisibility().equals("low")){
			// We assume that a close friend is an agent with a social preference in a range between 
			// +- .1 from the customer social preferences.
			for(Integer c: this.getCustomer(cid).getContacts()){
				// If is a close friend.
				if (Math.abs(this.getCustomer(cid).getProductPreference(pid) - this.getCustomer(c).getProductPreference(pid)) <= 0.1)	
					// If is consuming the same product.
					numcontacts += 1;
					if (this.getCustomer(c).getPurchase() == pid) {
						consumingthesame += 1;
					}
			}
		}
		else if (this.getProduct(pid).getVisibility().equals("normal")){
			// We search customers that are friends of the current customer.
			numcontacts = this.getCustomer(cid).getContacts().length;
			for(int c  = 0; c < this.getCustomer(cid).getContacts().length; c++) {
				// If is consuming the same product.
				if (this.getCustomer(this.getCustomer(cid).getContactId(c)).getPurchase() == pid) {
					consumingthesame += 1;
				}
			}
		}
		else{
			// As result, we have a set with the friends of friend that compose the extended network
			// for the customer. Then we calculate the fraction of contacts consuming the same product as
			// the current customer.
			for(Integer c: this.getCustomer(cid).getContacts()){
				for(Integer f: this.getCustomer(c).getContacts()) {
					if (this.getCustomer(f).getPurchase() == pid && cid != f) {
						consumingthesame += 1;
					}
				}
			}
		}
		// Sometimes there exist customers (in the case of low visibility products) who don't have 
		// contacts with a similar value of social preference that were consuming the same so the return
		// value must be 0.
		if (numcontacts == 0)
			return 0.0;
		else
			return (double) consumingthesame / (double) numcontacts;
	}
	
	private double expectedUtility(int cid, int pid, double customerProductShare){
		return this.getAlpha() * (this.getCustomer(cid).getSocialPreference() * 
				(1.0 - Math.abs(this.getProduct(pid).getQuality() - this.getCustomer(cid).getProductPreference(pid))) 
						+ (1.0 - this.getCustomer(cid).getSocialPreference()) * customerProductShare);
		
	}
	
	private double expectedUncertainty(int cid, double customerProductShare){
		return (1.0 - this.getCustomer(cid).getSocialPreference()) * (1.0 - customerProductShare);
	}
	
	private void calculateutilsanduncts(double [] [] utils, double [] [] uncts){
		double customerProductShare;
		for(int cpos = 0; cpos < utils.length; cpos++){
			// calculation which number of contacts of the 
			for(int ppos = 0; ppos < utils[cpos].length; ppos++){
				customerProductShare = sameElection(cpos, ppos);
				utils [cpos] [ppos] = this.expectedUtility(cpos, ppos, customerProductShare);
				uncts [cpos] [ppos] = this.expectedUncertainty(cpos, customerProductShare);
			}
		}
	}
	/* --------- Decision heuristics -----------------*/
	
	private int deliberation(int cid, double [] expectedutilities){
		// The agent will evaluate the expected U i of each product, and will use a logit 
		// function to solve the discrete choice.
		// In the logit function the products acquire a probability Tp of being chosen.
		// This probability depends on the relative expected utility.
		// The products are weighted by the exponent of the parameter b 1 times the
		// expected utility. The higher the value of b 1 , the more sensitive is the decision
		// between the products to differences in their expected utility.
		double sumexputility = 0.0;
		
		// We select only the set of products the customer has awareness of. 
		for(int prod = 0; prod < expectedutilities.length; prod++){
			if(this.getCustomer(cid).getProductAwareness(prod)) {
				sumexputility += Math.pow(Math.E, this.getB1() * expectedutilities[prod]);
			}
		}
		// And we compose the random pie also from the products the customer is aware of. 
		boolean selected = false;
		int product = 0;
		double Tp = 0.0;
		double randval = g.nextDouble();; 
		
		while (!selected && product < this.getProducts().length){
			if (this.getCustomer(cid).getProductAwareness(product)) {
				Tp += Math.pow(Math.E, this.getB1() * expectedutilities[product]) / sumexputility;
				
				if (randval < Tp) {
					selected = true;
				}
				else {
					product++;
				}
			}
			else {
				product++;
			}
		}
		// Sometimes, an agent has information about a product but any of her/his contacts 
		// buy it or vice versa. In these cases, the agent can't take a decision about another 
		// product so (s)he needs to repeat her/his decision. 
		if (product == this.getProducts().length) {
			return this.getCustomer(cid).getPurchase();
		}
		else {
			return product;
		}
	}
	
	private int imitation(int cid){
		// The agent evaluates the products that are being consumed by his or her friends. 
		// The product with the largest share among the neighbors has a higher probability
		// of being chosen for current consumption.
		int [] friendsconsumptions = new int [this.getProducts().length];
		
		// Calculate the products being consumed by their friends (amount per product).
		for(Integer c: this.getCustomer(cid).getContacts()){
			friendsconsumptions[this.getCustomer(c).getPurchase()] += 1;
		}
		// Then, he have obtained the number of friends consuming each product.
		double sumprodamount = 0.0;
		for(int prod = 0; prod < friendsconsumptions.length; prod++){
			if(this.getCustomer(cid).getProductAwareness(prod)) {
				sumprodamount += Math.pow(Math.E, this.getB2() * (double)friendsconsumptions[prod]);
			}
		}
		// Finally, we choose the customer election.
		boolean selected = false;
		int product = 0;
		double Tp = 0.0;
		double randval = g.nextDouble();
		
		while (!selected && product < this.getProducts().length){
			if (this.getCustomer(cid).getProductAwareness(product)) {
				Tp += Math.pow(Math.E, this.getB2() * friendsconsumptions[product]) / sumprodamount;

				if (randval < Tp) {
					selected = true;
				}
				else {
					product++;
				}
			}
			else {
				product++;
			}
		}
		// Sometimes, an agent has information about a product but any of her/his contacts 
		// buy it or vice versa. In these cases, the agent can't take a decision about another 
		// product so (s)he needs to repeat her/his decision. 
		if (product == this.getProducts().length) {
			return this.getCustomer(cid).getPurchase();
		}
		else {
			return product;
		}
	}
	
	private int socialComparison(int cid, double [] expectedutilities){
		// The agent evaluates the products that are consumed by his or her friends. Using the
		// same logit function as in the case of deliberation, the agent makes a choice
		// between the expected satisfaction resulting from consuming the products that are
		// also consumed by their friends. Thus the socially comparing agent might consider
		// a smaller set of products than a deliberating agent.
		int [] friendsconsumptions = new int [this.getProducts().length];
		
		// Calculate the products being consumed by their friends (amount per product).
		for(Integer c: this.getCustomer(cid).getContacts()){
			friendsconsumptions[this.getCustomer(c).getPurchase()] += 1;
		}
		
		
		double sumexputility = 0.0;
		
		for(int prod = 0; prod < expectedutilities.length; prod++){
			if (friendsconsumptions[prod] > 0 && this.getCustomer(cid).getProductAwareness(prod)){
				sumexputility += Math.pow(Math.E, this.getB1() * expectedutilities[prod]);
			}
		}
		
		boolean selected = false;
		int product = 0;
		double Tp = 0.0;
		double randval = g.nextDouble();

		while (!selected && product < this.getProducts().length){
			if (friendsconsumptions[product] > 0 && this.getCustomer(cid).getProductAwareness(product)){
				Tp += Math.pow(Math.E, this.getB1() * expectedutilities[product]) / sumexputility;
				
				if (randval < Tp) {
					selected = true;
				}
				else {
					product++;
				}
			}
			else {
				product++;
			}
		}
		// Sometimes, an agent has information about a product but any of her/his contacts 
		// buy it or vice versa. In these cases, the agent can't take a decision about another 
		// product so (s)he needs to repeat her/his decision. 
		if (product == this.getProducts().length) {
			return this.getCustomer(cid).getPurchase();
		}
		else {
			return product;
		}
	}
	
	/* --------- Information exchange and consumption processes ---------- */
	private void consumptionProcess(double [] [] utils, double [] [] uncts, int step){
		
		int [] huse = new int[4];
		double conttruecustomers = 0;
		
		for(int c = 0; c < this.getCustomers().length; c++){
			if (this.getCustomer(c).isSeed()){
				huse[0] += 1;
			}
			else{
				if (g.nextDouble() < this.getBuyProb()){ // If the agent buys in this iteration.
					
					if (utils [this.getCustomer(c).getIdentifier()] [this.getCustomer(c).getPurchase()] >= this.getCustomer(c).getUmin() && 
							uncts [this.getCustomer(c).getIdentifier()] [this.getCustomer(c).getPurchase()] <= this.getCustomer(c).getUnct()){
						// Customer repeats his election.
						huse[0] += 1;
					}
					else if (utils [this.getCustomer(c).getIdentifier()] [this.getCustomer(c).getPurchase()] < this.getCustomer(c).getUmin() && 
							uncts [this.getCustomer(c).getIdentifier()] [this.getCustomer(c).getPurchase()] <= this.getCustomer(c).getUnct()){
						this.getCustomer(c).setPurchase(this.deliberation(this.getCustomer(c).getIdentifier(), utils [this.getCustomer(c).getIdentifier()]));
						huse[1] += 1;
					}
					else if (utils [this.getCustomer(c).getIdentifier()] [this.getCustomer(c).getPurchase()] >= this.getCustomer(c).getUmin() && 
							uncts [this.getCustomer(c).getIdentifier()] [this.getCustomer(c).getPurchase()] > this.getCustomer(c).getUnct()){
						this.getCustomer(c).setPurchase(this.imitation(this.getCustomer(c).getIdentifier()));
						huse[2] += 1;
					}
					else{
						this.getCustomer(c).setPurchase(this.socialComparison(this.getCustomer(c).getIdentifier(), utils [this.getCustomer(c).getIdentifier()]));
						huse[3] += 1;
					}
					conttruecustomers += 1.0;
				}
			}
		}
		this.statistics.setMeanCustomers(conttruecustomers / (double) this.getCustomers().length, step);
		this.statistics.setHeuristicsUse(huse, step);
	}
	
	public void speak(int coneid){
		Customer c = this.getCustomer(coneid);
		for(int p = 0; p < this.getProducts().length; p++){
			if(c.getProductAwareness(p)) { 
				if(g.nextDouble() < c.getAw()){
					for(int contact = 0; contact < c.getContacts().length; contact++) {
						if(!this.getCustomer(c.getContactId(contact)).getProductAwareness(p)) {
							this.getCustomer(c.getContactId(contact)).setProductAwareness(p, true);
						}
					}
				}
			}
		}
	}
	
	public void decay(int coneid) {
		for(int p = 0; p < this.getCustomer(coneid).getAwareness().length; p++){
			// If isn't the product being consumed by the customer
			if (p != this.getCustomer(coneid).getPurchase()) {
				if(this.getCustomer(coneid).getProductAwareness(p)) { 
					if(g.nextDouble() < this.getCustomer(coneid).getDecay()) {
						this.getCustomer(coneid).setProductAwareness(p, false);
					}
				}
			}
		}
	}
	
	public void wordOfMouth(){
		for(int customer = 0; customer < this.getCustomers().length; customer++) {
			Customer c = this.getCustomer(customer);
			if (c.isSeed()) {
				for(int contact = 0; contact < c.getContacts().length; contact++) {
					if(!this.getCustomer(c.getContactId(contact)).getProductAwareness(this.products.length - 1)) {
						this.getCustomer(c.getContactId(contact)).setProductAwareness(this.products.length - 1, true);
					}
				}
			}
			else {
				this.speak(c.getIdentifier());
			}
		}
	}
	
	public void decayStage(){
		for(int customer = 0; customer < this.getCustomers().length; customer++) {
			if (!this.getCustomer(customer).isSeed()) {
				this.decay(this.getCustomer(customer).getIdentifier());
			}
		}
	}
	
	/* -------------- Engine ---------------------- */
	/**------- Methods for viral marketing optimization ------------ */
	private int [] selectSeeds(){
		double w = 0.0;
        int popsize = sn.getNumNodes();
	
        Map<Integer, Double> seedQueue = new TreeMap<>();
        
        for(int node = 0; node < popsize; node++){
            w = (optimizationParameters.getDegreeWeight() * sn.getNetworkMetrics().getNormNodeDegMetric(node) +  
        		 optimizationParameters.getNeighborhoudWeight() * sn.getNetworkMetrics().getNormNodeTstepsMetric(node) +
        		 optimizationParameters.getClusteringCoefficientWeight() * sn.getNetworkMetrics().getNormNodeCcMetric(node));
 
            seedQueue.put(node, w);
        }
        // Descending order. 
		TreeMap<Integer, Double> sortedSeeds = new TreeMap<>(util.Util.sortByValue(seedQueue));
		int [] selected = new int [optimizationParameters.getNumTargets()];
		int cont = 0; 
		
		for (Map.Entry<Integer, Double> entrySet : sortedSeeds.entrySet()) {
		    if(cont < optimizationParameters.getNumTargets()){
		    	selected [cont] = (entrySet.getKey());
		        cont++;
		    }
		    else{
		        break;
		    }
		}
		// Removing unnecessary structures. 
		seedQueue = null;
		sortedSeeds = null; 
		
	    return selected;
	}
	
	/**------- Methods for viral marketing optimization ------------ */
	private int [] selectSeedsAtRandom(){
		int [] customers = new int [this.getCustomers().length];
		for(int i=0; i < this.getCustomers().length; i++) {
			customers[i] = i; 
		}
		customers = Util.RandomizeArray(customers);
		int [] selected = new int [optimizationParameters.getNumTargets()]; 
		System.arraycopy(customers, 0, selected, 0, optimizationParameters.getNumTargets());
		return selected;
	}
	private double [] NPV(int day, int [] lastElection){
		int numadopters = 0;
		int numseeds = 0;
		
		double [] npv; 
		int counter = 0;
		for(Customer c: this.getCustomers()){
			if(c.isSeed()){
				numseeds += 1;
			}
			else{
				if (lastElection[counter] != (this.getProducts().length - 1)){
					if(c.getPurchase() == (this.getProducts().length - 1)){
						numadopters += 1;
					}
				}
			}
			counter++;
		}	
		
		double benfs = (double) numadopters * Math.pow(0.9, (day));
		double costs = ((double) numseeds * (1.0/8.0)) * Math.pow(0.9, (day));
//		System.out.println("Day: " + day + " Adopters: " + numadopters + " Seeds: " + numseeds);
//		System.out.println("Products: " + this.getProducts().length);
		if (this.mo){
			npv = new double [2];
			npv[0] = benfs;
			npv[1] = costs;
		}
		else{
			npv = new double [1];
			npv[0] = benfs - costs;
		}
		return npv;
	}

	public void run(int monteCarloId){
		// Starting time measurement. 
		long startTime = System.currentTimeMillis();
		// Setting up random number generator using 
		// Monte-Carlo identifier to set the random generator
		// seed. 
		this.setUpRandomGenerator(monteCarloId);
		// Setting up model. 
		this.setUpModel();
		// Structure to store the indexes of the agents which 
		// are selected into targeting process. 
		int [] selectedSeeds; 
		// Targeting process: 
		// 1) Random (without using weights for local network metrics). 
		// 2) Sorting consumers using weight for network metrics and then targeting.  
		if(this.isRandomSeedSelection()) {
			selectedSeeds = selectSeedsAtRandom();
		}
		else {
			selectedSeeds = selectSeeds();
		}
		// If simulation is launched for a optimization, then initialize the seeds. 
		for (Integer seed: selectedSeeds){
			this.getCustomer(seed).setSeed();
			this.getCustomer(seed).setPurchase(this.getProducts().length - 1);
		}
		// Variables to store past and actual products selection. 
		int [] pastChoices = new int [this.getCustomers().length];
		int [] nextChoices = new int [this.getCustomers().length];
		/**
		 * Setting up Consumat model parameters such as utilities and uncertainties for consumer 
		 * selection. 
		 */
		double [] [] utilities = new double [this.getCustomers().length] [this.getProducts().length];
		double [] [] uncertainties = new double [this.getCustomers().length] [this.getProducts().length];
		/**
		 * Initialize utilities and uncertainties. 
		 */
		this.calculateutilsanduncts(utilities, uncertainties);
		// Variable to control which step the statistics need to be updated. 
		int freqStep = 0; 
		// Variable for NPV values
		double [] NPV = new double[2];
		/**
		 * Starting simulation process.
		 */
		for(int step = 0; step < this.getSteps(); step++){
			// Setting past choices
			this.setConsumptions(pastChoices);
			/**
			 * If the model is extended using word-of-mouth and awareness filters 
			 * then run diffusion processes.  
			 */
			if (this.getExtended()){
				this.wordOfMouth();
				this.decayStage();
			}
			/**
			 * Launch consumption progress when actual step fits 
			 * consumption frequency. 
			 */
			if ((step % stationality) == 0 && step!=0){
				this.consumptionProcess(utilities, uncertainties, freqStep);
				this.setConsumptions(nextChoices);
				/**
				 * Update consumer statistics. 
				 */
				updateStatistics(freqStep);	
				// Increase freqStep
				freqStep++;
				/**
				 * Calculate utilities and uncertainties. 
				 */
				this.calculateutilsanduncts(utilities, uncertainties);
			}
			// Calculating NPV for benefits and costs. 
			double [] values = this.NPV(step, pastChoices);
			NPV[0] += values[0];
			NPV[1] += values[1];
		}
		System.out.println("Benefits: " + NPV[0] + " -  Costs: " + NPV[1]);
		statistics.setOptimizationStatistics(new OptimizationStatistics(NPV[0], NPV[1]));
		statistics.setExecutionTime(System.currentTimeMillis() - startTime);
	}
	/**
	 * Updates consumption statistics in a given step. 
	 * @param step
	 */
	private void updateStatistics(int step) {
		statistics.setConsumerChoiceByStep(this.getCustomers(), step);
		statistics.setProductSelection(this.getCustomers(), step);
		statistics.setAwarenessRatio(getCustomers(), step);
		// Calculating Gini.
		statistics.setGini(StatisticsFunctions.calculateGini(this.getCustomers()), step);
	}
	/* ----------- Display Methods -------------------*/
	public void display(){
		System.out.println("| ---------- STRUCTURE INDICATORS ------------- |");
		System.out.println("| ---------- Market model simulation ------------- |");
		System.out.println("| -- This simulation uses a social network with the following structure and values --- |");
		this.getSocialNetwork().displayInformation();
		System.out.println("| -- In the market there are " + this.getCustomers().length + " customers --|");
		System.out.println("| -- ** Where " + this.statistics.getMeanCustomers() + " perform as customers --|");
		System.out.println("| -- There are " + this.getProducts().length + " products involved in the market --|");
		
		if (this.getExtended())
			System.out.println("| -- The model involves word of mouth and decay processes --|");
		else
			System.out.println("| -- The model does not involve word of mouth and decay processes --|");
		
		if (this.getConfiguration().getParameterBoolean("to_optimize"))
			System.out.println("| -- As we are searching for the best viral marketing campaign, product " + 
		Integer.toString(this.getProducts().length - 1) + " performs as the profuct to be optimized --|");
		else
			System.out.println("| -- Where are only simulating market dynamics -- |");
		System.out.println("| ---------- --------------- ------------- |");
		System.out.println("| ---------- EXECUTION INDICATORS ------------- |");
		System.out.println("| -- Total execution time per day of consumption: " + this.statistics.getExecutionTime() + " --|");
		System.out.println("| ---------- ----------------- ------------- |");
		System.out.println("| ---------- PERFORMANCE INDICATORS ------------- |");
		System.out.println("| -- After execution we obtain the following values for Gini coefficient and turbulence metrics: --|");
		System.out.println("| -- Gini: " + Double.toString(this.statistics.getAggregatedGiniCoefficient()) + " --|");
		System.out.println("| -- Turbulence: " + Double.toString(this.statistics.getAggregatedTurbulence()) + " --|");
		
		System.out.println("| -- Referring to products: --|");
		System.out.println("| -- The amount of sales after the simulation is: --|" + Arrays.toString(statistics.lastProductSelection()) + " --|");
		System.out.println("| -- The percentage of buys during the simulation is: --|" + Arrays.toString(statistics.getFinalBuyProbs()[this.getSteps()-1]) + " --|");
		System.out.println("| -- The percentage in the decission heuristic usege during "
				+ "the simulation is: --|" + Arrays.toString(statistics.getHeuristicsUse()) + " --|");
		System.out.println("| ---------- -------------------- ------------- |");
	}
}
