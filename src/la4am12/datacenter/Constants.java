package la4am12.datacenter;

/**
 * @author : LA4AM12
 * @create : 2023-03-15 09:43:57
 * @description :
 */
public interface Constants {
	/**
	 * Low performance mips
	 */
	public static final int L_MIPS = 1000;

	/**
	 * Medium performance mips
	 */
	public static final int M_MIPS = 2000;

	/**
	 * High performance mips
	 */
	public static final int H_MIPS = 4000;

	/**
	 * Low performance price ($ per sec)
	 */
	public static final double L_PRICE = 0.3;

	/**
	 * Medium performance price ($ per sec)
	 */
	public static final double M_PRICE = 0.6;

	/**
	 * High performance price ($ per sec)
	 */
	public static final double H_PRICE = 0.9;

	/**
	 * Low performance vms count
	 */
	public static final int L_VM_N = 3;

	/**
	 * Medium performance vms count
	 */
	public static final int M_VM_N = 2;

	/**
	 * High performance vms count
	 */
	public static final int H_VM_N = 1;

	/**
	 * RAM of each VM (MB)
	 */
	public static final int RAM = 2048;

	/**
	 * VM storage capacity
	 */
	public static final long STORAGE = 100000;

	/**
	 * VM image size
	 */
	public static final long IMAGE_SIZE = 10000;

	/**
	 * VM bandwidth
	 */
	public static final int BW = 1000;
}
