package util;

import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Util {
	/* ------------- Métodos para ordenación ----------------------*/
    /** 
     * Ordena un Map en forma descendente.
     * @param map Map de entrada. 
     * @return TreeMap ordenado de forma ascendente.
     */
    public static TreeMap<Integer, Double> sortByValue(Map<Integer, Double> map) {
        // Creamos el comparador por valor. 
        ValueComparator vc =  new ValueComparator(map);
        // Creamos el TreeMap resultado. 
        TreeMap<Integer, Double> sortedMap = new TreeMap<>(vc);
        // Introducimos todos los valores. 
        sortedMap.putAll(map); 
        return sortedMap;
    }
    
    public static int[] RandomizeArray(int a, int b){
		Random rgen = new Random();  // Random number generator		
		int size = b-a+1;
		int[] array = new int[size];
 
		for(int i=0; i< size; i++){
			array[i] = a+i;
		}
 
		for (int i=0; i<array.length; i++) {
		    int randomPosition = rgen.nextInt(array.length);
		    int temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
 
		for(int s: array)
			System.out.println(s);
 
		return array;
	}
    
    public static int[] RandomizeArray(int[] array){
		Random rgen = new Random();  // Random number generator			
 
		for (int i=0; i<array.length; i++) {
		    int randomPosition = rgen.nextInt(array.length);
		    int temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
 
		return array;
	}
}

/**
 * Clase destinada como comparador de Map por valor. 
 * @author Juan Francisco Robles Fuentes.
 */
class ValueComparator implements Comparator<Integer> {
    // Map a ordenar. 
    Map<Integer, Double> map;
    // Comparador de valores. 
    public ValueComparator(Map<Integer, Double> base) {
        this.map = base;
    }
    // Compara dos claves enteras por sus valores reales. 
    @Override
    public int compare(Integer a, Integer b) {
        // TODO Auto-generated method stub
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
