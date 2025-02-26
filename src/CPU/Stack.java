package CPU;

public class Stack {

    private int[] stack;
    private int stackPointer;

    public Stack() {
        stack = new int[16];
        stackPointer = 0;
    }

    public void push(int value) {
        try {
            stack[stackPointer++] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new StackOverflowError("Custom Stack Overflow Error");
        }
    }

    public int pop() {
        return stack[--stackPointer];
    }
}
