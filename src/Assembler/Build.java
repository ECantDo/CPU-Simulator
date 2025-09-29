package Assembler;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import Assembler.Exceptions.OpcodeExistsException;
import Assembler.Operations.*;
import CPU.CPUSpecs;

public class Build {

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
		System.out.println("Replaced Constants:");
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

		int lineNumber = 0;

		for (String line : fileContents) {
			// Contents already formatted.
//			String line = fileContents[lineNumber];

			if (line.isBlank()) {
				continue;
			}

			if (line.startsWith("def ")) {
				continue;
			}

			String[] parts = line.split(" ");
//            System.out.println("Parts: " + Arrays.toString(parts));

			if (PseudoOpcodes.operationExists(parts[0])) {
				parts = PseudoOpcodes.convert(line).split(" ");
			}

			String mnemonic = parts[0];

			if (mnemonic.charAt(mnemonic.length() - 1) == ':') { // Skip labels
				continue;
			}

			// [arg count, opcode value]
			int[] args = Opcodes.generateOperation(mnemonic);
			if (args == null) throw new OpcodeExistsException("Opcode \"" + mnemonic + "\" does not exist");

			if (parts.length - 1 != args[0]) {
				throw new IllegalArgumentException("Expected argument count does not match received count\n-> " + line);
			}

			int immediatePosition = Opcodes.getImmediateMap(mnemonic);

			int[] instructionValues = new int[parts.length];
			int currentPosition = 1;
			int immediateValue = 0;

			// Convert the words into number :3
			for (int i = 0; i < instructionValues.length; i++) {
				String part = parts[i];
				if ((currentPosition & immediatePosition) != 0) {
					int labelLineNumber = labels.getOrDefault(part, -1);
					if (labelLineNumber >= 0) {
						immediateValue = labelLineNumber - lineNumber - 1;
//						System.err.println("Imm is label -> " + labelLineNumber + " - " + lineNumber + " = " + immediateValue);
					} else {
						immediateValue = parseInt(part);
					}
				} else if (i == 0) { // If is the opcode position, use the value already gotten for it
					instructionValues[i] = args[1];
				} else { // Should otherwise be a register
					instructionValues[i] = Registers.getRegisterValue(part);
				}

				currentPosition <<= 1;
			}

			// Put all the bytes into the correct location(s)

			int operationValue = 0;

			operationValue |= instructionValues[0]; // Opcode always takes the first 8 bits

			switch (operationValue & 0b11111) {
				case 0: // Halt
					// Nothing needs to happen here, operationValue = 0 for halt
					break;
				// Without immediate...
				case 1: // ALU (add, sub, xor, and, or, ...)
				case 2: // Barrel Shifter
				case 3: // Add with flags
					// RD, first 5 bits of byte 2
					operationValue |= (instructionValues[3] & 0b11111) << 8;

					// RS1, last 3 bits of byte 2, first 2 bits of byte 3
					operationValue |= (instructionValues[1] & 0b11111) << 13;

					// RS2, 5 bits of byte 3, starting 3 bits in
					operationValue |= (instructionValues[2] & 0b11111) << 18;
					break;
				case 5: //  Jump and link
				case 25: // Load Immediate
					// RD, first 5 bits of byte 2
					operationValue |= (instructionValues[2] & 0b11111) << 8;

					// bit 14 & 15 -> first 2 bits of byte 3
					operationValue |= ((immediateValue >> 14) & 0b11) << 16;

					// Remaining imm bits starts 3rd bit into byte 3
					operationValue |= (immediateValue & 0x3FFF) << 18;
					break;
				case 18: // Shifter
					// shifting needs the immediateValue to only be 4 bits long
					immediateValue &= 0xF;
					// then the rest of the logic is the same as case 17
				case 17: // ALU with imm
				case 6: // Jump and Link Register
				case 7: // Store/load
					// RD, first 5 bits of byte 2
					operationValue |= (instructionValues[3] & 0b11111) << 8;

					// RS1, last 3 bits of byte 2, first 2 bits of byte 3
					operationValue |= (instructionValues[1] & 0b11111) << 13;

					// Imm bits starts 3rd bit into byte 3
					operationValue |= (immediateValue & 0x3FFF) << 18;
					break;
				case 4: // Branching
//					System.out.println("Imm: " + immediateValue);
					// RS1, last 3 bits of byte 2, first 2 bits of byte 3
					operationValue |= (instructionValues[1] & 0b11111) << 13;

					// RS2, 5 bits of byte 3, starting 3 bits in
					operationValue |= (instructionValues[2] & 0b11111) << 18;

					// Bits [0 : 4] take the place of RD
					operationValue |= (immediateValue & 0x1F) << 8;

					// Bits [5 : 13] take the rest of the instruction
					operationValue |= (immediateValue >> 5) << 23;
					break;
				case 8: // IO ports
					immediateValue &= 0xF;

					// Imm bits starts 3rd bit into byte 3
					operationValue |= (immediateValue & 0x3FFF) << 18;

					if (operationValue >> 5 == 0) { // in
						// rd -> bit 0 of byte 2
						operationValue |= (instructionValues[2] & 0x1F) << 8;
					} else { // out
						// rs1 -> bit 5 of byte 2
						operationValue |= (instructionValues[1] & 0x1F) << 13;
					}
					break;
				default:
					throw new UnsupportedOperationException("Unimplemented encoding for function " +
							(operationValue & 0b11111));
			}
			instructions.add(operationValue);
			lineNumber++;
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
			return Integer.parseInt(s, base);
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
			if (line.isBlank()) {
				continue;
			}
			if (line.startsWith("def ")) {
				continue;
			}
			if (line.contains(":")) {
//				System.out.println("'" + line + "'");
				labels.put(line.substring(0, line.indexOf(':')), address);
				continue;
			}
//			System.out.println(line);
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
			if (!line.startsWith("def ")) {
				continue;
			}
			String[] split = line.split(" ");

			if (Opcodes.operationExists(split[1])) {
				System.err.println("Constant \"" + split[1] + "\" is a valid opcode, not a constant; Constants " +
						"cannot be opcodes\nLine: " + line + " (" + i + ")");
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
				if (line.isBlank()) {
					continue;
				}
				if (line.startsWith("def ")) {
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
	 * Removes comments and in-line comments from the file. Normalizes whitespace
	 * (tabs/spaces -> single space) and keeps the number of lines the same.
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

			// Remove in-line comments
			if (line.contains("//")) {
				line = line.substring(0, line.indexOf("//"));
			}

			// Normalize whitespace + brackets
			line = line.replaceAll("[()]", " ");   // replace () with spaces
			line = line.replaceAll("\\s+", " ");  // collapse tabs/spaces into one space
			line = line.trim();

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
