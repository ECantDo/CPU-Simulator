package Assembler.Operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PseudoOpcodes {

	public static final Map<String, String[]> opcodeMap = new HashMap<>() {{
		put("mv", new String[]{"add", "zero", "rs", "rd"});
		put("nop", new String[]{"add", "zero", "zero", "zero"});
		put("neg", new String[]{"sub", "zero", "rs", "rd"});

		put("beqz", new String[]{"beq", "rs", "zero", "imm"});
		put("bnez", new String[]{"bne", "rs", "zero", "imm"});
		put("blez", new String[]{"bge", "zero", "rs", "offset"});
		put("bgez", new String[]{"bge", "rs", "zero", "offset"});
		put("bltz", new String[]{"blt", "rs", "zero", "offset"});
		put("bgtz", new String[]{"blt", "zero", "rs", "offset"});

		put("bgt", new String[]{"blt", "rt", "rs", "offset"});
		put("ble", new String[]{"bge", "rt", "rs", "offset"});
		put("bgtu", new String[]{"bltu", "rt", "rs", "offset"});
		put("bleu", new String[]{"bgeu", "rt", "rs", "offset"});

		put("j", new String[]{"jal", "zero", "offset"});
		put("jal", new String[]{"jal", "ra", "offset"});
		put("ret", new String[]{"jalr", "zero", "0", "ra"});
	}};

	private static final Map<String, String[]> convertionMap = new HashMap<>() {{
		put("mv", new String[]{"rs", "rd"});
		put("nop", new String[]{});
		put("neg", new String[]{"rs", "rd"});

		put("beqz", new String[]{"rs", "imm"});
		put("bnez", new String[]{"rs", "imm"});
		put("blez", new String[]{"rs", "imm"});
		put("bgez", new String[]{"rs", "imm"});
		put("bltz", new String[]{"rs", "imm"});
		put("bgtz", new String[]{"rs", "imm"});

		put("bgt", new String[]{"rs", "rt", "imm"});
		put("ble", new String[]{"rs", "rt", "imm"});
		put("bgtu", new String[]{"rs", "rt", "imm"});
		put("bleu", new String[]{"rs", "rt", "imm"});

		put("j", new String[]{"imm"});
		put("jal", new String[]{"imm"});
		put("ret", new String[]{});
	}};

	public static String convert(String line) {
		// Split into parts
		String[] lineParts = line.trim().split("\\s+");
		String mnemonic = lineParts[0];

		String[] pseudoParts = opcodeMap.get(mnemonic);
		String[] conversionParts = convertionMap.get(mnemonic);

		if (pseudoParts == null)
			throw new IllegalArgumentException("Cannot convert \"" + line + "\", as it is not a pseudo opcode.");

		// Extract operands from input (ignore mnemonic)
		String[] operands = Arrays.copyOfRange(lineParts, 1, lineParts.length);

		// Map named operands like "rd", "rs"
		Map<String, String> operandMap = new HashMap<>();
		for (int i = 0; i < conversionParts.length; i++) {
			operandMap.put(conversionParts[i], operands[i]);
		}

		// Build output
		ArrayList<String> output = new ArrayList<>();
		for (String part : pseudoParts) {
			output.add(operandMap.getOrDefault(part, part));
		}

		return String.join(" ", output);
	}

	public static boolean operationExists(String operation) {
		operation = operation.toLowerCase();
		return PseudoOpcodes.opcodeMap.get(operation) != null;
	}
}
