import CPU.ExecutionLoop;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        String programPath = "." + File.separator + "src" + File.separator + "fib.as";
        System.out.println(programPath);

        int speed = 10;
        int[] program = Assembler.Build.build(programPath);

        ExecutionLoop executionLoop = new ExecutionLoop(program, speed);
        executionLoop.loop();
    }
}