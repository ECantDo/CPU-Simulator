import CPU.ExecutionLoop;

public class Main {
    public static void main(String[] args) {

        String programPath = "src\\division.as";

        int[] program = Assembler.Build.build(programPath);

        ExecutionLoop executionLoop = new ExecutionLoop(program);
        executionLoop.loop();
    }
}