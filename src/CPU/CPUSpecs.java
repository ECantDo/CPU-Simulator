package CPU;

/**
 * Provides general CPU Specifications numbers, like how many bits the CPU is
 */
public class CPUSpecs {
	/**
	 * The number of bits the CPU will be using in the registers, and for the ALU,
	 */
	public static final int bitCount = 16;
	/**
	 * A mask to fix the integer size to the specified bit size.
	 * Takes in the bit count and converts it into the proper mask for that size.
	 */
	public static final int bitMask = 0xFFFFFFFF >>> (Integer.SIZE - bitCount);
	/**
	 * The size of the input addressing to get a value from the RAM.
	 */
	public static final int ramAddressSpaceMask = 0xFFF;
	/**
	 * The size of the input addressing to get a value from the ROM.
	 */
	public static final int romAddressSpaceMask = 0xFFF;
	/**
	 * Number of addresses in the RAM.
	 */
	public static final int ramAddressSpace = 4095;
	/**
	 * Number of addresses in the ROM.
	 */
	public static final int romAddressSpace = 4095;
}
