package la4am12.woa;

import java.util.Random;

/**
 * @author : LA4AM12
 * @create : 2023-02-10 16:16:20
 * @description : class implements the whale optimization algorithm
 */
public class WOA {
	private OptFunction optFunction;
	private int lb, ub;
	private int population;
	private int dim, maxIter;
	private int[][] positions;
	private boolean minimize;
	private double[] convergenceCurve;
	private int[] optimalPos;
	private double optimalScore;

	public WOA(OptFunction optFunction, int population, int lb, int ub, int dim, int maxIter, boolean minimize) {
		this.optFunction = optFunction;
		this.population = population;
		this.lb = lb;
		this.ub = ub;
		this.dim = dim;
		this.maxIter = maxIter;
		this.positions = new int[population][dim];
		this.convergenceCurve = new double[maxIter];
		this.minimize = minimize;
		this.optimalScore = minimize ? Double.MAX_VALUE : -Double.MAX_VALUE;
		optimalPos = new int[dim];
		initPopulation();
	}


	private void adjustPositions(int agentIndex) {
		for (int j = 0; j < dim; j++) {
			if (positions[agentIndex][j] < lb) {
				positions[agentIndex][j] = lb;
			}
			if (positions[agentIndex][j] > ub) {
				positions[agentIndex][j] = ub;
			}
		}
	}


	private void initPopulation() {
		Random rand = new Random();
		this.positions = new int[population][dim];
		for (int i = 0; i < population; i++) {
			for (int j = 0; j < dim; j++) {
				positions[i][j] = (int) (lb + rand.nextDouble() * (ub - lb));
			}
		}
	}

	private void calcFitness() {
		for (int i = 0; i < population; i++) {
			// Return back the search agents that go beyond the boundaries of the search space
			adjustPositions(i);

			// Calculate objective function for each search agent
			double fitness = optFunction.calc(positions[i]);

			// Update the leader
			if (minimize && fitness < optimalScore || !minimize && fitness > optimalScore) {
				optimalScore = fitness;
				System.arraycopy(positions[i], 0, optimalPos, 0, dim);
			}
		}
	}


	private void updatePosition(double a, double a2) {
		Random rand = new Random();
		for (int i = 1; i < population; i++) {
			double r1 = rand.nextDouble();
			double r2 = rand.nextDouble();
			double A = 2.0 * a * r1 - a;                            // Eq. (2.3) in the paper
			double C = 2.0 * r2;                                    // Eq. (2.4) in the paper
			double b = 1.0;                                         // parameters in Eq. (2.5)
			double l = (a2 - 1.0) * rand.nextDouble() + 1.0;        // parameters in Eq. (2.5)
			double p = rand.nextDouble();                           // p in Eq. (2.6)

			for (int j = 0; j < dim; j++) {
				if (p < 0.5) {
					if (Math.abs(A) < 1) {
						double D_Leader = Math.abs(C * optimalPos[j] - positions[i][j]);  // Eq. (2.1)
						positions[i][j] = (int) (optimalPos[j] - A * D_Leader);      // Eq. (2.2)
					} else {
						int randWhaleIdx = rand.nextInt(population);
						int[] randomPos = positions[randWhaleIdx];
						double D_X_rand = Math.abs(C * randomPos[j] - positions[i][j]); // Eq. (2.7)
						positions[i][j] = (int) (randomPos[j] - A * D_X_rand);  // Eq. (2.8)
					}
				} else {
					double distance2Leader = Math.abs(optimalPos[j] - positions[i][j]);
					// Eq. (2.5)
					positions[i][j] = (int) (distance2Leader * Math.exp(b * l) * Math.cos(2.0 * Math.PI * l) + optimalPos[j]);
				}
			}
		}
	}

	public int[] execute() {
		for (int iter = 0; iter < maxIter; iter++) {
			calcFitness();
			convergenceCurve[iter] = optimalScore;

			// a decreases linearly from 2 to 0 in Eq. (2.3)
			double a = 2.0 - (double) iter * (2.0 / maxIter);

			// a2 linearly decreases from -1 to -2 to calculate t in Eq. (3.12)
			double a2 = -1.0 + (double) iter * (-1.0 / maxIter);

			updatePosition(a, a2);
		}
		calcFitness();
		return optimalPos;
	}

	public double[] getConvergenceCurve() {
		return convergenceCurve;
	}

	public int[] getLeaderPos() {
		return optimalPos;
	}

	public double getOptimalScore() {
		return optimalScore;
	}

}
