package optimization;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import configuration.Reader;
import simulator.Simulator;
import socialnetwork.SocialNetwork;

public class MOTargetingProblem extends AbstractDoubleProblem{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Simulator simulator;
	
	public MOTargetingProblem(){
		this(4, "MOTargetingViralMarketing");
	}
	
	public MOTargetingProblem(Integer numberOfVariables, String paramsfile) {
		setNumberOfVariables(numberOfVariables);
	    setNumberOfObjectives(2);
	    setName("MOTargetingViralMarketing");
	    
	    Reader reader = new Reader(paramsfile);
	    
	    this.simulator = new Simulator(paramsfile);
	    int nodes = new SocialNetwork(reader.getParameterString("network_path")).getNumNodes();
		double maxTargets = (double) nodes * reader.getParameterDouble("targets_ratio");
		
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
		simulator.setOptParameters(ws);
		simulator.simulateModel();
		
		solution.setObjective(0, -1.0 * simulator.getBenefits());
		
		solution.setObjective(1, simulator.getCosts());
		
	}

}
