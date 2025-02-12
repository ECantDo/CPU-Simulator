import CPU.ExecutionLoop;

public class Main {
    public static void main(String[] args) {

        String programPath = "src\\Programs_V1_1\\MandelbrotSet.as";

        int speed = 10;
        int[] program = Assembler.Build.build(programPath);

        ExecutionLoop executionLoop = new ExecutionLoop(program, speed);
        executionLoop.loop();
    }
}