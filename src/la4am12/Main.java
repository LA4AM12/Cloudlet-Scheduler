package la4am12;

import la4am12.minmin.MinMinScheduler;
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
	 * Low performance mips
	 */
	private static final int L_MIPS = 1000;

	/**
	 * Medium performance mips
	 */
	private static final int M_MIPS = 2000;

	/**
	 * High performance mips
	 */
	private static final int H_MIPS = 4000;

	/**
	 * Low performance price ($ per sec)
	 */
	private static final float L_PRICE = 0.03f;

	/**
	 * Medium performance price ($ per sec)
	 */
	private static final float M_PRICE = 0.06f;

	/**
	 * High performance price ($ per sec)
	 */
	private static final float H_PRICE = 0.09f;

	/**
	 * Low performance vms count
	 */
	private static final int L_VM_N = 3;

	/**
	 * Medium performance vms count
	 */
	private static final int M_VM_N = 2;

	/**
	 * High performance vms count
	 */
	private static final int H_VM_N = 1;

	/**
	 * RAM of each VM (MB)
	 */
	private static final int RAM = 2048;

	/**
	 * VM storage capacity
	 */
	private static final long STORAGE = 100000;

	/**
	 * VM image size
	 */
	private static final long IMAGE_SIZE = 10000;

	/**
	 * VM bandwidth
	 */
	private static final int BW = 1000;

	/**
	 * Cloudlets count
	 */
	private static final int CLOUDLET_N = 100;

	private static final Random R = new Random(1);

	private static final int NUM_USER = 1;

	public static void main(String[] args) throws Exception {
		Log.printLine("Starting...");

		// First step: Initialize the CloudSim package. It should be called before creating any entities.
		CloudSim.init(NUM_USER, Calendar.getInstance(), false);

		// Second step: Create Datacenters
		createDatacenter("Datacenter");

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
		Scheduler scheduler = new MinMinScheduler(cloudletList, vmList);
		// Scheduler scheduler = new MaxMinScheduler(cloudletList, vmList);
		// Scheduler scheduler = new WOAScheduler(cloudletList, vmList);
		scheduler.schedule();

		// Starts the simulation
		CloudSim.startSimulation();

		// Print results when simulation is over
		List<Cloudlet> newList = broker.getCloudletReceivedList();
		printCloudletList(newList);
	}

	private static Datacenter createDatacenter(String name) throws Exception {
		// 1. We need to create a list to store our machine
		List<Host> hostList = new ArrayList<>();

		int ram = RAM * (L_VM_N + M_VM_N + H_VM_N);
		long storage = STORAGE * (L_VM_N + M_VM_N + H_VM_N);
		int bw = BW * (L_VM_N + M_VM_N + H_VM_N);
		int mips = L_MIPS * L_VM_N + M_MIPS * M_VM_N + H_MIPS * H_VM_N;

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
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<>(); // we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
	}

	private static List<Vm> createVms(int userId) {
		List<Vm> vmList = new ArrayList<>();
		
		int vmId = 0;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		
		for (int i = 0; i < L_VM_N; i++) {
			vmList.add(new Vm(vmId++, userId, L_MIPS, pesNumber, RAM, BW, IMAGE_SIZE, vmm, new CloudletSchedulerTimeShared()));
		}

		for (int i = 0; i < M_VM_N; i++) {
			vmList.add(new Vm(vmId++, userId, M_MIPS, pesNumber, RAM, BW, IMAGE_SIZE, vmm, new CloudletSchedulerTimeShared()));
		}

		for (int i = 0; i < H_VM_N; i++) {
			vmList.add(new Vm(vmId++, userId, H_MIPS, pesNumber, RAM, BW, IMAGE_SIZE, vmm, new CloudletSchedulerTimeShared()));
		}
		return vmList;
	}

	private static List<Cloudlet> createCloudlets(int userId) {
		List<Cloudlet> cloudletList = new ArrayList<>();
		int id = 0;
		int pesNumber = 1;
		long outputSize = 300;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		
		for(int i=0; i < CLOUDLET_N; i++) {
			// todo
			long length = R.nextInt(4000) + 1000;
			long fileSize = R.nextInt(20000) + 10000;
			Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet.setUserId(userId);
			cloudletList.add(cloudlet);
			id++;
		}
		return cloudletList;
	}

	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (Cloudlet value : list) {
			cloudlet = value;
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getSubmissionTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}

}
