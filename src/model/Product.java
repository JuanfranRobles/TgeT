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

/**
 * @author Juan Francisco Robles Fuentes.
 */

/**
 * Product class.
 * 
 * This class represents a product in a consumption environment and is defined with
 * the next set of parameters: 
 * 
 * -> Name - Product name.
 * -> Visibility - This attribute represents the way a customer interacts with its 
 *   environment (friends) to determine the social utility for the product. This attribute
 *   works as follows:
 *   - For products of normal (medium) visibility the agent will look at this network of 
 *   friends to determine the social utility. 
 *   - For less visible products the agent will only look at those agents having similar 
 *   preferences (values of pi), which we call close friends. Here the agent will use a 
 *   subset of its network of friends. 
 *   - Highly visible products may generate social processes among agents who are not even
 *    friends. Here information is also obtained from friends of friends; thus a meta-network 
 *    is being used that consists of the summed networks of all friends.
 *   -> Quality - Reflects the quality of a product. It is a combination of tangible (price) 
 *   and intangible (beauty) characteristics. 
 *   
 */

public class Product {
	
	private String name;
	private String visibility; 
	private double quality;
	
	/* ------------- Constructors ------------------*/
	public Product(String pn){
		this.name = pn;
	}
	
	public Product(String pn, String pv, double pq){
		this.name = pn;
		this.visibility = pv;
		this.quality = pq;
	}
	
	/* ------------ Methods ----------------------- */
	
	public void setName(String pn){
		this.name = pn;
	}
	
	public void setVisibility(int pv){
		if (pv == 0)
			this.visibility = "low";
		else if (pv == 1)
			this.visibility = "normal";
		else
			this.visibility = "high";
	}
	
	public void setQuality(double pq){
		this.quality = pq;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getVisibility(){
		return this.visibility;
	}
	
	public double getQuality(){
		return this.quality;
	}
	
	public void display(){
		System.out.println("| Product " + this.getName() + " |");
		System.out.println("| Visibility " + this.getVisibility() + " |");
		System.out.println("| Quality " + this.getQuality() + " |");
	}
}
