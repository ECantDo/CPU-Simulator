package CPU;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Registers {

    private final int[] registers;

    public Registers() {
        registers = new int[CPUSpecs.registerCount];
    }

    public void set(int index, int value) {
        registers[index & CPUSpecs.registerMask] = value & CPUSpecs.bitMask;
    }

    public int[] get(int index_a, int index_b) {
        index_a = (index_a & CPUSpecs.registerMask);
        index_b = (index_b & CPUSpecs.registerMask);
        return new int[]{registers[index_a], registers[index_b]};
    }

    public String toString() {
        int[] output = new int[CPUSpecs.registerCount];
        for (int i = 0; i < CPUSpecs.registerCount; i++) {
            output[i] = registers[i] & 0xFF;
        }
        List<String> list = Arrays.stream(output).mapToObj(String::valueOf).collect(Collectors.toList());
        list.add(0, "REGISTERS:");
        return Arrays.toString(output);
    }
}
