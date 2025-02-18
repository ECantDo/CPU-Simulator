package Assembler.Operations;

public class Registers {
	public static final String[] registers = new String[]{
			"r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7", "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15",
			"r16", "r17", "r18", "r19", "r20", "r21", "r22", "r23", "r24", "r25", "r26", "r27", "r28", "r29", "r30",
			"r31"
	};

	public static final String[] riscVRegisters = new String[]{
			"zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6",
			"a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
	};

	public static int getRegister(String register) {
		for (int i = 0; i < registers.length; i++) {
			if (registers[i].equals(register)) {
				return i;
			}
			if (riscVRegisters[i].equals(register)) {
				return i;
			}
		}
		return -1;
	}
}
