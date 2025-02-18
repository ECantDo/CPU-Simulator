package CPU;

import java.util.Arrays;

public class RAM {


    private final int[] ram;// = new byte[256];

    public RAM() {
        ram = new int[CPUSpecs.ramAddressSpace];
    }


    public int get(int address) {
        return ram[address] & 0xFF;
    }

    public void set(int address, int value) {
        ram[address] = value & 0xFF;
    }


    public String toString() {
        return Arrays.toString(ram);
    }
}
