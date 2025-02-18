package CPU;

public class ProgramCounter {

    // TODO: Program counter is changing... class needs reworking.
    private int PROGRAM_COUNTER = 0;

    public void set(int value) {
        PROGRAM_COUNTER = value;
        PROGRAM_COUNTER = PROGRAM_COUNTER & CPUSpecs.romAddressSpaceMask;
    }

    public void increment() {
        PROGRAM_COUNTER++;
        PROGRAM_COUNTER = PROGRAM_COUNTER & CPUSpecs.romAddressSpaceMask;
    }

    public int getProgramCounter() {
        return PROGRAM_COUNTER;
    }

}
