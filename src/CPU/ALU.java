package CPU;

public class ALU {

	//==================================================================================================================
	// Flags
	//==================================================================================================================
	boolean equalsZero;
	boolean carryOut;

	//==================================================================================================================
	// Run the CPU.ALU, get the operation and run the proper operation
	//==================================================================================================================

	//TODO; implement flags
	private int runOperation(int operation, int input_a, int input_b) {

		// The opcode is in the left 4-most bits, if it is 1, 2, or 3, it is an ALU operation.
		int opcode = operation & 0b00001111;
		if (!(opcode == 1 || opcode == 2 || opcode == 3)) {
			return 0;
		}

		// The sub opcode tells the ALU what operation to perform
		int subOpcode = (operation & 11100000) >> 5;

		// Refer to the opCodes array for the operation value
		// Doing it this way makes it really easy to add new operations
		if (opcode == 1)
			return switch (subOpcode) {
				case 0 -> add(input_a, input_b);
				case 1 -> sub(input_a, input_b);
				case 2 -> xor(input_a, input_b);
				case 3 -> or(input_a, input_b);
				case 4 -> and(input_a, input_b);
				case 5 -> xnor(input_a, input_b);
				case 6 -> nor(input_a, input_b);
				case 7 -> nand(input_a, input_b);
				default -> 0;
			};
		else if (opcode == 2)
			return switch (subOpcode) {
				case 0 -> lshift(input_a, input_b);
				case 2 -> arshift(input_a, input_b);
				case 3 -> rshift(input_a, input_b);
				default -> 0;
			};

		return addWithFlags(input_a, input_b);
	}

	//==================================================================================================================
	// Operations
	//==================================================================================================================
	private int add(int input_a, int input_b) {
		return (input_a + input_b) & CPUSpecs.bitMask;
	}

	private  int addWithFlags(int input_a, int input_b){
		return (input_a + input_b + (carryOut? 1 : 0)) & CPUSpecs.bitMask;
	}

	private int sub(int input_a, int input_b) {
		return (input_a - input_b) & CPUSpecs.bitMask;
	}

	private int and(int input_a, int input_b) {
		return (input_a & input_b) & CPUSpecs.bitMask;
	}

	private int or(int input_a, int input_b) {
		return (input_a | input_b) & CPUSpecs.bitMask;
	}

	private int xor(int input_a, int input_b) {
		return (input_a ^ input_b) & CPUSpecs.bitMask;
	}

	private int nand(int input_a, int input_b) {
		return ~(input_a & input_b) & CPUSpecs.bitMask;
	}

	private int nor(int input_a, int input_b) {
		return ~(input_a | input_b) & CPUSpecs.bitMask;
	}

	private int xnor(int input_a, int input_b) {
		return ~(input_a ^ input_b) & CPUSpecs.bitMask;
	}

	private int lshift(int input_a, int input_b) {
		input_b = input_b & 0xF; // 16-bit, only able to shift 15 times
		return (input_a << input_b) & CPUSpecs.bitMask;
	}

	private int rshift(int input_a, int input_b) {
		input_b = input_b & 0xF; // 16-bit, only able to shift 15 times
		return (input_a >>> input_b) & CPUSpecs.bitMask;
	}

	private int arshift(int input_a, int input_b) {
		input_b = input_b & 0xF; // 16-bit, only able to shift 15 times
		input_a |= -1 << CPUSpecs.bitCount; // Make everything above bit 16 a 1, so when right shifting 1s will fill
		return (input_a >> input_b) & CPUSpecs.bitMask;
	}

}
