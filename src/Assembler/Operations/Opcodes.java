package Assembler.Operations;

import java.util.HashMap;
import java.util.Map;

public class Opcodes {

    public static final Map<String, int[]> immediateMap = new HashMap<>() {{
        put("ima", new int[]{-1, 128, 24});
        put("imb", new int[]{-1, 64, 24});
    }};

    public static final Map<String, int[]> opcodeMap = new HashMap<>() {{
        // "OPERATION", [ARGUMENT COUNT, OP VALUE, SHIFT]
        put("nop", new int[]{0, 0, 24});
        put("hlt", new int[]{0, 1, 24});
        put("add", new int[]{3, 2, 24});
        put("sub", new int[]{3, 3, 24});
        put("and", new int[]{3, 4, 24});
        put("or", new int[]{3, 5, 24});
        put("xor", new int[]{3, 6, 24});
        put("nand", new int[]{3, 7, 24});
        put("nor", new int[]{3, 8, 24});
        put("xnor", new int[]{3, 9, 24});
        put("bsh", new int[]{3, 10, 24});

        put("goto", new int[]{1, 16, 24});
        put("eql", new int[]{3, 17, 24});
        put("grt", new int[]{3, 18, 24});
        put("lst", new int[]{3, 19, 24});
        put("gre", new int[]{3, 20, 24});
        put("lse", new int[]{3, 21, 24});
        put("neq", new int[]{3, 22, 24});

        put("str", new int[]{2, 24, 24}); // STORE (ram)
        put("lod", new int[]{2, 25, 24}); // LOAD (ram)
        put("cal", new int[]{1, 26, 24}); // CALL (stack)
        put("ret", new int[]{0, 27, 24}); // RETURN (stack)
        put("out", new int[]{3, 28, 24}); // IO OUTPUT
        put("in", new int[]{2, 29 + 128, 24}); // IO INPUT
    }};

    //==================================================================================================================
    // GET OPERATION
    //==================================================================================================================

    public static int[] getOperation(String operation) {
        if (opcodeMap.containsKey(operation)) {
            return opcodeMap.get(operation);
        } else if (immediateMap.containsKey(operation)) {
            return immediateMap.get(operation);
        } else if (SudoOpcodes.sudoOpcodes.containsKey(operation)) {
            return SudoOpcodes.buildOperation(operation);
        }
        return null;
    }
}
