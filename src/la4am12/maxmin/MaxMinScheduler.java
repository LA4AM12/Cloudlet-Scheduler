package la4am12.maxmin;

import la4am12.minmin.MinMinScheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.Collections;
import java.util.List;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 19:09:50
 * @description : MaxMin + Greedy Algorithm
 */
public class MaxMinScheduler extends MinMinScheduler {
	public MaxMinScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		super(cloudletList, vmList);
		Collections.reverse(cloudletList);
		Log.printLine("Using MaxMin scheduler");
	}
}
