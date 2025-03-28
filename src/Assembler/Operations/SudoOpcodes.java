package Assembler.Operations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SudoOpcodes {

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
		put("ret", new String[]{"jalr", "zero", "ra", "0"});
	}};
}
