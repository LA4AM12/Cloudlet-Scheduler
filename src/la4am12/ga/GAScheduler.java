package la4am12.ga;

import la4am12.datacenter.Scheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

/**
 * @author : LA4AM12
 * @create : 2023-03-15 13:19:12
 * @description : GA schedule Algorithm
 */
public class GAScheduler extends Scheduler {
	private static final int POPULATION_SIZE = 100; // 种群大小
	private static final int MAX_GENERATIONS = 100; // 最大迭代次数
	private static final double CROSSOVER_RATE = 0.6; // 交叉概率
	private static final double MUTATION_RATE = 0.005; // 变异概率
	private static final double TOURNAMENT_RATIO = 0.15; // 锦标赛选择中的竞争个数占比

	private GeneticAlgorithm ga;

	public GAScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		super(cloudletList, vmList);
		this.ga = new GeneticAlgorithm(this::estimateFitness, POPULATION_SIZE, CROSSOVER_RATE, MUTATION_RATE, vmNum, cloudletNum, (int) (cloudletNum * TOURNAMENT_RATIO), MAX_GENERATIONS);
		Log.printLine("Using GA scheduler");
	}

	@Override
	public int[] allocate() {
		return ga.run();
	}
}
