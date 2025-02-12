package Assembler;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import Assembler.Operations.*;

public class Build {

	public static void main(String[] args) {
		String filePath = "src\\program.as";

		int[] program = build(filePath);
		System.out.println("PROGRAM: " + Arrays.toString(program));
	}

	public static void runBuildToSchem(String filePath) {
		String directory = System.getProperty("user.dir");
//        System.out.println(directory);
		directory += "\\src\\Assembler\\runBuildToSchem.bat";
		String command = "cmd /c start ";

		try {
			ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", directory, filePath);
//            pb.directory(new File(directory));
			Process p = pb.start();
		} catch (IOException e) {
			System.err.println("Failed to run .bat\n" + e.getMessage());
		}
	}

	/**
	 * Logic that sequences the build cycle
	 *
	 * @param filePath The file path that contains the source code (custom .as file)
	 * @return An array of integers of the assembled program - custom machine code
	 */
	public static int[] build(String filePath) {
		if (filePath == null) {
			throw new IllegalArgumentException("File path cannot be null");
		}


		String[] fileContents = formatFile(readFile(filePath));
		Map<String, Integer> labels = getLabels(fileContents);
		Map<String, Integer> constants = getConstants(fileContents);

		replaceConstants(fileContents, constants);
		for (String line : fileContents) {
			System.out.println(line);
		}

		Integer[] instructions = assemble(fileContents, labels);
		try {
			File outputFile = new File(filePath.substring(0, filePath.lastIndexOf('.')) + ".bin");
			if (outputFile.createNewFile()) {
				System.out.println("Created new file");
			}

			FileWriter writer = new FileWriter(outputFile);
			for (Integer instruction : instructions) {
				writer.write("0b" + String.format("%32s", Integer.toBinaryString(instruction)).replace(' ', '0') + ",\n");
			}
			writer.close();
			System.out.println("Wrote to output file: " + outputFile.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Arrays.stream(instructions).mapToInt(i -> i).toArray();
	}

	/**
	 * Main assembler logic.  Converts the file into an array of integers
	 *
	 * @param fileContents The contents of the file as a String array
	 * @param labels       The labels in the file
	 * @return An array of integers of the assembled program
	 */
	private static Integer[] assemble(String[] fileContents, Map<String, Integer> labels) {
		if (fileContents == null) {
			throw new IllegalArgumentException("File Contents Array cannot be null");
		}
		if (labels == null) {
			throw new IllegalArgumentException("Labels Map cannot be null");
		}

		System.out.println(labels);

		ArrayList<Integer> instructions = new ArrayList<>();

		for (int lineNumber = 0; lineNumber < fileContents.length; lineNumber++) {
			String line = fileContents[lineNumber];

			if (line.isBlank()) {
				continue;
			}

			if (line.startsWith("const ")) {
				continue;
			}

			int operationValue = 0;
			if (line.charAt(0) == ':') { // Skip labels
				continue;
			}

			String[] parts = line.split(" ");
//            System.out.println("Parts: " + Arrays.toString(parts));

			int numberOfImmediateValues = 0;
			boolean imm_a = false;
			boolean imm_b = false;
			for (String part : parts) { // GET IMMEDIATEs
				int[] opData = Opcodes.getOperation(part);
				if (opData == null) {
					System.err.println("Invalid operation: " + part + "\nLine: " + line);
					System.exit(-1);
				}

				if (Opcodes.immediateMap.containsKey(part)) {
					operationValue |= Opcodes.immediateMap.get(part)[1] << Opcodes.immediateMap.get(part)[2];
					if (part.equals("imb")) {
						imm_b = true;
					} else if (part.equals("ima")) {
						imm_a = true;
					}
					numberOfImmediateValues++;
					continue;
				}
				break;
			}
			String operation = parts[numberOfImmediateValues];
//            System.out.println("Operation: " + operation);

			int[] opData = Opcodes.getOperation(operation);

//            System.out.println("OpData: " + Arrays.toString(opData));
//            System.out.println("There is an immediate value A: " + imm_a + " B: " + imm_b);

			if (opData == null) {
				System.err.println("Invalid operation: " + operation + "\nLine: " + line);
				System.exit(-1);
			}
			if (opData[0] != parts.length - numberOfImmediateValues - 1) {

				System.err.println("Invalid operation: '" + operation + "'\nInvalid number of arguments, " + "expected " + opData[0] + " got " + (parts.length - numberOfImmediateValues - 1) + "\nLine: " + line);
				System.exit(-1);
			}

			// Opdata immediate values; set the immediate value if the opcode has the immediate bit set
			if ((opData[0] & 128) == 128) {
				imm_a = true;
			}
			if ((opData[0] & 64) == 64) {
				imm_b = true;
			}

			operationValue |= opData[1] << opData[2];

			int length = parts.length - numberOfImmediateValues - 1;
			String[] non_imm_execution = Arrays.copyOfRange(parts, numberOfImmediateValues + 1, parts.length);
//            System.out.println(Arrays.toString(non_imm_execution));
			if (opData[0] == 1) {

				String value = non_imm_execution[0];
				int registerValue = Registers.getRegister(value);
				if (registerValue != -1) {
					operationValue |= Registers.getRegister(value);
				} else if (labels.containsKey(value)) {
					operationValue |= labels.get(value);
				} else {
					operationValue |= parseInt(value);
				}
			} else if (opData[0] == 2) {
				String value = non_imm_execution[0];
				int shiftAmount;
				if (operation.equals("lod")) {
					shiftAmount = 8;
				} else {
					shiftAmount = 16;
				}
				if (imm_a) {
					try {
						operationValue |= parseInt(value) << shiftAmount;
					} catch (NumberFormatException e) {
						System.err.println("Value '" + value + "' is not a number, expected an immediate value." + "\nLine: " + line);
						System.exit(-1);
					}
				} else {
					int registerValue = Registers.getRegister(value);
					if (registerValue != -1) {
						operationValue |= Registers.getRegister(value) << shiftAmount;
					} else {
						System.err.println("Register " + value + " not found, " + value + " might not be a register" + "\nLine: " + line);
						System.exit(-1);
					}
				}
				if (operation.equals("not")) {
					operationValue |= ((operationValue & (0xFF << 16)) >> 8); // Take the value from byte 01, and put it in byte 02
				}

				value = non_imm_execution[1];
				if (operation.equals("str")) {
					shiftAmount = 8;
				} else {
					shiftAmount = 0;
				}
				int registerValue = Registers.getRegister(value);
				if (registerValue != -1) {
					operationValue |= Registers.getRegister(value) << shiftAmount;
				} else if (labels.containsKey(value)) {
					operationValue |= labels.get(value) << shiftAmount;
				} else {
					operationValue |= parseInt(value) << shiftAmount;
				}
			} else if (opData[0] == 3) {

				String value = non_imm_execution[0];
				if (imm_a) {
					try {
						operationValue |= parseInt(value) << 16;
					} catch (NumberFormatException e) {
						System.err.println("Value " + value + " is not a number, expected an immediate\nLine: " + line);
						System.exit(-1);
					}
				} else {
					int registerValue = Registers.getRegister(value);
					if (registerValue != -1) {
						operationValue |= Registers.getRegister(value) << 16;
					} else {
						System.err.println("Register " + value + " not found, " + value + " might not be a register" + "\nLine: " + line);
						System.exit(-1);
					}
				}

				value = non_imm_execution[1];
				if (imm_b) {
					try {
						operationValue |= parseInt(value) << 8;
					} catch (NumberFormatException e) {
						System.err.println("Value " + value + " is not a number, expected an immediate\nLine: " + line);
						System.exit(-1);
					}
				} else {
					if (operation.equals("rsh") || operation.equals("lsh")) {
						System.err.println("Operation " + operation + " requires an immediate value, not a register" + "\nLine: " + line);
						System.exit(-1);
					}

					int registerValue = Registers.getRegister(value);
					if (registerValue != -1) {
						operationValue |= Registers.getRegister(value) << 8;
					} else {
						System.err.println("Register " + value + " not found, " + value + " might not be a register" + "\nLine: " + line);
						System.exit(-1);
					}
				}

				value = non_imm_execution[2];
				int registerValue = Registers.getRegister(value);
				if (registerValue != -1) {
					operationValue |= Registers.getRegister(value);
				} else if (labels.containsKey(value)) {
					operationValue |= labels.get(value);
				} else {
					operationValue |= parseInt(value);
				}
			}

			instructions.add(operationValue);
		}
		return instructions.toArray(new Integer[0]);
	}


	public static int parseInt(String s) {
		if (s == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		s = s.trim();
		if (s.isEmpty()) {
			throw new IllegalArgumentException("Value cannot be empty");
		}

		int base = 10;
		if (s.startsWith("0x") || s.startsWith("0X")) {
			s = s.substring(2);
			base = 16;
		} else if (s.startsWith("0b") || s.startsWith("0B")) {
			s = s.substring(2);
			base = 2;
		} else if (s.startsWith("0") && s.length() > 1) {
			s = s.substring(1);
			base = 8;
		}

		try {
			int value = Integer.parseInt(s, base);
			if (value > 255) {
				throw new IllegalArgumentException("Value '" + s + "' is too large");
			}
			return value;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Value '" + s + "' is not a number");
		}
	}

	/**
	 * Returns a map of labels and their addresses
	 *
	 * @param fileContents the contents of the file, formatted
	 */
	private static Map<String, Integer> getLabels(String[] fileContents) {
		if (fileContents == null) {
			throw new IllegalArgumentException("File Contents Array cannot be null");
		}

		Map<String, Integer> labels = new HashMap<>();
		int address = 0;
		for (String line : fileContents) {
			if (line.isEmpty()) {
				continue;
			}
			if (line.charAt(0) == ':') {
				labels.put(line, address);
				continue;
			}
			address++;
		}
		return labels;
	}

	/**
	 * Gets the constant values from the file, and returns them in a map
	 *
	 * @param fileContents The .as file contents
	 * @return The map of constants <String Value, Integer Value>
	 */
	private static Map<String, Integer> getConstants(String[] fileContents) {
		if (fileContents == null) {
			throw new IllegalArgumentException("File Contents Array cannot be null");
		}

		Map<String, Integer> constants = new HashMap<>();
		for (int i = 0; i < fileContents.length; i++) {
			String line = fileContents[i].trim();
			if (line.isEmpty()) {
				continue;
			}
			if (!line.startsWith("const ")) {
				continue;
			}
			String[] split = line.split(" ");

			if (Opcodes.getOperation(split[1]) != null) {
				System.err.println("Constant \"" + split[1] + "\" is a valid opcode, not a constant; Constants cannot be " +
						"opcodes" + "\nLine: " + line + " (" + i + ")");
				System.exit(-1);
			}
			try {
				constants.put(split[1], parseInt(split[2]));
			} catch (NumberFormatException e) {
				System.err.println("Value " + split[2] + " is not a number, expected a constant\nLine: " + line +
						" (" + i + ")");
				System.exit(-1);
			}
		}
		return constants;
	}

	public static void replaceConstants(String[] fileContents, Map<String, Integer> constants) {
		if (fileContents == null) {
			throw new IllegalArgumentException("File Contents Array cannot be null");
		}

		for (Map.Entry<String, Integer> entry : constants.entrySet()) {
			for (int i = 0; i < fileContents.length; i++) {
				String line = fileContents[i].trim();
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("const ")) {
					continue;
				}
				String[] split = line.split(" ");
				for (int j = 0; j < split.length; j++) {
					if (split[j].equals(entry.getKey())) {
						fileContents[i] = fileContents[i].replace(split[j], Integer.toString(entry.getValue()));
					}
				}
			}
		}
	}


	/**
	 * Removes comments and in-line comments from the file.  Keeps the number of lines the same
	 *
	 * @param fileContents The contents of the file as a String array
	 * @return The formatted file as a String array
	 */
	private static String[] formatFile(String[] fileContents) {
		if (fileContents == null) {
			throw new IllegalArgumentException("File Contents Array cannot be null");
		}

		String[] lines = new String[fileContents.length];
		for (int i = 0; i < fileContents.length; i++) {
			String line = fileContents[i];
			line = line.trim();
			if (line.startsWith("//")) { // Remove comments
				line = "";
			}
			line = line.split("//")[0]; // Remove in-line comments
			lines[i] = line;
		}
		return lines;
	}

	/**
	 * Reads from a .as file and returns an array of strings containing the contents of the file.
	 *
	 * @param filePath The path to the .as file
	 * @return An array of strings containing the contents of the file
	 */
	private static String[] readFile(String filePath) {
		if (filePath == null) {
			throw new IllegalArgumentException("File path cannot be null");
		}

		try {
			File file = new File(filePath);
			Scanner myReader = new Scanner(file);
			ArrayList<String> lines = new ArrayList<>();
			while (myReader.hasNextLine()) {
				lines.add(myReader.nextLine());
			}
			myReader.close();
			return lines.toArray(new String[0]);
		} catch (FileNotFoundException e) {
			System.err.println("An error occurred. File not found.");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
}
