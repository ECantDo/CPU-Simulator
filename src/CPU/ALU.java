package CPU;

public class ALU {

	//==================================================================================================================
	// Operation codes, both mnemonics and numbers; get index from these
	//==================================================================================================================
	public static final String[] opCodesMnemonics = {
			"add", "sub", "and", "or", "xor",
			"nand", "nor", "xnor", "bsh"
	};

	public static final int[] opCodes = {
			2, 3, 4, 5, 6,
			7, 8, 9, 10
	};

	public static int getOpcode(int opcode) {
		for (int i = 0; i < opCodes.length; i++) {
			if (opCodes[i] == opcode) {
				return i;
			}
		}
		return -1;
	}

	public static int getOpcode(String mnemonic) {
		for (int i = 0; i < opCodesMnemonics.length; i++) {
			if (opCodesMnemonics[i].equals(mnemonic)) {
				return opCodes[i];
			}
		}
		return -1;
	}

	//==================================================================================================================
	// Run the CPU.ALU, get the operation and run the proper operation
	//==================================================================================================================
	public int run(int opCode, int input_a, int input_b) {
		return runOperation(getOpcode(opCode), input_a, input_b);
	}

	public int run(String mnemonic, int input_a, int input_b) {
		return runOperation(getOpcode(mnemonic), input_a, input_b);
	}

	private byte runOperation(int operation, int input_a, int input_b) {
		// Refer to the opCodes array for the operation value
		// Doing it this way makes it really easy to add new operations
		return switch (operation) {
			// TODO: Fix the ALU output table
//            case 0 -> add(input_a, input_b);
//            case 1 -> sub(input_a, input_b);
//            case 2 -> and(input_a, input_b);
//            case 3 -> or(input_a, input_b);
//            case 4 -> xor(input_a, input_b);
//            case 5 -> nand(input_a, input_b);
//            case 6 -> nor(input_a, input_b);
//            case 7 -> xnor(input_a, input_b);
//            case 8 -> bsh(input_a, input_b);
			default -> 0;
		};
	}

	//==================================================================================================================
	// Operations
	//==================================================================================================================
	private int add(int input_a, int input_b) {
		return (input_a + input_b) & CPUSpecs.bitMask;
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
		return (input_a >> input_b) & CPUSpecs.bitMask;
	}

}
