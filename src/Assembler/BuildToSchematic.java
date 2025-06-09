package Assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BuildToSchematic {

	public static void main(String[] args) {
		String file_name = "src/Programs_V2_0/fib";
		int[] program = Build.build(file_name + ".as");
		writeToAsciiBinary(file_name, program);
//		Build.runBuildToSchem(file_name + ".bin");
	}

	private static void writeToAsciiBinary(String file, int[] program) {
		file += ".bin";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (int i : program) {
				writer.write(String.format("%32s", Integer.toBinaryString(i)).replace(' ', '0'));
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
