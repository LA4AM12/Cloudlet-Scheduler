package la4am12.woaga;

import la4am12.datacenter.Scheduler;
import la4am12.ga.GeneticAlgorithm;
import la4am12.woa.WhaleOptimizationAlgorithm;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

/**
 * @author : LA4AM12
 * @create : 2023-03-16 10:49:41
 * @description :
 */
public class WOAGAScheduler extends Scheduler {
	private WhaleOptimizationAlgorithm woa;

	private static final int POPULATION = 100;

	private static final int MAX_ITER = 100;
	private static final int MAX_GENERATIONS = 100; // 最大迭代次数
	private static final double CROSSOVER_RATE = 0.6; // 交叉概率
	private static final double MUTATION_RATE = 0.005; // 变异概率
	private static final double TOURNAMENT_RATIO = 0.15; // 锦标赛选择中的竞争个数占比

	private GeneticAlgorithm ga;


	public WOAGAScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		super(cloudletList, vmList);
		this.woa = new WhaleOptimizationAlgorithm(this::estimateFitness, POPULATION, 0, vmNum-1, cloudletNum, MAX_ITER, true);
		this.ga = new GeneticAlgorithm(this::estimateFitness, POPULATION, CROSSOVER_RATE, MUTATION_RATE, vmNum, cloudletNum, (int) (cloudletNum * TOURNAMENT_RATIO), MAX_GENERATIONS);
		Log.printLine("Using WOA+GA scheduler");
	}

	@Override
	public int[] allocate() {
		int[] optimal = woa.execute();
		Log.printLine("WOA optimal: " + estimateMakespan(optimal));
		double[][] positions = woa.getPositions();
		int[][] chromosomes = new int[POPULATION][cloudletNum];

		for (int i = 0; i < POPULATION; i++) {
			for (int j = 0; j < cloudletNum; j++) {
				chromosomes[i][j] = (int) positions[i][j];
			}
		}
		ga.setChromosomes(chromosomes);
		return ga.run();
	}
}
