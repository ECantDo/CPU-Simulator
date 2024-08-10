package CPU;

public class CLU {
    /**
     * CPU.CLU stands for Conditional Logic Unit
     */

    //==================================================================================================================
    //  Operation codes, both mnemonics and numbers; get index from these
    //==================================================================================================================
    public static final String[] opCodesMnemonics = {
            "goto", "eql", "grt", "lst", "gre", "lse", "neq"
    };

    public static final byte[] opCodes = {
            16, 17, 18, 19, 20, 21, 22
    };

    public static int getOpcode(byte opcode) {
        for (int i = 0; i < opCodes.length; i++) {
            if (opCodes[i] == opcode) {
                return i;
            }
        }
        return -1;
    }

    public static int getOpcode(String mnemonic) {
        for (int i = 0; i < opCodesMnemonics.length; i++) {
            if (opCodesMnemonics[i].equals(mnemonic)) {
                return opCodes[i];
            }
        }
        return -1;
    }

    //==================================================================================================================
    // Run the CPU.CLU, get the operation and run the proper operation
    //==================================================================================================================
    public boolean run(byte opCode, byte input_a, byte input_b) {
        return runOperation(getOpcode(opCode), input_a, input_b);
    }

    public boolean run(String mnemonic, byte input_a, byte input_b) {
        return runOperation(getOpcode(mnemonic), input_a, input_b);
    }

    private boolean runOperation(int operation, byte input_a, byte input_b) {
        // Refer to the opCodes array for the operation value
        // Doing it this way makes it really easy to add new operations
        return switch (operation) {
            case 0 -> goto_(input_a, input_b);
            case 1 -> eql(input_a, input_b);
            case 2 -> grt(input_a, input_b);
            case 3 -> lst(input_a, input_b);
            case 4 -> gre(input_a, input_b);
            case 5 -> lse(input_a, input_b);
            case 6 -> neq(input_a, input_b);
            default -> false;
        };
    }

    //==================================================================================================================
    // Operations
    //==================================================================================================================
    private boolean goto_(byte input_a, byte input_b) {
        return true;
    }

    private boolean eql(byte input_a, byte input_b) {
        return input_a == input_b;
    }

    private boolean grt(byte input_a, byte input_b) {
        return (int) input_a > (int) input_b;
    }

    private boolean lst(byte input_a, byte input_b) {
        return (int) input_a < (int) input_b;
    }

    private boolean gre(byte input_a, byte input_b) {
        return (int) input_a >= (int) input_b;
    }

    private boolean lse(byte input_a, byte input_b) {
        return (int) input_a <= (int) input_b;
    }

    private boolean neq(byte input_a, byte input_b) {
        return input_a != input_b;
    }
}
