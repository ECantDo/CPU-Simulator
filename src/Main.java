//import CPU.ExecutionLoop;

import Assembler.Build;
import Assembler.Operations.PseudoOpcodes;

public class Main {
	public static void main(String[] args) {

		System.out.println(Build.build("src\\Programs_V2_0\\fib.as"));


//        String programPath = "src\\smile.as";

//        int speed = 10;
//        int[] program = Assembler.Build.build(programPath);

//        ExecutionLoop executionLoop = new ExecutionLoop(program, speed);
//        executionLoop.loop();
	}
}