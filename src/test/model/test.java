package test.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import model.Market;
import socialnetwork.NetworkMetrics;
import socialnetwork.SocialNetwork;

public class test {
	public static void main(String args[]){
		/*
		Market market = new Market("/home/jfrobles/Documentos/VMOptimization/ConfigFiles/config_scale_free.properties");
		market.run();
		market.display();
		*/
		
		/*String nwpath = "/home/jfrobles/Documentos/VMOptimization/SNs/SF-100nodes.gexf";
		String rnwpath = "/home/jfrobles/Documentos/VMOptimization/SNs/ER-1024nodes.gexf";
		
		SocialNetwork sn = new SocialNetwork(nwpath);
		sn.displayInformation();
		NetworkMetrics nm = new NetworkMetrics(sn);
		nm.display();
		*/
		Market market = new Market("./configuration_files/config_animal_jam.properties");
		//market.getSocialNetwork().getNetworkMetrics().display();
		double [] nw = new double [] {0.5, 0.0, 0.0, 1000.0};
		double [] nwt = new double [] {0.6, 0.6, 0.4, 0.3, 0.4, 0.3, 50.0};
		double [] npv = market.run(nw);
		
		/*TreeMap<String, Integer> map = new TreeMap<String, Integer>();
        map.put("a", 10);
        map.put("b", 30);
        map.put("c", 50);
        map.put("d", 40);
        map.put("e", 100);
        map.put("f", 60);
        map.put("g", 110);
        map.put("h", 50);
        map.put("i", 90);
        map.put("k", 70);
        map.put("L", 80);
        
        map.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
        //.limit(10) 
        //.forEach(System.out::println); 
        .filter(entry -> entry.getKey() == 1);*/
        
	}
}
