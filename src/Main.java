import CPU.CPUExecutionCycle;

import Assembler.Build;
import Assembler.Operations.Opcodes;
import Assembler.Operations.Registers;

import java.io.File;
import java.util.Map;

import static CPU.CPUExecutionCycle.decodeInstruction;

public class Main {

	public static void main(String[] args) {
//		int[] instructions = Build.build("src\\Programs_V2_0\\fib.as");

		String programPath = "src" + File.separator + "Programs_V2_0" + File.separator + "fib.as";

		int delay = 0;
		int[] program = Build.build(programPath);

//		for (int i : program) {
//			System.out.println(disassemble(i));
//		}

		CPUExecutionCycle executionLoop = new CPUExecutionCycle(program, delay);
		executionLoop.loop();
	}

	public static String disassemble(int instruction) {
		Map<String, Integer> decoded = decodeInstruction(instruction);

		int opcode = decoded.get("opcode");
		int func = decoded.get("function");

		// Halt
		if (func == 0) {
			return "hlt";
		}

		switch (func) {
			case 1: // ALU
			case 2: // Barrel Shifter
			case 3: // Add with flags
				return String.format("op%d %s %s %s",
						func,
						Registers.riscVRegisters[decoded.get("rd")],
						Registers.riscVRegisters[decoded.get("rs1")],
						Registers.riscVRegisters[decoded.get("rs2")]);

			case 5:  // Jump and link
			case 25: // Load Immediate
				return String.format("li %s %d",
						Registers.riscVRegisters[decoded.get("rd")],
						decoded.get("imm"));

			case 17: // ALU with imm
				return String.format("op%d %s %d %s ",
						func,
						Registers.riscVRegisters[decoded.get("rd")],
						decoded.get("imm"),
						Registers.riscVRegisters[decoded.get("rs1")]
				);

			case 18: // Shifter (imm limited to 4 bits)
				return String.format("sh %s %s %d",
						Registers.riscVRegisters[decoded.get("rd")],
						Registers.riscVRegisters[decoded.get("rs1")],
						decoded.get("imm") & 0xF);

			case 6: // Jump and Link Register
				return String.format("jalr %s %s %d",
						Registers.riscVRegisters[decoded.get("rd")],
						Registers.riscVRegisters[decoded.get("rs1")],
						decoded.get("imm"));

			case 7: // Store/load
				return String.format("mem %s %d(%s)",
						Registers.riscVRegisters[decoded.get("rd")],
						decoded.get("imm"),
						Registers.riscVRegisters[decoded.get("rs1")]);

			case 4: // Branch
				return String.format("br %s %s %d",
						Registers.riscVRegisters[decoded.get("rs1")],
						Registers.riscVRegisters[decoded.get("rs2")],
						decoded.get("imm"));

			default:
				return String.format("unknown(op=%d, func=%d)", opcode, func);
		}
	}
}