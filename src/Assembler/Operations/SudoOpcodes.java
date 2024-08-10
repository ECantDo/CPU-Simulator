package Assembler.Operations;

import java.util.HashMap;
import java.util.Map;

public class SudoOpcodes {

    public static final Map<String, Object[]> sudoOpcodes = new HashMap<>() {{
        // "OPERATION", [ARGUMENT COUNT, SUB OPERATIONS...]
        put("cpy", new Object[]{2, "imb", "or"});
        put("not", new Object[]{2, "nor"});
    }};

    public static int[] buildOperation(String opcode) {
        /**
         * Gets the operation from the sudoOpcodes map
         * @param operation
         * @return [Argument Count, Operation int value]
         */
        if (!sudoOpcodes.containsKey(opcode)) {
            return null;
        }

        Object[] args = sudoOpcodes.get(opcode);
        int operationValue = 0;

        for (int i = 1; i < args.length; i++) {
            int[] operation = Opcodes.getOperation((String) args[i]);
            if (operation == null) {
                return null;
            }
            operationValue |= operation[1] << operation[2];
        }

        return new int[]{(int) args[0], operationValue, 0};
    }
}
