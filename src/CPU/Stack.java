package CPU;

public class Stack {

    private int[] stack;
    private int stackPointer;

    public Stack() {
        stack = new int[32];
        stackPointer = 0;
    }

    public void push(int value) {
        stack[stackPointer] = value;
        stackPointer++;
    }

    public int pop() {
        stackPointer--;
        return stack[stackPointer];
    }
}
