package CPU;

public class ProgramCounter {

    // TODO: Program counter is changing... class needs reworking.
    private int PROGRAM_COUNTER = 0;

    public void setProgramCounter(int value){
        this.PROGRAM_COUNTER = value & CPUSpecs.romAddressSpaceMask;
    }

    public void increment(int amount) {
        this.PROGRAM_COUNTER += amount;
        this.PROGRAM_COUNTER = this.PROGRAM_COUNTER & CPUSpecs.romAddressSpaceMask;
    }

    public int getProgramCounter() {
        return this.PROGRAM_COUNTER;
    }

}
