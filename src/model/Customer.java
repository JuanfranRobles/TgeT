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

import java.util.List;

/**
 * @author Juan Francisco Robles Fuentes.
 */

/**
 * Customer class.
 * 
 * This class represents a customer in a consumption environment and is defined with
 * the next set of parameters: 
 * 
 * Parameters:
 *   -> Identifier - Customer identifier (i.e., number of node in social network, 
 *   random name, etc).
 *   -> Purchase - The purchase of the customer.
 *   -> Contacts - People which is connected to the customer. This people could be understood
 *   as friends of the current customer as an example of connection between people.
 *   -> Preferences - The preference of the customer for the products involved in the market.
 *   The preference for a product p is expressed by a value between 0 and 1 where 0 is the 
 *   lowest preference and 1 the greatest.
 *   -> Personal/social preferences (social) - The preference of an agent to consider its opinions or
 *   guide them by the opinions of its friend.
 *   -> Umin: It is the utility threshold which guides if an agent feels that the 
 *   product is chosen continue satisfying him or not. Remains constant for all 
 *   the simulations.
 *   -> Unct: It is the uncertainty threshold which guides when the agents have
 *   uncertainty about its buys or not. Remains constant for all 
 *   the simulations.
 *   -> Awareness - The awareness of each product of the model. If the value for a product
 *   p is false, the agent does not have information about the product so he can not talk about
 *   it with other customers in the extended Consumat. In the other hand, if this value is true, 
 *   the customer can share its perceptions about the product with their friends.
 *    
 */

public class Customer {
	
	private int identifier;
	
	private int [] contacts;
	private int purchase;
	private double socialpreference;
	private double umin;
	private double unct;
	private double aw;
	private double awd;
	private double [] preferences;
	private boolean [] awareness;
	boolean seed = false;
	
	/* ----------------- Constructors -------------------- */
	
	public Customer(int id){
		this.identifier = id;
		this.purchase = -1;
		this.contacts = new int [0];
		this.preferences = null;
		this.awareness = null;
	}
	
	/* ---------------- Methods -------------------------- */
	
	public void setIdentifier(int id){
		this.identifier = id;
	}
	
	public void setPurchase(int pchs){
		this.purchase = pchs;
	}
	
	public void setContacts(List<Integer> c){
		this.contacts = new int [c.size()];
		for (int cont = 0; cont < c.size(); cont++) {
			contacts[cont] = c.get(cont);
		}
	}
	
	public void setSocialPreference(double sp){
		this.socialpreference = sp;
	}
	
	public void setUmin(double um){
		this.umin = um;
	}
	
	public void setUnct(double un){
		this.unct = un;
	}
	
	public void setAw(double awr){
		this.aw = awr;
	}
	
	public void setDecay(double awrd){
		this.awd = awrd;
	}
	
	public void setPreferences(double [] ps){
		this.preferences = new double [ps.length];
		System.arraycopy(ps, 0, this.preferences, 0, this.preferences.length);
	}
	
	public void setPreference(int pid, double pref){
		this.preferences[pid] = pref;
	}
	
	public void setAwareness(boolean [] aw){
		this.awareness = new boolean [aw.length];
		System.arraycopy(aw, 0, this.awareness, 0, this.awareness.length);
	}
	
	public void setProductAwareness(int pid, boolean aware){
		this.awareness [pid] = aware;
	}
	public void setSeed(){
		this.seed = true;
	}
	public int getIdentifier(){
		return this.identifier;
	}
	
	public int getPurchase(){
		return this.purchase;
	}
	
	public int [] getContacts(){
		return this.contacts;
	}
	
	public int getContactId(int pos) {
		return this.contacts[pos];
	}
	
	public double getSocialPreference(){
		return this.socialpreference;
	}
	
	public double getUmin(){
		return this.umin;
	}
	
	public double getUnct(){
		return this.unct;
	}
	
	public double getAw(){
		return this.aw;
	}
	
	public double getDecay(){
		return this.awd;
	}
	
	public double [] getPreferences(){
		return this.preferences;
	}
	
	public double getProductPreference(int pid){
		return this.preferences[pid];
	}
	
	public boolean [] getAwareness(){
		return this.awareness;
	}
	
	public boolean getProductAwareness(int pid){
		return this.awareness[pid];
	}
	public boolean isSeed(){
		return this.seed;
	}
	public void displayAwareness(){
		System.out.println("| The customer have awareness about products: ");
		int aux = 0;
		for(boolean aw: this.getAwareness()){
			if (aw)
				System.out.print(aux);
			aux++;
		}
	}
	
	public void display(){
		
		System.out.println("|.........................................|");
		System.out.println("| Customer " + Integer.toString(this.getIdentifier()) + " |");
		System.out.println("| Purchsing product " + this.getPurchase() + " |");
		System.out.println("| Connected to " + this.getContacts().length + " customers.");
		System.out.print("| ");
		for(Integer c: this.getContacts()){
			System.out.print(c + " ");
		}
		System.out.print(" |");
		System.out.println("| Its social preference is : " + this.getSocialPreference() + " |");
		System.out.print("| As this value is ");
		
		if(this.getSocialPreference() < 0.5)
			System.out.print("greater than 0.5. It means that the social needs are weighted more, "
					+ "as is usually the case with less innovative people");
		else if(this.getSocialPreference() > 0.5)
			System.out.print("less than 0.5, the personal customer need is weighted more, as is "
					+ "usually the case with more innovative people");
		else
			System.out.print(" equal to 0.5 so the customer weighted its social and personal needs "
					+ "as equal.");
		
		System.out.println("| Its preferences among products are |");
		int aux = 0;
		for(double p: this.getPreferences()){
			System.out.println("| Product " + aux + ": " + p + " |");
			aux++;
		}		
		System.out.println("|.........................................|");
	}
}
