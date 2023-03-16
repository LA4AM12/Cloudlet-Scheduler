package la4am12.random;

import la4am12.datacenter.Scheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.Random;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 16:17:50
 * @description : Random schedule
 */
public class RandomScheduler extends Scheduler {
	public RandomScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		super(cloudletList, vmList);
		Log.printLine("Using Random scheduler");
	}

	@Override
	public int[] allocate() {
		int[] cloudletToVm = new int[cloudletNum];
		Random random = new Random();
		for (int i = 0; i < cloudletNum; i++) {
			cloudletToVm[i] = random.nextInt(vmNum);
		}
		return cloudletToVm;
	}
}
