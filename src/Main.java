import CPU.CPUExecutionCycle;

public class Main {
	public static void main(String[] args) {

		String programPath = "src\\Programs_V1_0\\bouncingBall.as";

		int speed = 10;
		int[] program = Assembler.Build.build(programPath);

		CPUExecutionCycle CPUExecutionCycle = new CPUExecutionCycle(program, speed);
		CPUExecutionCycle.loop();
	}
}