package la4am12.minmin;

import la4am12.datacenter.Scheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 16:47:26
 * @description : MinMin + Greedy Algorithm
 */
public class MinMinScheduler extends Scheduler {
	public MinMinScheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		super(cloudletList, vmList);
		Collections.sort(cloudletList, Comparator.comparingLong(Cloudlet::getCloudletLength));
		Collections.sort(vmList, Comparator.comparingDouble(Vm::getMips));
		Log.printLine("Using MinMin scheduler");
	}

	@Override
	public int[] allocate() {
		int[] cloudletToVm = new int[cloudletNum];


		// time[i][j] denotes the execution time of task i on virtual machine j
		double[][] time = new double[cloudletNum][vmNum];
		for (int i = 0; i < cloudletNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				time[i][j] = cloudletList.get(i).getCloudletLength() / vmList.get(j).getMips();
			}
		}

		// vms' uptime
		double[] vmUptime = new double[vmNum];
		// the cloudlet num running on specific vm
		int[] vmTaskCount = new int[vmNum];


		// Prioritise mapping of small tasks to faster executing machines
		vmUptime[vmNum - 1] = time[0][vmNum - 1];
		vmTaskCount[vmNum - 1]++;
		cloudletToVm[0] = vmList.get(vmNum - 1).getId();

		int idx;
		double makespan;
		for (int i = 1; i < cloudletNum; i++) {
			makespan = vmUptime[vmNum - 1] + time[i][vmNum - 1];
			idx = vmNum - 1;

			for (int j = vmNum - 2; j >= 0; j--) {
				if (vmUptime[j] == 0) {
					if (makespan >= time[i][j]) {
						idx = j;
					}
					break;
				}

				if (makespan > vmUptime[j] + time[i][j]) {
					makespan = vmUptime[j] + time[i][j];
					idx = j;
				} else if (makespan == vmUptime[j] + time[i][j] && vmTaskCount[j] < vmTaskCount[idx]) {
					idx = j;
				}
			}
			vmUptime[idx] += time[i][idx];
			vmTaskCount[idx]++;
			cloudletToVm[i] = vmList.get(idx).getId();
		}

		return cloudletToVm;
	}
}
