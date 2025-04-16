import CPU.CPUExecutionCycle;

public class Main {
	public static void main(String[] args) {

		String programPath = "src\\Programs_V2_0\\fib.as";

		// TODO; double check that it works as intended when finished with the build program
		int speed = 10;
		int[] program = Assembler.Build.build(programPath);
//		CPUExecutionCycle cpuExecutionCycle = new CPUExecutionCycle(program, speed);
//		CPUExecutionCycle.loop();
	}
}