package Assembler.Operations;

import java.util.HashMap;
import java.util.Map;

public class Opcodes {

    public static final Map<String, int[]> opcodeMap = new HashMap<>() {{
        // "OPERATION", [ARGUMENT COUNT, OP VALUE]
        put("hlt",   new int[] {0, 0});

        put("add",   new int[] {3, 1  });
        put("sub",   new int[] {3, 33 });
        put("xor",   new int[] {3, 65 });
        put("or",    new int[] {3, 97 });
        put("and",   new int[] {3, 129});
        put("xnor",  new int[] {3, 161});
        put("nor",   new int[] {3, 193});
        put("nand",  new int[] {3, 225});
        put("sl",    new int[] {3, 2  });
        put("sra",   new int[] {3, 34 });
        put("sr",    new int[] {3, 66 });

        put("addi",  new int[] {3, 17 });
        put("addf",  new int[] {3, 3  });
        put("subi",  new int[] {3, 49 });
        put("li",    new int[] {2, 25 });
        put("xori",  new int[] {3, 81 });
        put("ori",   new int[] {3, 113});
        put("andi",  new int[] {3, 145});
        put("xnori", new int[] {3, 177});
        put("nori",  new int[] {3, 209});
        put("nandi", new int[] {3, 241});
        put("sli",   new int[] {3, 18 });
        put("srai",  new int[] {3, 50 });
        put("sri",   new int[] {3, 82 });

        put("beq",   new int[] {3, 4  });
        put("bne",   new int[] {3, 36 });
        put("blt",   new int[] {3, 68 });
        put("bge",   new int[] {3, 100});
        put("bltu",  new int[] {3, 132});
        put("bgeu",  new int[] {3, 164});

        put("jal",   new int[] {2, 5  });
        put("jalr",  new int[] {3, 6  });

        put("lod",   new int[] {3, 7  });
        put("str",   new int[] {3, 39 });

        put("in",    new int[] {2, 8  });
        put("out",   new int[] {2, 40 });
    }};

    //==================================================================================================================
    // GET OPERATION
    //==================================================================================================================

    public static int[] getOperation(String operation) {
        if (operation == null)
            throw new IllegalArgumentException("Cannot get a null operation");

        if (opcodeMap.containsKey(operation)) {
            return opcodeMap.get(operation);
        } else if (SudoOpcodes.sudoOpcodes.containsKey(operation)) {
            return SudoOpcodes.buildOperation(operation);
        }
        return null;
    }
}
