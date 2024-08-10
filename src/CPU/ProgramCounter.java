package CPU;

public class ProgramCounter {
    private int PROGRAM_COUNTER = 0;

    public void set(int value) {
        PROGRAM_COUNTER = value;
        PROGRAM_COUNTER = PROGRAM_COUNTER & 0xFF;
    }

    public void increment() {
        PROGRAM_COUNTER++;
        PROGRAM_COUNTER = PROGRAM_COUNTER & 0xFF;
    }

    public int getProgramCounter() {
        return PROGRAM_COUNTER;
    }

}
