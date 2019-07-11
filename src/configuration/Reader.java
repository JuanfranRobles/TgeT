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

package configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Juan Francisco Robles Fuentes.
 */

/**
 * Configuration reader class.
 * This class reads the configuration files which initialize the set of parameters
 * necessary to create a market model based on ABMs. 
 * 
 * Attributes:
 *   -> Path to route which contains the paremetes for AMB.
 *   -> Properties object to load the parameters given in file name in a HashMap structure. 
 *   
 */

public class Reader {

    private final Properties parameters;  
    private static String file_name;
	
    /* ----------------------------- Constructors ------------------------------------- */
    /**
     * Initializes a configuration file from file path. 
     * @param path File path.
     */
    public Reader(String path) {
        parameters = new Properties();
        file_name = path;
        
        // Read configuration file.
        this.readConfigFile();
    }
    
    /* -------------------- Getters -----------------------------*/
    /**
     * Return the value of a parameter giving its name. 
     * I.e., name = "parameter").
     *         |        |
     *      Parameter   Value
     *        Name 
     */
    
    public String getParameterString(String ParameterName) {
        return parameters.getProperty(ParameterName); 
    }
	
   
    public int getParameterInteger(String ParameterName) {
    	return Integer.parseInt(parameters.getProperty(ParameterName));
    }
    
    public long getParameterLong(String ParameterName) {
    	return Long.parseLong(parameters.getProperty(ParameterName));
    }
        
    
    public boolean getParameterBoolean (String ParameterName) {
    	return Boolean.parseBoolean(parameters.getProperty(ParameterName));
    }
	
    public double getParameterDouble(String ParameterName) {
    	return Double.parseDouble(parameters.getProperty(ParameterName));
    }
	
    public float getParameterFloat(String ParameterName) {
    	return Float.parseFloat(parameters.getProperty(ParameterName));
    }
    
    public double[] getParameterDoubleArray(String ParameterName) {
    	String[] tmpStr;
    	double[] tmpDouble;
		
		tmpStr = parameters.getProperty(ParameterName).split(",");
		tmpDouble = new double[tmpStr.length];
	
		for(int i=0; i<tmpStr.length; i++) {
			tmpDouble[i] = Double.parseDouble(tmpStr[i]);
		}
		
		return tmpDouble;
    }
    
    public String[] getParameterStringArray(String ParameterName) {
        String[] tmpStr;		
    
        tmpStr = parameters.getProperty(ParameterName).split(",");
        return tmpStr;
    }
    
    public double[][] getParameterDoubleArrayTwoDim(String ParameterName) {
		String[] tmpStr;
		String[][] tmpStrTwoDim;
		double[][] tmpDoubleTwoDim;		
		int count;
		
	
		tmpStr = parameters.getProperty(ParameterName).split(";");
		count = org.apache.commons.lang3.StringUtils.countMatches(tmpStr[0], ",");
		count++;
		tmpStrTwoDim = new String[tmpStr.length][count];
		tmpDoubleTwoDim = new double[tmpStr.length][count];
		
		for(int i=0; i<tmpStr.length; i++) {
	            tmpStrTwoDim[i] = tmpStr[i].split(",");
		}
		 
		for(int i=0; i<tmpStr.length; i++) {
            for(int j=0; j<count; j++) {
            	tmpDoubleTwoDim[i][j] = Double.parseDouble(tmpStrTwoDim[i][j]);
            }
		}
		
		return tmpDoubleTwoDim;
    }
    
    public String[][] getParameterStringArrayTwoDim(String ParameterName) {
		String[] tmpStr;
		String[][] tmpStrTwoDim;
		int count;
		
		tmpStr = parameters.getProperty(ParameterName).split(";");
		count = org.apache.commons.lang3.StringUtils.countMatches(tmpStr[0], ",");
		count++;
		tmpStrTwoDim = new String[tmpStr.length][count];
		
		for(int i=0; i<tmpStr.length; i++) {
            tmpStrTwoDim[i] = tmpStr[i].split(",");
		}
		
		return tmpStrTwoDim;
    }
    
    public double[][][] getParameterDoubleArrayThreeDim(String ParameterName) {
		String[] tmpStr;
		String[][] tmpStrTwoDim;
		String[][][] tmpStrThreeDim;		
		double[][][] tmpDoubleThreeDim;		
		int count1;
		int count2;		
		 
		tmpStr = parameters.getProperty(ParameterName).split(":");
		count1 = org.apache.commons.lang3.StringUtils.countMatches(tmpStr[0], ";");
		count1++;
		tmpStrTwoDim = new String[tmpStr.length][count1];
		
        for(int i=0; i<tmpStr.length; i++) {
            tmpStrTwoDim[i] = tmpStr[i].split(";");
        }
		
		count2 = org.apache.commons.lang3.StringUtils.countMatches(tmpStrTwoDim[0][0], ",");
		count2++;
			
		tmpStrThreeDim = new String[tmpStr.length][count1][count2];
		tmpDoubleThreeDim = new double[tmpStr.length][count1][count2];

        for(int i=0; i<tmpStr.length; i++) {
            for(int j=0; j<count1; j++) {
            	tmpStrThreeDim[i][j] = tmpStrTwoDim[i][j].split(",");
            }
        }
    
		for(int i=0; i<tmpStr.length; i++) {
	        for(int j=0; j<count1; j++) {
	        	for(int k=0; k<count2; k++) {
	                tmpDoubleThreeDim[i][j][k] = Double.parseDouble(tmpStrThreeDim[i][j][k]);					
	            }
	        }
		}
		
		return tmpDoubleThreeDim;
    }
    
    /* --------------------Class Methods ---------------------------- */
    
    public void readConfigFile() {
        
    	InputStream Input = null;	
		try {
	            Input = new FileInputStream(file_name);
	            parameters.load(Input);
	
	        } 
	        catch (IOException ex) {
	        } 
        	finally {
             
	            if (Input != null) {
	            	try {
	                    Input.close();
	            	} 
	            	catch (IOException e) {
	            	}
	            }
        	}
	}	
}