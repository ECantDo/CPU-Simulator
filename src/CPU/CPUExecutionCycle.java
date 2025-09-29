package CPU;

import java.util.LinkedHashMap;
import java.util.Map;

public class CPUExecutionCycle {

	ProgramCounter programCounter;
	Registers registers;
	ALU alu;
	ProgramMemory programMemory;
	Stack stack;
	RAM ram;
	IO io;
	int speed;

	public CPUExecutionCycle() {
		programCounter = new ProgramCounter();
		registers = new Registers();
		alu = new ALU();
		programMemory = new ProgramMemory();
		stack = new Stack();
		ram = new RAM();
		io = new IO();
		speed = 10;
	}

	public CPUExecutionCycle(int speed) {
		this();
		this.speed = speed;
	}

	public CPUExecutionCycle(int[] program, int speed) {
		this();
		programMemory = new ProgramMemory(program);
		this.speed = speed;
	}

	public void loop() {
		System.out.println("PROGRAM START\n");
		long step = 0;
		long cycleStartTime;
		boolean cont = true;
		while (cont) {
			// Info print
			System.out.print(programCounter.getProgramCounter() + " : " + registers.toString()
					+ " : S" + step++ + "\r");

			// Do a cycle
			cycleStartTime = System.currentTimeMillis();
			cont = cycle();
			int sleepTime = (int) (System.currentTimeMillis() - cycleStartTime);

			// Sleep if the cycle time was shorter than the cycle time.
			if (sleepTime <= 0) {
				continue;
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		// Output Print
		System.out.println();
		System.out.println("Program finished\n\nREGISTERS:");
		System.out.println(registers.toString());

//		System.out.println();
//		System.out.println("RAM:");
//		System.out.println(ram.toString());

	}

	public boolean cycle() {
		int instruction = programMemory.getInstruction(programCounter.getProgramCounter());
		programCounter.increment(1); // TODO: this is no longer true

//		throw new UnsupportedOperationException("CPU 'cycle()' needs to be remade");

		// Extract values
		Map<String, Integer> parts = decodeInstruction(instruction);
		System.out.println(parts);

		return parts.get("opcode") != 0; // if == 0; stop looping; return false


        /*
        // Extract the opcode, byte01, byte02, and byte03 from the instruction
        byte opcode = (byte) ((instruction & (0xFF00_0000)) >> 24);
        byte byte01 = (byte) ((instruction & (0x00FF_0000)) >> 16);
        byte byte02 = (byte) ((instruction & (0x0000_FF00)) >> 8);
        byte byte03 = (byte) ((instruction & (0x0000_00FF)) >> 0);

        boolean immediate_a = (opcode & 0b1000_0000) != 0; // is the immediate bit set for value A?
        boolean immediate_b = (opcode & 0b0100_0000) != 0; // is the immediate bit set for value B?
        opcode = (byte) (opcode & 0b0011_1111); // remove the immediate bits

        if (opcode == 0) {
            return true; // If the opcode is 0, then the loop should continue; noop instruction
        }
        if (opcode == 1) {
            return false; // If the opcode is 1, then the loop should stop; halt instruction
        }


        byte[] registerValues = registers.get(byte01, byte02); // Get the values in the registers


        // THIS HAS TO HAPPEN
        byte valueA = immediate_a ? byte01 : registerValues[0]; // Mux for value A; immediate_a vs registerValues[0]
        byte valueB = immediate_b ? byte02 : registerValues[1]; // Mux for value B; immediate_b vs registerValues[1]


        // Execute the RAM
        if (opcode == 24) { // STR
            ram.set(valueB, valueA);
//            System.out.println("set ram idx: " + valueB + " val: " + valueA);
        } else if (opcode == 25) { // LOD
            registers.set(byte03, (byte) ram.get(valueB)); // Get from the ram, set in the registers
        }
        // Execute the stack
        else if (opcode == 26) { // CAL
            stack.push(programCounter.getProgramCounter()); // THE COUNTER WAS INCREMENTED AT THE START OF THE CYCLE
            programCounter.set(byte03);
            // REMEMBER THAT THE COUNTER VALUE IS NOT THE SAME AS THE CURRENT INSTRUCTION, IT IS ONE AHEAD
        } else if (opcode == 27) { // RET
            programCounter.set(stack.pop());
        } else if (opcode == 28) { // IO Out
            io.ioOutput(valueA + (valueB << 8), byte03);
        } else if(opcode == 29) { // IO In
            registers.set(byte03, (byte)io.ioInput(valueA));
        }

        // Execute the CPU.ALU and CPU.CLU
        boolean runALU = ALU.getOpcode(opcode) != -1;
        int result = alu.run(opcode, valueA, valueB); // Execute the CPU.ALU

        boolean runCLU = CLU.getOpcode(opcode) != -1;
        boolean setCounter = clu.run(opcode, valueA, valueB); // Execute the CPU.CLU

//        System.out.println("\nBYTES: " +
//                String.format("%8s", Integer.toBinaryString(byte01)).replace(' ', '0') + " " +
//                String.format("%8s", Integer.toBinaryString(byte02)).replace(' ', '0') + " " +
//                String.format("%8s", Integer.toBinaryString(byte03)).replace(' ', '0'));
//        System.out.println("Op value: " + String.format("%32s", Integer.toBinaryString(instruction)).replace(' ', '0'));

        if (runALU) {
            registers.set(byte03, result); // Set the value in the register
        } else if (runCLU && setCounter) {
            programCounter.set(byte03);
        }


//        registers.set(byte03, result); // Set the value in the register

        return true; // Returns true if the loop should continue
        */
	}

	/**
	 * Converts a number from an integer to a binary String.
	 *
	 * @param value The value to convert.
	 * @return A String representation of the value, in binary.
	 */
	public String getBinaryValue(int value) {
		StringBuilder output = new StringBuilder(Integer.toBinaryString(value & CPUSpecs.bitMask));
		while (output.length() != CPUSpecs.bitCount) {
			output.insert(0, "0");
		}
		return output.toString();
	}

	public static Map<String, Integer> decodeInstruction(int operationValue) {
		Map<String, Integer> result = new LinkedHashMap<>();

		// Base fields
		int opcode = operationValue & 0xFF;              // bits 0..7
		int func = operationValue & 0b11111;           // low 5 bits (function selector)
		result.put("opcode", opcode);
		result.put("function", func);

		switch (func) {
			// Without immediate...
			case 0: // Halt
				result.put("rd", 0);
				result.put("rs1", 0);
				result.put("rs2", 0);
				result.put("imm", 0);
				break;
			case 1: // ALU
			case 2: // Barrel Shifter
			case 3: // Add with flags
				result.put("rd", (operationValue >> 8) & 0b11111);
				result.put("rs1", (operationValue >> 13) & 0b11111);
				result.put("rs2", (operationValue >> 18) & 0b11111);
				break;

			case 5: // Jump and link
			case 25: // Load Immediate
				result.put("rd", (operationValue >> 8) & 0b11111);

				// imm split: 2 bits at 16..17, 14 bits at 18..
				int immHi = (operationValue >> 16) & 0b11;
				int immLo = (operationValue >> 18) & 0x3FFF;
				int imm = (immHi << 14) | immLo;

//				System.err.println("--->" + imm);

				// sign-extend if you need signed immediates
				imm = signExtend(imm, 16);
				result.put("imm", imm);
				break;

			case 18: // Shifter (imm 4 bits)
			case 17: // ALU with imm
			case 6:  // Jump and Link Register
			case 7:  // Store/load
				result.put("rd", (operationValue >> 8) & 0b11111);
				result.put("rs1", (operationValue >> 13) & 0b11111);

				int imm14 = (operationValue >> 18) & 0x3FFF;
				imm14 = signExtend(imm14, 14);
				result.put("imm", imm14);
				break;

			case 4: // Branching
				result.put("rs1", (operationValue >> 13) & 0b11111);
				result.put("rs2", (operationValue >> 18) & 0b11111);

				// low 5 bits from [8..12], upper 9 from [23..31]
				int low5 = (operationValue >> 8) & 0x1F;
				int high9 = (operationValue >> 23) & 0x1FF;
				int branchImm = (high9 << 5) | low5;
				branchImm = signExtend(branchImm, 14);
				result.put("imm", branchImm);
				break;

			default:
				throw new UnsupportedOperationException(
						"Unimplemented decode for function " + func
				);
		}

		return result;
	}

	private static int signExtend(int value, int bits) {
		int shift = 32 - bits;
		return (value << shift) >> shift;
	}

	/**
	 * Despite the name, it does not in fact print the value, it gets the name as a string.
	 * See {@link this#getBinaryValue(int)} for the replacement function.
	 *
	 * @param value The value to convert to a string.
	 * @return String representation of the number, in binary.
	 */
	@Deprecated
	public String printValue(int value) {
		String binary = Integer.toBinaryString(value);
		binary = "00000000000000000000000000000000" + binary;
		binary = binary.substring(binary.length() - CPUSpecs.bitCount);
		return binary;
	}

	/**
	 * Despite the name, it does not in fact print the value, it gets the name as a string.
	 * See {@link this#getBinaryValue(int)} for the replacement function.
	 *
	 * @param value The value (byte) to convert to a string.
	 * @return String representation of the number, in binary.
	 * @deprecated No longer working with an 8-bit CPU...
	 */
	@Deprecated
	public String printValue(byte value) {
		String binary = Integer.toBinaryString(value);
		binary = "00000000" + binary;
		binary = binary.substring(binary.length() - 8);
		return binary;
	}

}
