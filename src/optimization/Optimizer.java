package optimization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder.Variant;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimizationBuilder;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder.GeneticAlgorithmVariant;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.SimpleRandomMutation;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.operator.impl.selection.TournamentSelection;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import configuration.Reader;
import model.Market;
import socialnetwork.NetworkMetrics;
import util.SeedContainer;

public class Optimizer {
	// Benchmark Parameters for EAs y MOEAs.
	private static int numEvaluations; 
	private static int populationSize; 
	private static String optimizationMode;
	private static double crossoverProb;
	// Parameters for parallelization.
	private static int numCores; // Number of cores to use during the experiments. 
	private static int numRuns; // Number of independent runs
	
	public static void main(String [] args) throws IOException{
		
		/*Reader r = new Reader("/home/jfrobles/Documentos/VMOptimization/ConfigFiles/config_scale_free.properties");
		Market m = new Market("/home/jfrobles/Documentos/VMOptimization/ConfigFiles/config_scale_free.properties");
		NetworkMetrics nm = new NetworkMetrics(m.getSocialNetwork());
		
		System.out.println(Arrays.toString(m.run(1, new double [] {0.5,  0.5, 0.5, 0.5, 100.0}, nm)));*/
		if(args.length != 2){
			throw new JMetalException("Needed arguments: benchmarkParameters experimentBaseDirectory");
		}
		
		String benchmarkParameters = args[0];
		
		
		Reader parameterReader = new Reader(benchmarkParameters); // Loading parameters from file.
		
		
		JMetalRandom.getInstance().setSeed(SeedContainer.getSeed(0));
		
		numEvaluations = parameterReader.getParameterInteger("evaluations");
		populationSize = parameterReader.getParameterInteger("population_size");
		optimizationMode = parameterReader.getParameterString("optimization_mode");
		
		crossoverProb =  parameterReader.getParameterDouble("cossover_prob");
		numCores = parameterReader.getParameterInteger("num_cores");
		numRuns = parameterReader.getParameterInteger("num_runs");
		
		String experimentBaseDirectory = args[1];
		
			
		// Preparing algorithms.
		// Single Evolutionary Algorithms Benchmark.
		if (optimizationMode.equals("EA")){
			
			// Adding experiments.
			List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
			problemList.add(new ExperimentProblem<>(new TargetingProblem(4, benchmarkParameters)));
			
			//System.out.println("Benchmarking Single Optimization algorithms over Targeting in Viral Marketing Problem");
			List<ExperimentAlgorithm<DoubleSolution, DoubleSolution>> algorithmList =
		            configureEAlgorithmList(problemList);
			
			Experiment<DoubleSolution, DoubleSolution> experiment =
			        new ExperimentBuilder<DoubleSolution, DoubleSolution>("TargetingViralMarketing")
			            .setAlgorithmList(algorithmList)
			            .setProblemList(problemList)
			            .setExperimentBaseDirectory(experimentBaseDirectory)
			            .setOutputParetoFrontFileName("FUN")
			            .setOutputParetoSetFileName("VAR")
			            .setReferenceFrontDirectory(experimentBaseDirectory+"/referenceFronts")
			            .setIndicatorList(Arrays.asList(
			                new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
			                new PISAHypervolume<DoubleSolution>(),
			                new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()))
			            .setNumberOfCores(numCores)
			            .setIndependentRuns(numRuns)
			            .build();
				
		    new ExecuteAlgorithms<>(experiment).run();
		    new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
		    new ComputeQualityIndicators<>(experiment).run() ;
		    new GenerateLatexTablesWithStatistics(experiment).run() ;
		    //new GenerateWilcoxonTestTablesWithR<>(experiment).run() ;
		    new GenerateFriedmanTestTables<>(experiment).run();
		    //new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run() ;
		}
		// Multiobjective Evolutionary Algorithms Benchmark.
		else {
			
			// Adding experiments.
			List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
			problemList.add(new ExperimentProblem<>(new MOTargetingProblem(4, benchmarkParameters)));
			
			System.out.println("Benchmarking Multiple Optimization algorithms over Targeting in Viral Marketing Problem");
			List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
		            configureMOEAlgorithmList(problemList);
	
		    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
		        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("MOTargetingViralMarketing")
		            .setAlgorithmList(algorithmList)
		            .setProblemList(problemList)
		            .setExperimentBaseDirectory(experimentBaseDirectory)
		            .setOutputParetoFrontFileName("FUN")
		            .setOutputParetoSetFileName("VAR")
		            .setReferenceFrontDirectory(experimentBaseDirectory+"/referenceFronts")
		            .setIndicatorList(Arrays.asList(
		                new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
		                new PISAHypervolume<DoubleSolution>(),
		                new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()))
		            .setNumberOfCores(numCores)
		            .setIndependentRuns(numRuns)
		            .build();
	
		    new ExecuteAlgorithms<>(experiment).run();
		    new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
		    new ComputeQualityIndicators<>(experiment).run() ;
		    new GenerateLatexTablesWithStatistics(experiment).run() ;
		    new GenerateWilcoxonTestTablesWithR<>(experiment).run() ;
		    new GenerateFriedmanTestTables<>(experiment).run();
		    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run() ;
		}
	}
	
	/**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
   * {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   *
   * @param problemList
   * @return
   */
	static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureMOEAlgorithmList(
          List<ExperimentProblem<DoubleSolution>> problemList) {
	  
		List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
		/*// SMPSO.
		for (int i = 0; i < problemList.size(); i++){
			double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
			double mutationDistributionIndex = 20.0;
    
			Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder((MOTargetingProblem) problemList.get(i).getProblem(), 
					new CrowdingDistanceArchive<DoubleSolution>(100))
					.setMutation(new PolynomialMutation(mutationProbability, mutationDistributionIndex))
					.setMaxIterations(numEvaluations)
					.setSwarmSize(100)
					.setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
					.build();
			algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
		}*/
		// NSGA-II.
		for (int i = 0; i < problemList.size(); i++) {
			Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problemList.get(i).getProblem(), 
					new SBXCrossover(1.0, 20.0), 
					new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
					.setMaxEvaluations(numEvaluations)
					.build();
			algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
		}
		/*// SPEA2
		for (int i = 0; i < problemList.size(); i++) {
			Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(problemList.get(i).getProblem(), 
					new SBXCrossover(1.0, 10.0),
					new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
					.setMaxIterations(numEvaluations)
					.build();
			algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
		}*/
		// MOEA/D.
		for (int i = 0; i < problemList.size(); i++) {
			Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i).getProblem(), Variant.MOEAD)
					.setMaxEvaluations(numEvaluations)
					.build();
			algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
		}
		/*// Coral Reefs Optimization.
		for (int i = 0; i < problemList.size(); i++){
			double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
		  
			Algorithm<List<DoubleSolution>> algorithm = new CoralReefsOptimizationBuilder<DoubleSolution>(
					(MOTargetingProblem) problemList.get(i).getProblem(),
					new TournamentSelection<DoubleSolution>(3),
					new SBXCrossover(crossoverProb, 20.0), 
					new SimpleRandomMutation(mutationProbability))
					.setMaxEvaluations(numEvaluations)
					.build();
			  	
			algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
		}*/
		return algorithms ;
	}
	
	/**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
   * {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   *
   * @param problemList
   * @return
   */
	static List<ExperimentAlgorithm<DoubleSolution, DoubleSolution>> configureEAlgorithmList(
			List<ExperimentProblem<DoubleSolution>> problemList) {
		
	  List<ExperimentAlgorithm<DoubleSolution, DoubleSolution>> algorithms = new ArrayList<>();
	  
	  // Genetic Algorithm with generational approach.
	  for (int i = 0; i < problemList.size(); i++){
		  double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
	    
		  Algorithm<DoubleSolution> algorithm = new GeneticAlgorithmBuilder<DoubleSolution>(
				  (TargetingProblem) problemList.get(i).getProblem(),
				  new SBXCrossover(1.0, 20.0), 
				  new SimpleRandomMutation(mutationProbability))
				  .setSelectionOperator(new TournamentSelection<DoubleSolution>(3))
				  .setMaxEvaluations(numEvaluations)
				  .setPopulationSize(populationSize)
				  .setVariant(GeneticAlgorithmVariant.GENERATIONAL)
				  .build();
		  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
	  }
	  // Genetic algorithm with stationary approach.
	  for (int i = 0; i < problemList.size(); i++){
		  double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
	    
		  Algorithm<DoubleSolution> algorithm = new GeneticAlgorithmBuilder<DoubleSolution>(
				  (TargetingProblem) problemList.get(i).getProblem(),
				  new SBXCrossover(1.0, 20.0), 
				  new SimpleRandomMutation(mutationProbability))
				  .setSelectionOperator(new TournamentSelection<DoubleSolution>(3))
				  .setMaxEvaluations(numEvaluations)
				  .setPopulationSize(populationSize)
				  .setVariant(GeneticAlgorithmVariant.STEADY_STATE)
				  .build();
		  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
	  }
	  // Differential evolution.
	  for (int i = 0; i < problemList.size(); i++){
	    
		  Algorithm<DoubleSolution> algorithm = new DifferentialEvolutionBuilder(
				  (TargetingProblem) problemList.get(i).getProblem())
				  .setPopulationSize(populationSize)
				  .setMaxEvaluations(numEvaluations)
				  .setCrossover(new DifferentialEvolutionCrossover())
				  .setSelection(new DifferentialEvolutionSelection())
				  .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
				  .build();
		  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
	  }
	  // CMAES.
	  for (int i = 0; i < problemList.size(); i++){
		  
		  Algorithm<DoubleSolution> algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(
						  (TargetingProblem) problemList.get(i).getProblem())
				  .setMaxEvaluations(numEvaluations)
				  .build();
				  
		  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
	  }
	  return algorithms ;
	}
}
