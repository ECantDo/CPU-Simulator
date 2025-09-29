package Assembler.Operations;

import java.util.HashMap;
import java.util.Map;

public class Opcodes {

	private static final Map<String, int[]> opcodeMap = new HashMap<>() {{
		// "OPERATION", [ARGUMENT COUNT, OP VALUE]
		put("hlt", new int[]{0, 0});

		put("add", new int[]{3, 1});
		put("sub", new int[]{3, 33});
		put("xor", new int[]{3, 65});
		put("or", new int[]{3, 97});
		put("and", new int[]{3, 129});
		put("xnor", new int[]{3, 161});
		put("nor", new int[]{3, 193});
		put("nand", new int[]{3, 225});
		put("sl", new int[]{3, 2});
		put("sra", new int[]{3, 34});
		put("sr", new int[]{3, 66});

		put("addi", new int[]{3, 17});
		put("addf", new int[]{3, 3});
		put("subi", new int[]{3, 49});
		put("li", new int[]{2, 25});
		put("xori", new int[]{3, 81});
		put("ori", new int[]{3, 113});
		put("andi", new int[]{3, 145});
		put("xnori", new int[]{3, 177});
		put("nori", new int[]{3, 209});
		put("nandi", new int[]{3, 241});
		put("sli", new int[]{3, 18});
		put("srai", new int[]{3, 50});
		put("sri", new int[]{3, 82});

		put("beq", new int[]{3, 4});
		put("bne", new int[]{3, 36});
		put("blt", new int[]{3, 68});
		put("bge", new int[]{3, 100});
		put("bltu", new int[]{3, 132});
		put("bgeu", new int[]{3, 164});

		put("jal", new int[]{2, 5});
		put("jalr", new int[]{3, 6});

		put("lod", new int[]{3, 39});
		put("str", new int[]{3, 7});

		put("in", new int[]{2, 8});
		put("out", new int[]{2, 40});
	}};

	private static final Map<String, Integer> immediateMap = new HashMap<>() {{
		// TODO; figure out what the heck I was thinking here
		// "OPERATION",
		put("hlt", 0);

		put("add", 0);
		put("sub", 0);
		put("xor", 0);
		put("or", 0);
		put("and", 0);
		put("xnor", 0);
		put("nor", 0);
		put("nand", 0);
		put("sl", 0);
		put("sra", 0);
		put("sr", 0);

		put("addi", 0b0100); // addi s0 ____ s2
		put("addf", 0);
		put("subi", 0b0010);
		put("li", 0b010);
		put("xori", 0b0100);
		put("ori", 0b0100);
		put("andi", 0b0100);
		put("xnori", 0b0100);
		put("nori", 0b0100);
		put("nandi", 0b0100);
		put("sli", 0b0100);
		put("srai", 0b0100);
		put("sri", 0b0100);

		put("beq", 0b1000);
		put("bne", 0b1000);
		put("blt", 0b1000);
		put("bge", 0b1000);
		put("bltu", 0b1000);
		put("bgeu", 0b1000);

		put("jal", 0b100);
		put("jalr", 0b0100);

		put("lod", 0b0100);
		put("str", 0b0100);

		put("in", 0b010);
		put("out", 0b100);
	}};

	//==================================================================================================================
	// GET OPERATION
	//==================================================================================================================

	public static int[] generateOperation(String operation) {
		if (operation == null)
			throw new IllegalArgumentException("Cannot get a null operation");
		operation = operation.toLowerCase();
		return opcodeMap.get(operation);
	}

	public static boolean operationExists(String operation) {
		operation = operation.toLowerCase();
		boolean exists = opcodeMap.get(operation) != null;
		if (exists)
			return true;
		return PseudoOpcodes.opcodeMap.get(operation) != null;
	}

	public static int getImmediateMap(String operation) {
		operation = operation.toLowerCase();
		return immediateMap.get(operation);
	}
}
