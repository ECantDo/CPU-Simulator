package CPU;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Registers {

    private final byte[] registers;

    private final int registerCount = 32;

    public Registers() {
        registers = new byte[registerCount];
    }

    public void set(byte index, byte value) {
        index = (byte) (index & 0b0001_1111);
        registers[index] = value;
    }

    public byte[] get(byte index_a, byte index_b) {
        index_a = (byte) (index_a & 0b0001_1111);
        index_b = (byte) (index_b & 0b0001_1111);
        return new byte[]{registers[index_a], registers[index_b]};
    }

    public String toString() {
        int[] output = new int[registerCount];
        for (int i = 0; i < registerCount; i++) {
            output[i] = registers[i] & 0xFF;
        }
        List<String> list = Arrays.stream(output).mapToObj(String::valueOf).collect(Collectors.toList());
        list.add(0, "REGISTERS:");
        return Arrays.toString(output);
    }
}
