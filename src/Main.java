import CPU.ExecutionLoop;

public class Main {
    public static void main(String[] args) {

        String programPath = "src\\testing.as";

        int speed = 10;
        int[] program = Assembler.Build.build(programPath);

        ExecutionLoop executionLoop = new ExecutionLoop(program, speed);
        executionLoop.loop();
    }
}