package CPU;

public class ProgramMemory {

    public static int[] programMemory = new int[256];

    public static int[] program = {

    };

    public ProgramMemory(int[] program) {
        System.arraycopy(program, 0, programMemory, 0, Math.min(program.length, programMemory.length));
    }

    public ProgramMemory() {
        this(program);
    }

    public int getInstruction(int address) {
        address = address & 0xFF;
        return programMemory[address];
    }
}
