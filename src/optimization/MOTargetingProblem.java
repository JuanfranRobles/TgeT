package optimization;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import configuration.Reader;
import model.Market;
import socialnetwork.NetworkMetrics;

public class MOTargetingProblem extends AbstractDoubleProblem{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Market market;
	
	public MOTargetingProblem(){
		this(4, "MOTargetingViralMarketing");
	}
	
	public MOTargetingProblem(Integer numberOfVariables, String paramsfile) {
		setNumberOfVariables(numberOfVariables);
	    setNumberOfObjectives(2);
	    setName("MOTargetingViralMarketing");
	    
	    Reader reader = new Reader(paramsfile);
	    
	    this.market = new Market(paramsfile);
		
		double maxTargets = (double) this.market.getSocialNetwork().getNumNodes() * reader.getParameterDouble("targets_ratio");
		
		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
	    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;
	    
		for (int i = 0; i < getNumberOfVariables() - 1; i++) {
	      lowerLimit.add(0.0);
	      upperLimit.add(1.0);
	    }
		
		lowerLimit.add(1.0);
	    upperLimit.add(maxTargets);
		
	    setLowerLimit(lowerLimit);
	    setUpperLimit(upperLimit);
	    
	}
	@Override
	public void evaluate(DoubleSolution solution) {

		double wd = solution.getVariableValue(0);
		double w2s = solution.getVariableValue(1);
		double wcc = solution.getVariableValue(2);
		double fs = solution.getVariableValue(3);
		
		//System.out.println("[ " + Double.toString(wd) + " -|- " + Double.toString(w2s) + 
			//	" -|- " + Double.toString(wcc) + " -|- " + Integer.toString((int) fs) + " ]");
		
		double [] ws = new double [] {wd, w2s, wcc, fs};
		
		double [] NPV = market.run(ws);
		
		solution.setObjective(0, -1.0 * NPV[0]);
		
		solution.setObjective(1, NPV[1]);
		
	}

}
