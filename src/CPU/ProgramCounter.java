package CPU;

public class ProgramCounter {

	private int programCounter = 0;

	public void setProgramCounter(int value) {
		this.programCounter = value & CPUSpecs.romAddressSpaceMask;
	}

	public void increment(int amount) {
		this.programCounter = (this.programCounter + amount) & CPUSpecs.romAddressSpaceMask;
	}

	public int getProgramCounter() {
		return this.programCounter;
	}

	public void reset() {
		this.programCounter = 0;
	}

}
