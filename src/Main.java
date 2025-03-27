import CPU.CPUExecutionCycle;

public class Main {
	public static void main(String[] args) {

		String programPath = "src\\Programs_V1_0\\bouncingBall.as";

		// TODO; double check that it works as intended when finished with the build program
		int speed = 10;
//		int[] program = Assembler.Build.build(programPath);
		int[] program = {0};
		CPUExecutionCycle CPUExecutionCycle = new CPUExecutionCycle(program, speed);
		CPUExecutionCycle.loop();
	}
}