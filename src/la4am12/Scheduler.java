package la4am12;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import java.util.Arrays;
import java.util.List;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 09:26:19
 * @description : Mapping cloudlets to Vms
 */
public abstract class Scheduler {
	protected List<Cloudlet> cloudletList;
	protected List<Vm> vmList;
	protected int cloudletNum;
	protected int vmNum;

	public Scheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		this.cloudletList = cloudletList;
		this.vmList = vmList;
		cloudletNum = cloudletList.size();
		vmNum = vmList.size();
	}

	public abstract int[] allocate();

	public void schedule() {
		int[] cloudletToVm = allocate();
		for (int i = 0; i < cloudletNum; i++) {
			cloudletList.get(i).setVmId(cloudletToVm[i]);
		}
	}

	public double estimateLB(int[] cloudletToVm) {
		int vmNum = vmList.size();
		int cloudletNum = cloudletList.size();

		double[] executeTimeOfVM = new double[vmNum];
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			int vmId = cloudletToVm[i];
			executeTimeOfVM[vmId] += length / vmList.get(vmId).getMips();
		}
		double timeSpan = Arrays.stream(executeTimeOfVM).max().getAsDouble();
		double[] LBOfVM = new double[vmNum];
		for (int i = 0; i < vmNum; i++) {
			LBOfVM[i] = executeTimeOfVM[i] / timeSpan;
		}

		return Arrays.stream(LBOfVM).average().getAsDouble();

	}

	public double estimateTimeSpan(int[] cloudletToVm) {
		int vmNum = vmList.size();
		int cloudletNum = cloudletList.size();

		double[] executeTimeOfVM = new double[vmNum];
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			int vmId = cloudletToVm[i];
			executeTimeOfVM[vmId] += length / vmList.get(vmId).getMips();
		}
		return Arrays.stream(executeTimeOfVM).max().getAsDouble();
	}
}
