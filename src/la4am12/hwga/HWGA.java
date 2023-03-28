package la4am12.hwga;

import la4am12.datacenter.OptFunction;

import java.util.*;

/**
 * @author : LA4AM12
 * @create : 2023-03-28 11:24:04
 * @description : hybrid whale genetic algorithm
 */
public class HWGA {
	private class Whale implements Comparable<Whale> {
		private double[] position;
		private double fitness;

		@Override
		public int compareTo(Whale o) {
			return Double.compare(fitness, o.fitness);
		}

		public Whale(double[] position) {
			setPosition(position);
		}

		public void setFitness(double fitness) {
			this.fitness = fitness;
		}

		public void setPosition(double[] position) {
			this.position = position;
			adjustPosition();
			this.fitness = optFunction.calc(Arrays.stream(position).mapToInt((x) -> (int) x).toArray());
		}

		public double[] getPosition() {
			return position;
		}

		public void adjustPosition() {
			for (int i = 0; i < position.length; i++) {
				position[i] = Math.round(position[i]);
				if (position[i] < 0) {
					position[i] = 0;
				} else if (position[i] > upperBound) {
					position[i] = upperBound;
				}
			}
		}
	}

	private OptFunction optFunction;
	private int upperBound;
	private int population;
	private int dim, maxIter;
	private List<Whale> whales;
	private double[] bestSol;
	private double optimalScore;
	private double tournamentRatio;
	private double crossoverRate;
	private double mutationRate;
	private double phaseOutRatio;
	private static final Random R = new Random();

	public HWGA(OptFunction optFunction,
				int population, int upperBound,
				int dim,
				int maxIter,
				double tournamentRatio,
				double crossoverRate,
				double mutationRate,
				double phaseOutRatio) {
		this.optFunction = optFunction;
		this.population = population;
		this.upperBound = upperBound;
		this.dim = dim;
		this.maxIter = maxIter;
		this.bestSol = new double[dim];
		this.optimalScore = Double.MAX_VALUE;
		this.tournamentRatio = tournamentRatio;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
		this.phaseOutRatio = phaseOutRatio;
		this.whales = new ArrayList<>();
	}

	public void initializePopulation() {
		for (int i = 0; i < population; i++) {
			double[] position = new double[dim];
			for (int j = 0; j < dim; j++) {
				position[j] = upperBound * R.nextDouble();
			}
			whales.add(new Whale(position));
		}
	}

	public void evaluatePopulation() {
		Collections.sort(whales);
		Whale best = whales.get(0);
		if (best.fitness < optimalScore) {
			optimalScore = best.fitness;
			System.arraycopy(best.position, 0, bestSol, 0, dim);
		}
	}

	private void updatePosition(double a, double a2) {
		for (Whale w : whales) {
			double r1 = R.nextDouble();
			double r2 = R.nextDouble();
			double A = 2.0 * a * r1 - a;                            // Eq. (2.3) in the paper
			double C = 2.0 * r2;                                    // Eq. (2.4) in the paper
			double b = 1.0;                                         // parameters in Eq. (2.5)
			double l = (a2 - 1.0) * R.nextDouble() + 1.0;        // parameters in Eq. (2.5)
			double p = R.nextDouble();                           // p in Eq. (2.6)

			double[] newPos = new double[dim];
			for (int i = 0; i < dim; i++) {
				if (p < 0.5) {
					if (Math.abs(A) < 1) {
						double D_Leader = Math.abs(C * bestSol[i] - w.position[i]);  // Eq. (2.1)
						newPos[i] = bestSol[i] - A * D_Leader;      // Eq. (2.2)
					} else {
						double[] randW = whales.get(R.nextInt(population)).getPosition();
						double D_X_rand = Math.abs(C * randW[i] - w.position[i]); // Eq. (2.7)
						newPos[i] = randW[i] - A * D_X_rand;  // Eq. (2.8)
					}
				} else {
					double distance2Leader = Math.abs(bestSol[i] - w.position[i]);
					// Eq. (2.5)
					newPos[i] = distance2Leader * Math.exp(b * l) * Math.cos(2.0 * Math.PI * l) + bestSol[i];
				}
			}
			w.setPosition(newPos);
		}
	}

	public Whale tournamentSelection() {
		List<Whale> tournament = new ArrayList<>();
		int tournamentSize = (int) (tournamentRatio * population);
		for (int i = 0; i < tournamentSize; i++) {
			int randomIndex = R.nextInt(population);
			tournament.add(whales.get(randomIndex));
		}
		return Collections.min(tournament);
	}

	public Whale crossover(Whale w1, Whale w2) {
		double[] pos1 = w1.getPosition();
		double[] pos2 = w2.getPosition();
		double[] pos = new double[dim];
		for (int i = 0; i < dim; i++) {
			if (R.nextDouble() < crossoverRate) {
				pos[i] = pos1[i];
			} else {
				pos[i] = pos2[i];
			}
		}
		return new Whale(pos);
	}

	public void mutate(Whale w) {
		for (int i = 0; i < dim; i++) {
			if (R.nextDouble() < mutationRate) {
				w.position[i] = upperBound * R.nextDouble();
			}
		}
	}

	private void phaseOut(int n) {
		for (int i = 0; i < n; i++) {
			Whale w1 = tournamentSelection();
			Whale w2 = tournamentSelection();
			Whale w = crossover(w1, w2);
			mutate(w);
			whales.get(population - i - 1).setPosition(w.getPosition());
		}
	}

	public int[] run() {
		initializePopulation();
		evaluatePopulation();
		for (int iter = 0; iter < maxIter; iter++) {
			// a decreases linearly from 2 to 0 in Eq. (2.3)
			double a = 2.0 - (double)iter * (2.0 / maxIter);
			// a2 linearly decreases from -1 to -2 to calculate t in Eq. (3.12)
			double a2 = (double)iter * (-1.0 / maxIter) - 1.0 ;
			updatePosition(a, a2);
			// evaluatePopulation();
			phaseOut((int) (population * phaseOutRatio));
			evaluatePopulation();
		}
		return Arrays.stream(bestSol).mapToInt((x) -> (int) x).toArray();
	}
}
