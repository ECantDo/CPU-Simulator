package CPU;

import java.util.Arrays;

public class RAM {


    private int[] ram;// = new byte[256];

    public RAM() {
        int byteCount = 8;
        ram = new int[(int) Math.pow(2, byteCount)];
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
