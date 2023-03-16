package la4am12.ga;

/**
 * @author : LA4AM12
 * @create : 2023-03-12 18:11:52
 * @description : genetic algorithm
 */

import la4am12.woa.OptFunction;

import java.util.*;

public class GeneticAlgorithm {
	private OptFunction optFunction;
	private int boundary;
	private int population;
	private int genesN, maxGenerations;
	private double crossoverRate;
	private double mutationRate;
	private int tournamentSize;
	private static final Random random = new Random();
	private List<Chromosome> Chromosomes;
	Chromosome bestChromosome = null;

	public GeneticAlgorithm(OptFunction optFunction, int population, double crossoverRate, double mutationRate, int boundary, int genesN, int tournamentSize, int maxGenerations) {
		this.optFunction = optFunction;
		this.population = population;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
		this.boundary = boundary;
		this.genesN = genesN;
		this.maxGenerations = maxGenerations;
		this.tournamentSize = tournamentSize;
	}

	public void initializePopulation() {
		Chromosomes = new ArrayList<>();
		for (int i = 0; i < population; i++) {
			int[] genes = new int[genesN];
			for (int j = 0; j < genesN; j++) {
				genes[j] = random.nextInt(boundary);
			}
			Chromosomes.add(new Chromosome(genes));
		}
	}

	public void evaluatePopulation() {
		for (Chromosome chromosome : Chromosomes) {
			double fitness = optFunction.calc(chromosome.getGenes());
			chromosome.setFitness(fitness);
		}
		bestChromosome = Collections.min(Chromosomes);
		// System.out.println(bestChromosome);
	}

	public void evolvePopulation() {
		List<Chromosome> newPopulation = new ArrayList<>();
		for (int i = 0; i < population; i++) {
			Chromosome parent1 = tournamentSelection();
			Chromosome parent2 = tournamentSelection();
			Chromosome offspring = crossover(parent1, parent2);
			mutate(offspring);
			newPopulation.add(offspring);
		}
		Chromosomes = newPopulation;
	}

	// 从种群中选择一个染色体进行锦标赛选择
	public Chromosome tournamentSelection() {
		List<Chromosome> tournament = new ArrayList<>();
		for (int i = 0; i < tournamentSize; i++) {
			int randomIndex = random.nextInt(population);
			tournament.add(Chromosomes.get(randomIndex));
		}
		return Collections.min(tournament);
	}

	// 对两个染色体进行交叉，生成一个新的染色体
	public Chromosome crossover(Chromosome parent1, Chromosome parent2) {
		int[] genes1 = parent1.getGenes();
		int[] genes2 = parent2.getGenes();
		int[] offspringGenes = new int[genesN];
		for (int i = 0; i < genesN; i++) {
			if (random.nextDouble() < crossoverRate) {
				offspringGenes[i] = genes1[i];
			} else {
				offspringGenes[i] = genes2[i];
			}
		}
		return new Chromosome(offspringGenes);
	}

	// 对染色体进行变异
	public void mutate(Chromosome chromosome) {
		int[] genes = chromosome.getGenes();
		for (int i = 0; i < genesN; i++) {
			if (random.nextDouble() < mutationRate) {
				genes[i] = random.nextInt(boundary);
			}
		}
		chromosome.setGenes(genes);
	}

	// 执行遗传算法
	public int[] run() {
		initializePopulation();
		evaluatePopulation();
		for (int i = 0; i < maxGenerations; i++) {
			evolvePopulation();
			evaluatePopulation();
		}
		return bestChromosome.getGenes();
	}

	// 染色体类
	private class Chromosome implements Comparable<Chromosome> {
		private int[] genes;
		private double fitness;

		public Chromosome(int[] genes) {
			this.genes = genes;
		}

		public int[] getGenes() {
			return genes;
		}

		public void setGenes(int[] genes) {
			this.genes = genes;
		}

		public double getFitness() {
			return fitness;
		}

		public void setFitness(double fitness) {
			this.fitness = fitness;
		}

		@Override
		public int compareTo(Chromosome other) {
			return Double.compare(fitness, other.fitness);
		}

		@Override
		public String toString() {
			return "fitness:" + fitness + "genes:" + Arrays.toString(genes);
		}
	}
}

