package CPU;

import java.util.Arrays;

public class Registers {

	private final int[] regs;

	public Registers() {
		regs = new int[CPUSpecs.registerCount];
	}

	/**
	 * Write a value to a register.
	 * Register 0 is hardwired to 0 and ignores writes.
	 */
	public void set(int index, int value) {
		index &= CPUSpecs.registerMask;
		if (index == 0) return; // x0 = 0
		regs[index] = value & CPUSpecs.bitMask;
	}

	/**
	 * Read a single register.
	 */
	public int get(int index) {
		index &= CPUSpecs.registerMask;
		if (index == 0) return 0; // hardwired zero
		return regs[index];
	}

	/**
	 * Debug dump of all registers.
	 */
	@Override
	public String toString() {
		int[] snapshot = new int[regs.length];
		for (int i = 0; i < regs.length; i++) {
			snapshot[i] = get(i); // ensures masking + x0=0
		}
		return "REGISTERS: " + Arrays.toString(snapshot);
	}
}
