package la4am12;

import la4am12.datacenter.Constants;
import la4am12.datacenter.Scheduler;
import la4am12.datacenter.Type;
import la4am12.ga.GAScheduler;
import la4am12.maxmin.MaxMinScheduler;
import la4am12.minmin.MinMinScheduler;
import la4am12.woa.WOAScheduler;
import la4am12.woaga.WOGAScheduler;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author : LA4AM12
 * @create : 2023-02-23 09:33:04
 * @description : Cloud Task (Cloudlet) Scheduling Simulation
 */
public class Main {

	/**
	 * Cloudlets count
	 */
	private static final int CLOUDLET_N = 300;

	private static final Random R = new Random(0);

	private static final int NUM_USER = 1;

	public static void main(String[] args) throws Exception {
		Log.printLine("Starting...");

		// First step: Initialize the CloudSim package. It should be called before creating any entities.
		CloudSim.init(NUM_USER, Calendar.getInstance(), false);

		// Second step: Create Datacenters
		Datacenter datacenter0 = createDatacenter("Datacenter0", Type.LOW);
		Datacenter datacenter1 = createDatacenter("Datacenter1", Type.MEDIUM);
		Datacenter datacenter2 = createDatacenter("Datacenter2", Type.HIGH);


		// Third step: Create Broker
		DatacenterBroker broker = new DatacenterBroker("Broker");
		int brokerId = broker.getId();

		// Fourth step: Create five virtual machine
		List<Vm> vmList = createVms(brokerId);

		// submit vm list to the broker
		broker.submitVmList(vmList);

		// create cloudlets
		List<Cloudlet> cloudletList = createCloudlets(brokerId);

		// submit cloudlet list to the broker.
		broker.submitCloudletList(cloudletList);

		// allocate tasks to vms
		// Scheduler scheduler = new RandomScheduler(cloudletList, vmList);
		// Scheduler scheduler = new MinMinScheduler(cloudletList, vmList);
		// Scheduler scheduler = new MaxMinScheduler(cloudletList, vmList);
		// Scheduler scheduler = new WOAScheduler(cloudletList, vmList);
		// Scheduler scheduler = new GAScheduler(cloudletList, vmList);
		Scheduler scheduler = new WOGAScheduler(cloudletList, vmList);
		scheduler.schedule();

		// Starts the simulation
		Log.printLine("========== START ==========");
		CloudSim.startSimulation();

		// Print results when simulation is over
		List<Cloudlet> newList = broker.getCloudletReceivedList();
		printCloudletList(newList);
	}

	private static Datacenter createDatacenter(String name, Type type) throws Exception {
		int ram, bw, mips;
		long storage;
		double costPerSec;

		switch (type) {
			case LOW:
				ram = Constants.RAM * Constants.L_VM_N;
				bw = Constants.BW * Constants.L_VM_N;
				mips = Constants.L_MIPS * Constants.L_VM_N;
				storage = Constants.STORAGE * Constants.L_VM_N;
				costPerSec = Constants.L_PRICE;
				break;
			case MEDIUM:
				ram = Constants.RAM * Constants.M_VM_N;
				bw = Constants.BW * Constants.M_VM_N;
				mips = Constants.M_MIPS * Constants.M_VM_N;
				storage = Constants.STORAGE * Constants.M_VM_N;
				costPerSec = Constants.M_PRICE;
				break;
			case HIGH:
				ram = Constants.RAM * Constants.H_VM_N;
				bw = Constants.BW * Constants.H_VM_N;
				mips = Constants.H_MIPS * Constants.H_VM_N;
				storage = Constants.STORAGE * Constants.H_VM_N;
				costPerSec = Constants.H_PRICE;
				break;
			default:
				throw new Exception("Invalid datacenter type");
		}


		// 1. We need to create a list to store our machine
		List<Host> hostList = new ArrayList<>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		List<Pe> peList = new ArrayList<>();

		// 3. Create PEs and add these into a list.
		// To simplify the model, it will only have one core.
		peList.add(new Pe(0, new PeProvisionerSimple(mips)));

		// physical machine
		hostList.add(
				new Host(
						0,
						new RamProvisionerSimple(ram),
						new BwProvisionerSimple(bw),
						storage,
						peList,
						new VmSchedulerTimeShared(peList)
				)
		);

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this resource
		double costPerGB = 0.1; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<>(); // we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, costPerSec, costPerMem,
				costPerStorage, costPerGB);

		// 6. Finally, we need to create a PowerDatacenter object.
		return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
	}

	private static List<Vm> createVms(int userId) {
		List<Vm> vmList = new ArrayList<>();

		int vmId = 0;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		for (int i = 0; i < Constants.L_VM_N; i++) {
			vmList.add(new Vm(vmId++, userId, Constants.L_MIPS, pesNumber, Constants.RAM, Constants.BW, Constants.IMAGE_SIZE, vmm, new CloudletSchedulerSpaceShared()));
		}

		for (int i = 0; i < Constants.M_VM_N; i++) {
			vmList.add(new Vm(vmId++, userId, Constants.M_MIPS, pesNumber, Constants.RAM, Constants.BW, Constants.IMAGE_SIZE, vmm, new CloudletSchedulerSpaceShared()));
		}

		for (int i = 0; i < Constants.H_VM_N; i++) {
			vmList.add(new Vm(vmId++, userId, Constants.H_MIPS, pesNumber, Constants.RAM, Constants.BW, Constants.IMAGE_SIZE, vmm, new CloudletSchedulerSpaceShared()));
		}
		return vmList;
	}

	private static List<Cloudlet> createCloudlets(int userId) {
		List<Cloudlet> cloudletList = new ArrayList<>();
		int id = 0;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		for (int i = 0; i < CLOUDLET_N; i++) {
			long length = R.nextInt(40000) + 10000;
			long fileSize = R.nextInt(190) + 10;
			long outputSize = R.nextInt(190) + 10;
			Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet.setUserId(userId);
			cloudletList.add(cloudlet);
			id++;
		}
		return cloudletList;
	}

	private static void printCloudletList(List<Cloudlet> cloudletList) {
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time" + indent + "BWCost" + indent + "CPUCost");

		DecimalFormat dft = new DecimalFormat("###.##");
		double makespan = 0;
		int vmNum = Constants.L_VM_N + Constants.M_VM_N + Constants.H_VM_N;
		double[] executeTimeOfVM = new double[vmNum];
		double cost = 0;
		double LB = 0;

		for (Cloudlet cloudlet : cloudletList) {
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {

				double finishTime = cloudlet.getFinishTime();
				if (finishTime > makespan) {
					makespan = finishTime;
				}
				int vmId = cloudlet.getVmId();
				double actualCPUTime = cloudlet.getActualCPUTime();
				executeTimeOfVM[vmId] += actualCPUTime;
				cost += actualCPUTime * cloudlet.getCostPerSec();

				Log.print("SUCCESS");
				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getSubmissionTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(finishTime)
						+ indent + indent + indent + dft.format(cloudlet.getProcessingCost())
						+ indent + indent + indent + dft.format(actualCPUTime * cloudlet.getCostPerSec()));
			}
		}
		double avgExecuteTime = Arrays.stream(executeTimeOfVM).average().getAsDouble();
		for (int i = 0; i < vmNum; i++) {
			// System.out.print(executeTimeOfVM[i] + " ");
			LB += Math.pow(executeTimeOfVM[i] - avgExecuteTime, 2);
		}
		LB = Math.sqrt(LB / vmNum);
		double finalMakespan = makespan;
		Log.printLine("makespan: " + finalMakespan);
		Log.printLine("LB: " + LB);
		Log.printLine("cost: " + cost);
	}

}
