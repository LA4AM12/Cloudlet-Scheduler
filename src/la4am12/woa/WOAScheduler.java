package la4am12.woa;


import la4am12.datacenter.Scheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 14:39:48
 * @description : WOA schedule Algorithm
 */
public class WOAScheduler extends Scheduler {
	private WhaleOptimizationAlgorithm woa;

	private static final int POPULATION = 100;

	private static final int MAX_ITER = 500;

	public WOAScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		super(cloudletList, vmList);
		this.woa = new WhaleOptimizationAlgorithm(this::estimateFitness, POPULATION, 0, vmNum-1, cloudletNum, MAX_ITER, true);
		Log.printLine("Using WOGA scheduler");
	}

	@Override
	public int[] allocate() {
		return woa.execute();
	}
}
