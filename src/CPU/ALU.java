package CPU;

public class ALU {

    //==================================================================================================================
    // Operation codes, both mnemonics and numbers; get index from these
    //==================================================================================================================
    public static final String[] opCodesMnemonics = {
            "add", "sub", "and", "or", "xor",
            "nand", "nor", "xnor", "bsh"
    };

    public static final byte[] opCodes = {
            2, 3, 4, 5, 6,
            7, 8, 9, 10
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
    // Run the CPU.ALU, get the operation and run the proper operation
    //==================================================================================================================
    public byte run(byte opCode, byte input_a, byte input_b) {
        return runOperation(getOpcode(opCode), input_a, input_b);
    }

    public byte run(String mnemonic, byte input_a, byte input_b) {
        return runOperation(getOpcode(mnemonic), input_a, input_b);
    }

    private byte runOperation(int operation, byte input_a, byte input_b) {
        // Refer to the opCodes array for the operation value
        // Doing it this way makes it really easy to add new operations
        return switch (operation) {
            case 0 -> add(input_a, input_b);
            case 1 -> sub(input_a, input_b);
            case 2 -> and(input_a, input_b);
            case 3 -> or(input_a, input_b);
            case 4 -> xor(input_a, input_b);
            case 5 -> nand(input_a, input_b);
            case 6 -> nor(input_a, input_b);
            case 7 -> xnor(input_a, input_b);
            case 8 -> bsh(input_a, input_b);
            default -> 0;
        };
    }

    //==================================================================================================================
    // Operations
    //==================================================================================================================
    private byte add(byte input_a, byte input_b) {
        return (byte) (input_a + input_b);
    }

    private byte sub(byte input_a, byte input_b) {
        return (byte) (input_a - input_b);
    }

    private byte and(byte input_a, byte input_b) {
        return (byte) (input_a & input_b);
    }

    private byte or(byte input_a, byte input_b) {
        return (byte) (input_a | input_b);
    }

    private byte xor(byte input_a, byte input_b) {
        return (byte) (input_a ^ input_b);
    }

    private byte nand(byte input_a, byte input_b) {
        return (byte) ~(input_a & input_b);
    }

    private byte nor(byte input_a, byte input_b) {
        return (byte) ~(input_a | input_b);
    }

    private byte xnor(byte input_a, byte input_b) {
        return (byte) ~(input_a ^ input_b);
    }

    private byte bsh(byte input_a, byte input_b) {
        boolean down = (input_b & 0b00001000) != 0; // The hardware looks at this bit to determine if the shift is up or down
        input_b = (byte) (input_b & 0b00000111); // The hardware will only be able to shift up or down 3 bits
        if (down) {
            return (byte) (input_a >> input_b);
        }
        return (byte) (input_a << input_b);
    }

}
