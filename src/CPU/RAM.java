package CPU;

import java.util.Arrays;

public class RAM {


    private final int[] ram;// = new byte[256];

    public RAM() {
        ram = new int[CPUSpecs.ramAddressSpace];
    }


    public int get(int address) {
        return ram[address] & CPUSpecs.bitMask;
    }

    public void set(int address, int value) {
        ram[address] = value & CPUSpecs.bitMask;
    }


    public String toString() {
        return Arrays.toString(ram);
    }
}
