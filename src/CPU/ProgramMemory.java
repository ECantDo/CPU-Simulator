package CPU;

public class ProgramMemory {

    public static int[] programMemory = new int[256];

    public static int[] program = {
            0b11000101000000010000000000000001,
            0b01000101000000010000000000000010,
            0b11000101000010110000000000000100,
            0b00010001000000000000010000001001,
            0b10000010000000010000000000000000,
            0b00000010000000010000001000000011,
            0b01000101000000100000000000000001,
            0b01000101000000110000000000000010,
            0b00010000000000000000000000000011,
            0b00000001000000000000000000000000
    };

    public ProgramMemory() {
        System.arraycopy(program, 0, programMemory, 0, program.length);
    }

    public int getInstruction(int address) {
        address = address & 0xFF;
        return programMemory[address];
    }
}
