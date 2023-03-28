package la4am12.datacenter;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 09:26:19
 * @description : Mapping cloudlets to Vms using fitness function
 */
public abstract class Scheduler {
	// cost
	private static final double ALPHA = 1.0/3;
	// total time
	private static final double BETA = 1.0/3;
	// LB
	private static final double GAMMA = 1.0/3;
	protected List<Cloudlet> cloudletList;
	protected List<Vm> vmList;
	protected int cloudletNum;
	protected int vmNum;
	private int[] randomCloudletToVm;


	public Scheduler(List<Cloudlet> cloudletList, List<Vm> vmList) {
		this.cloudletList = cloudletList;
		this.vmList = vmList;
		cloudletNum = cloudletList.size();
		vmNum = vmList.size();
		randomCloudletToVm = new int[cloudletNum];
		Random random = new Random();
		for (int i = 0; i < cloudletNum; i++) {
			randomCloudletToVm[i] = random.nextInt(vmNum);
		}
	}

	public abstract int[] allocate();

	public void schedule() {
		int[] cloudletToVm = allocate();
		for (int i = 0; i < cloudletNum; i++) {
			cloudletList.get(i).setVmId(cloudletToVm[i]);
		}
		Log.printLine("estimate time span: " + estimateMakespan(cloudletToVm));
		Log.printLine("estimate LB: " + estimateLB(cloudletToVm));
		Log.printLine("estimate cost: " + estimateCost(cloudletToVm));
		Log.printLine("estimate totalTime: " + estimateTotalTime(cloudletToVm));
		Log.printLine("estimate fitness: " + estimateFitness(cloudletToVm));
	}

	public double estimateLB(int[] cloudletToVm) {
		double[] executeTimeOfVM = new double[vmNum];
		double avgExecuteTime = 0;
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			int vmId = cloudletToVm[i];
			double execTime = length / vmList.get(vmId).getMips();
			executeTimeOfVM[vmId] += execTime;
			avgExecuteTime += execTime;
		}
		avgExecuteTime /= vmNum;
		double LB = 0;
		for (int i = 0; i < vmNum; i++) {
			LB += Math.pow(executeTimeOfVM[i] - avgExecuteTime, 2);
		}
		LB = Math.sqrt(LB / vmNum);
		return LB;
	}

	public double estimateMakespan(int[] cloudletToVm) {
		double[] executeTimeOfVM = new double[vmNum];
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			int vmId = cloudletToVm[i];
			executeTimeOfVM[vmId] += length / vmList.get(vmId).getMips();
		}
		return Arrays.stream(executeTimeOfVM).max().getAsDouble();
	}

	public double estimateCost(int[] cloudletToVm) {
		double cost = 0;
		double costPerSec = 0;
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			double mips = vmList.get(cloudletToVm[i]).getMips();
			if (mips == Constants.L_MIPS) {
				costPerSec = Constants.L_PRICE;
			} else if (mips == Constants.M_MIPS) {
				costPerSec = Constants.M_PRICE;
			} else if (mips == Constants.H_MIPS) {
				costPerSec = Constants.H_PRICE;
			}
			int vmId = cloudletToVm[i];
			cost += length / vmList.get(vmId).getMips() * costPerSec;
		}
		return cost;
	}

	public double estimateTotalTime(int[] cloudletToVm) {
		double totalTime = 0;
		for (int i = 0; i < cloudletNum; i++) {
			long length = cloudletList.get(i).getCloudletLength();
			int vmId = cloudletToVm[i];
			totalTime += length / vmList.get(vmId).getMips();
		}
		return totalTime;
	}

	private double estimateMaxCost() {
		int[] cloudletToVm = new int[cloudletNum];
		Arrays.fill(cloudletToVm, 0);

		return estimateCost(cloudletToVm);
	}

	private double estimateMinCost() {
		int[] cloudletToVm = new int[cloudletNum];
		Arrays.fill(cloudletToVm, vmNum-1);
		return estimateCost(cloudletToVm);
	}

	private double estimateMaxTotalTime() {
		int[] cloudletToVm = new int[cloudletNum];
		Arrays.fill(cloudletToVm, 0);
		return estimateTotalTime(cloudletToVm);
	}

	private double estimateMinTotalTime() {
		int[] cloudletToVm = new int[cloudletNum];
		Arrays.fill(cloudletToVm, vmNum-1);
		return estimateTotalTime(cloudletToVm);
	}

	private double estimateMaxLB() {
		return estimateLB(randomCloudletToVm);
	}

	private double estimateMinLB() {
		return 0.0;
	}

	public double estimateFitness(int[] cloudletToVm) {
		return ALPHA * (estimateCost(cloudletToVm) - estimateMinCost()) / (estimateMaxCost() - estimateMinCost())
				+ BETA * (estimateTotalTime(cloudletToVm) - estimateMinTotalTime()) / (estimateMaxTotalTime() - estimateMinTotalTime())
				+ GAMMA * (estimateLB(cloudletToVm) - estimateMinLB()) / (estimateMaxLB() - estimateMinLB());
	}
}
