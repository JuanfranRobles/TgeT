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

import model.Customer;

/**
 * @author Juan Francisco Robles Fuentes.
 */

/**
 * Indicators class.
 * 
 * This class serves to calculate some indicators associated to the market simulator: 
 * 
 * The following metrics set is defined:
 * 	 -> gini - Gini coefficient measures dominance of products on the market. The 
 *      higher this indicator, the more the market is dominated by one or a few products. 
 *      A value of zero means that all products have an equal market share.
 *   -> turbulence - is the average change of product choice during the simulation by all agents. 
 *      Thus turbulence 1.0 means that all agents change their choice each period, and 0.0 means 
 *      that no agent changes its choice after its initial choice.
 */
public class Indicator {
	
	private double executiontime = 0.0;
	private double gini = 0.0;
	private double turbulence = 0.0;
	private int [] finalbuys = new int [0];
	private double [] finalbuyprob = new double [0];
	private double [] heuristicsuse = new double [0];
	private double [] awarenessRatio = new double [0];
	private double meancustomers = 0.0;
	
	public void setExecutionTime(double t){
		this.executiontime = t;
	}
	
	public void setGini(double g){
		this.gini = g;
	}
	
	public void setTurbulence(double t){
		this.turbulence = t;
	}
	
	public void setFinaBuys(Customer [] cs){
		int [] buys = new int [cs[0].getPreferences().length];	
		this.finalbuys = new int [buys.length];
		
		for(Customer c: cs){
			buys[c.getPurchase()] += 1;
		}
		
		System.arraycopy(buys, 0, this.finalbuys, 0, buys.length);
	}
	
	public void setFinalBuyProb(Customer [] cs){
		double [] buyprobs = new double [cs[0].getPreferences().length];
		double totalsales = (double) cs.length;
		
		for(Customer c: cs){
			buyprobs[c.getPurchase()] += 1.0;
		}
		
		for(int v = 0; v < buyprobs.length; v++){
			buyprobs[v] /= totalsales;
		}

		if(this.finalbuyprob.length == 0){
			this.finalbuyprob = new double [buyprobs.length];
			System.arraycopy(buyprobs, 0, this.finalbuyprob, 0, buyprobs.length);
		}
		else{
			for(int v = 0; v < buyprobs.length; v++){
				this.finalbuyprob[v] = (this.finalbuyprob[v] + buyprobs[v]) / 2.0;
			}
		}
	}
	
	public void setHeuristicsUse(int [] hs){
		int total = 0;
		for(Integer v: hs){
			total += v;
		}
		double [] hperc = new double [hs.length];
		
		for(int v = 0; v < hperc.length; v++)
			hperc[v] = (double) hs[v] / (double) total;
		
		if(this.heuristicsuse.length == 0){
			this.heuristicsuse = new double [hperc.length];
			System.arraycopy(hperc, 0, this.heuristicsuse, 0, hperc.length);
		}
		else{
			for(int v = 0; v < hperc.length; v++)
				hperc[v] = (this.heuristicsuse[v] + hperc[v]) / 2.0;
		}
	}
	
	public void setAwarenessRatio(Customer [] cs){
		this.awarenessRatio = new double [cs[0].getPreferences().length];
		
		for(Customer c: cs){
			for(int prod = 0; prod < c.getAwareness().length; prod++){
				if(c.getProductAwareness(prod))
					this.awarenessRatio[prod] += 1.0;
			}
		}
		for(int val = 0; val < this.awarenessRatio.length; val++){
			this.awarenessRatio[val] /= (double) cs.length;
		}
	}
	public void setMeanCustomers(double cs){
		if (this.meancustomers == 0.0){
			this.meancustomers = (double) cs;
		}
		else{
			this.meancustomers = (this.meancustomers + cs) / 2.0;
		}
	}
	public double getExecutionTime(){
		return this.executiontime;
	}
	
	public double getGini(){
		return this.gini;
	}
	
	public double getTurbulence(){
		return this.turbulence;
	}
	
	public int [] getFinalBuys(){
		return this.finalbuys;
	}
	
	public double [] getFinalBuyProbs(){
		return this.finalbuyprob;
	}
	
	public double [] getHeuristicsUse(){
		return this.heuristicsuse;
	}
	public double [] getAwarenessRatio(){
		return this.awarenessRatio;
	}
	public double getMeanCustomers(){
		return this.meancustomers;
	}
	
	public double calculateGini(Customer [] customers){
		// Calculate the products being consumed. 
		int [] products = new int [customers[0].getPreferences().length];
		int total = 0; // If in the future we need to deal with customers that 
					   // not consume we need to count the consumed products one by one.
		for(Customer c: customers){
			products[c.getPurchase()] += 1;
			total += 1; 
		}
		double gval = 0.0;
		for(int po = 0; po < products.length; po++){
			for(int pt = po+1; pt < products.length; pt++){
				gval += (Math.abs(products[po] - products[pt])) / ((double)products.length * (double)total);
			}
		}
		return gval;
	}
	
	public double calculateTurbulence(int [] pchoices, int [] nchoices){
		int diffchoices = 0;
		for(int c = 0; c < pchoices.length; c++){
			if((pchoices[c] - nchoices[c]) != 0)
				diffchoices += 1;
		}
		return (double)diffchoices / (double)pchoices.length;
	}
	
}
