package Assembler.Operations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SudoOpcodes {

	private static final Character[] operations = {
			'|', // or
			'&', // and
			'^', // xor
	};

	public static final Map<String, Object[]> sudoOpcodes = new HashMap<>() {{
		// "OPERATION", [ARGUMENT COUNT, SUB OPERATIONS...]
		put("cpy", new Object[]{2, "imb", "or"});
		put("not", new Object[]{2, "nor"});

		put("lsh", new Object[]{3, "bsh", "imb"});
		put("rsh", new Object[]{3, "bsh", "imb", "|2048"});

		put("uscr", new Object[]{0, "ima", "imb", "out", "|49152"});
		put("cscr", new Object[]{0, "ima", "imb", "out", "|32768"});
	}};

	/**
	 * Gets the operation from the sudoOpcodes map
	 *
	 * @param opcode The opcode to get
	 * @return [Argument Count, Operation int value]
	 */
	public static int[] buildOperation(String opcode) {

		if (!sudoOpcodes.containsKey(opcode)) {
			return null;
		}

		Object[] args = sudoOpcodes.get(opcode);
		int operationValue = 0;

		for (int i = 1; i < args.length; i++) {
			if (Arrays.asList(operations).contains(((String) args[i]).charAt(0))) {
				char operation = ((String) args[i]).charAt(0);
				int value = Integer.parseInt(((String) args[i]).substring(1));
				switch (operation) {
					case '|':
						operationValue |= value;
						break;
					case '&':
						operationValue &= value;
						break;
					case '^':
						operationValue ^= value;
						break;
				}
				continue;
			}

			int[] operation = Opcodes.getOperation((String) args[i]);
			if (operation == null) {
				return null;
			}
			operationValue |= operation[1] << operation[2];
		}

		return new int[]{(int) args[0], operationValue, 0};
	}
}
