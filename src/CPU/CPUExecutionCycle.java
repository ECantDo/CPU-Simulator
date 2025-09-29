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
			int sleepTime = (int) (System.currentTimeMillis() - cycleStartTime + speed);

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

		System.out.println();
		System.out.println("RAM:");
		System.out.println(ram.toString());

	}

	public boolean cycle() {
		int instruction = programMemory.getInstruction(programCounter.getProgramCounter());
		programCounter.increment(1); // default: move to next

		Map<String, Integer> parts = decodeInstruction(instruction);

//		System.out.println(parts);
		int func = parts.get("function");
		int opcode = parts.get("opcode");

		int rd = parts.getOrDefault("rd", 0);
		int rs1 = parts.getOrDefault("rs1", 0);
		int rs2 = parts.getOrDefault("rs2", 0);
		int imm = parts.getOrDefault("imm", 0);

		int rs1Val = registers.get(rs1);
		int rs2Val = registers.get(rs2);

		// Halt
		if (func == 0) {
			return false;
		}

		switch (func) {
			//===========================================================
			// ALU operations
			//===========================================================
			case 1: // ALU
			case 2: // Shifter
			case 3: // Add with carry/flags
				int aluResult = alu.runOperation(instruction, rs1Val, rs2Val);
				registers.set(rd, aluResult);
				break;

			//===========================================================
			// ALU immediate ops
			//===========================================================
			case 17: // ALU with imm
			case 18: // Shifter with imm
				int aluImmResult = alu.runOperation(instruction, rs1Val, imm);
				registers.set(rd, aluImmResult);
				break;

			//===========================================================
			// Branches (decide using ALU + flags)
			//===========================================================
			case 4:
				boolean takeBranch = switch (opcode >>> 5) {
					case 0 -> rs1Val == rs2Val;                      // BEQ
					case 1 -> rs1Val != rs2Val;                      // BNE
					case 2 -> rs1Val < rs2Val;                       // BLT
					case 3 -> rs1Val >= rs2Val;                      // BGE
					case 4 -> Integer.compareUnsigned(rs1Val, rs2Val) < 0;  // BLTU
					case 5 -> Integer.compareUnsigned(rs1Val, rs2Val) >= 0; // BGEU
					default -> false;
				};
				if (takeBranch) {
					programCounter.increment(imm);
				}
				break;

			//===========================================================
			// Load immediate
			//===========================================================
			case 25:
				registers.set(rd, imm);
				break;

			//===========================================================
			// Memory access
			//===========================================================
			case 7: // Load/Store
				// convention: rd is src/dest, rs1+imm is address
				int addr = registers.get(rd) + imm;
				if (opcode >> 5 == 0) { // STR
					ram.set(addr, rs1Val);
				} else { // LOD
					registers.set(rs1, ram.get(addr));
				}
				break;

			//===========================================================
			// Jump + Link
			//===========================================================
			case 5: // JAL
				registers.set(rd, programCounter.getProgramCounter());
				programCounter.setProgramCounter(imm);
				break;

			case 6: // JALR
				registers.set(rd, programCounter.getProgramCounter());
				programCounter.setProgramCounter(rs1Val + imm);
				break;

			//===========================================================
			// IO
			//===========================================================
			case 8: // IN / OUT
				int port = imm & 0xF; // same mask as decode

				if ((opcode >> 5) == 0) { // IN
					// rd gets input from port
					int valueIn = io.ioInput(port);
					registers.set(rd, valueIn);
				} else { // OUT
					// write rs1 to port
					io.ioOutput(rs1Val, port);
				}
				break;

			default:
				throw new UnsupportedOperationException("Unknown function " + func);
		}

		return true;
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

			case 8: // IO ports
				int portImm = (operationValue >> 18) & 0x3FFF; // upper imm field
				portImm &= 0xF; // only 4 bits are valid
				result.put("imm", portImm);

				if ((opcode >> 5) == 0) { // IN
					int rdIn = (operationValue >> 8) & 0b11111;
					result.put("rd", rdIn);
					result.put("rs1", 0); // unused
				} else { // OUT
					int rs1Out = (operationValue >> 13) & 0b11111;
					result.put("rs1", rs1Out);
					result.put("rd", 0); // unused
				}
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
