package Assembler;

import Assembler.Exceptions.OpcodeExistsException;
import Assembler.Operations.Opcodes;
import Assembler.Operations.PseudoOpcodes;
import Assembler.Operations.Registers;
import CPU.CPUSpecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Build {

	/**
	 * @param programPath Path to the assembly program
	 * @return Integer array of the converted assembly to machine code
	 */
	public static int[] build(String programPath) {
//		TODO:
//		 Make the stack pointer point to the proper position in the stack on program start
		System.out.println("Building program: " + programPath);
		ArrayList<String> programLinesList = new ArrayList<>();

		// Get all file contents including empty lines.
		Scanner scanner;
		try {
			scanner = new Scanner(new File(programPath));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			programLinesList.add(line);
		}


		// Get and filter the constants
		Map<String, Integer> constants = filterConstants(programLinesList);

		// Remove comments
		programLinesList = filterProgramComments(programLinesList);

		// Get the index of labels
		Map<String, Integer> labelTable = filterLabels(programLinesList);

		// Check for duplicates in the label table and constants
		for (String label : labelTable.keySet()) {
			if (constants.get(label) != null)
				throw new RuntimeException("Found a constant that shares a name with a label:\nConstant: " +
						constants.get(label) + "\tLabel: " + label);
		}

		// Replace pseudo opcodes with their counterparts
		String[] programLines = new String[programLinesList.size()];
		for (int i = 0; i < programLines.length; i++) {
			String line = programLinesList.get(i);
			try {
				programLines[i] = PseudoOpcodes.convert(line);
			} catch (IllegalArgumentException e) {
				programLines[i] = line;
			}
		}


		// Replace values in the constants and label table (in-place)
		convertConstants(programLines, constants, labelTable);

		// Convert to machine code and return
//		int[] code = convertToMachineCode(programLines);

		return convertToMachineCode(programLines);

	}

	/**
	 * Remove all comments and blanklines from a given program.
	 *
	 * @param programLines {@link ArrayList} of lines in the program.
	 * @return Returns a new ArrayList with the new program.
	 */
	private static ArrayList<String> filterProgramComments(ArrayList<String> programLines) {
		if (programLines == null)
			throw new IllegalArgumentException("programLines cannot be null");

		ArrayList<String> outputLines = new ArrayList<>(programLines.size());

		for (String line : programLines) {
			int stripIdx = line.indexOf("//") - 1;
			if (stripIdx >= 0)
				line = line.substring(0, stripIdx);

			line = line.strip().replaceAll("\t", " ");

			if (!line.isEmpty())
				outputLines.add(line);
		}
		return outputLines;
	}


	/**
	 * Returns a {@link Map<>} of all constants in the program.  Also removes those lines from the list of program
	 * lines.
	 *
	 * @param programLines All program lines in the file.
	 * @return Map of a String and Integer.  The string is the constant name, the integer is the value.
	 */
	private static Map<String, Integer> filterConstants(ArrayList<String> programLines) {
		HashMap<String, Integer> constants = new HashMap<>();
		for (int i = programLines.size() - 1; i >= 0; i--) {
			String line = programLines.get(i);
			String[] elements = line.split(" ");
			if (!elements[0].equals("def"))
				continue;


			if (elements.length != 3)
				throw new RuntimeException("Found a constant value started with 'def' expected 3 values, got " +
						elements.length + ".\nLine: " + (i + 1) + "\n>>> " + line);


			if (Opcodes.operationExists(elements[1]))
				throw new OpcodeExistsException("An opcode using that name already exists, use a different name.\n"
						+ "Line: " + (i + 1) + "\n>>> " + line);

			if (constants.containsKey(elements[1]))
				throw new IllegalArgumentException("Constant \"" + elements[1] + "\" already exists.\n" +
						"Line: " + (i + 1) + "\n>>> " + line);

			constants.put(elements[1], parseValue(elements[2]));
			programLines.remove(i);
		}

		return constants;
	}

	/**
	 * Finds all labels in the program and makes a map out of them.  Removes the label from the program.
	 *
	 * @param programLines The program to find the labels in.  Assumes that all other lines have already been removed
	 *                     and just contains program lines.
	 * @return Map
	 */
	private static Map<String, Integer> filterLabels(ArrayList<String> programLines) {
		HashMap<String, Integer> labels = new HashMap<>();
		for (int lineIdx = 0; lineIdx < programLines.size(); lineIdx++) {
			String line = programLines.get(lineIdx);
			if (line.charAt(line.length() - 1) != ':')
				continue;

			if (line.split(" ").length != 1)
				continue;

			String label = line.substring(0, line.length() - 1);

			if (Opcodes.operationExists(label))
				throw new OpcodeExistsException("Label cannot be an opcode.\n>> " + line + "\n");

			if (labels.containsKey(label))
				throw new RuntimeException("Label \"" + label + "\" already exists.");

			labels.put(label, lineIdx--);
			programLines.remove(line);

		}
		return labels;
	}

	/**
	 * In-place conversion of labels and constants to their integer values.
	 *
	 * @param programLines Array of each line in the program
	 * @param constants    Map of the name of the constant as the key, with an integer value corresponding to it
	 * @param labelTable   Map of the name of the label as the key, with the line it corresponds to as the integer value
	 */
	private static void convertConstants(String[] programLines, Map<String, Integer> constants,
	                                     Map<String, Integer> labelTable) {
		for (int j = 0; j < programLines.length; j++) {
			String line = programLines[j].trim();

			// Preprocess: Convert 0(s0) → 0 s0
			line = line.replaceAll("([a-zA-Z0-9_\\-]+)\\((\\w+)\\)", "$1 $2");

			String[] components = line.split("\\s+");
			if (components.length == 0) continue;

			String opcode = components[0];
			Integer mask = Opcodes.immediateMap.get(opcode);
			if (mask == null || mask == 0) {
				programLines[j] = line; // Just in case we modified it above
				continue;
			}

			// Start building updated components
			String[] updated = new String[components.length];
			updated[0] = opcode;
			int maskPosition = 0b10;
			for (int i = 1; i < components.length; i++) {
				// Check if this operand position is in the mask
				if ((mask & maskPosition) != 0) {
					String operand = components[i];

					Integer v = labelTable.get(operand);


					if (v == null) v = constants.get(operand);
					else { // V is not null, and has some value, if the opcode is a branch, convert to an offset
						if (updated[0].charAt(0) == 'b')
							// V has the destination; offset = destination - current
							v = v - j;

					}
					// if V is null, assume integer, I will have a later check for it
					if (v == null) {
						updated[i] = components[i];
					} else {
						updated[i] = v.toString();
					}

				} else {
					updated[i] = components[i];
				}
				maskPosition = maskPosition << 1;
			}

			programLines[j] = String.join(" ", updated);
		}
	}

	/**
	 * Convert a series of instructions into the equivalent machine code.
	 *
	 * @param programLines List of instructions to convert.
	 * @return Integer array of the machine code.
	 */
	private static int[] convertToMachineCode(String[] programLines) {
		int[] output = new int[programLines.length];
		int i = 0;
		for (String line : programLines) {
			String[] arguments = line.split("\\s");
			int machineCode = Opcodes.opcodeMap.get(arguments[0])[1];

			int opcode = machineCode & 0b11111;
			int rs1, rs2, rd, imm;
			switch (opcode) {
				case 0:
					// ___ ; op
					break;
				case 1:
				case 2:
				case 3:
					// rs2, rs1, rd, upper op, opcode
					rs1 = Registers.getRegisterValue(arguments[1]);
					rs2 = Registers.getRegisterValue(arguments[2]);
					rd = Registers.getRegisterValue(arguments[3]);
					machineCode |= ((rs2 << 10) | (rs1 << 5) | rd) << 8;
					break;
				case 6:
				case 7:
				case 17:
					// imm[13:0], rs1, rd; op, s1, imm, rd
					rs1 = Registers.getRegisterValue(arguments[1]);
					imm = parseValue(arguments[2]) & 0x3FFF;
					rd = Registers.getRegisterValue(arguments[3]);
					machineCode |= ((imm << 10) | (rs1 << 5) | rd) << 8;
					break;
				case 18:
					// imm[3:0], rs1, rd; op, s1, imm, rd
					rs1 = Registers.getRegisterValue(arguments[1]);
					imm = parseValue(arguments[2]);
					rd = Registers.getRegisterValue(arguments[3]);

					if (imm > 0xF)
						throw new RuntimeException("Cannot shift by a value greater than 15 -> " + line);
					if (imm < 0)
						throw new RuntimeException("Cannot shift by a value less than 0 -> " + line);

					machineCode |= ((imm << 10) | (rs1 << 5) | rd) << 8;
					break;
				case 4:
					// imm[13:5], rs2, rs1, imm[4:0] ; op, rs1, rs2, imm
					rs1 = Registers.getRegisterValue(arguments[1]);
					rs2 = Registers.getRegisterValue(arguments[2]);
					imm = parseValue(arguments[3]);
					machineCode |= (((imm & 0x3FE0) << 10) | (rs2 << 10) | (rs1 << 5) | (imm & 0x1F)) << 8;
					break;
				case 5:
					// imm[13:0], imm[15:14], [_ _ _], rd ; op rd imm
					rd = Registers.getRegisterValue(arguments[1]);
					imm = parseValue(arguments[2]);
					machineCode |= (((imm & 0x3FFF) << 10) | ((imm & 0xC000) >>> 6) | rd) << 8;
					break;
				case 25:
					// imm[13:0], imm[15:14], [3], rd ; op imm rd
					imm = parseValue(arguments[1]);
					rd = Registers.getRegisterValue(arguments[2]);
					machineCode |= (((imm & 0x3FFF) << 10) | ((imm & 0xC000) >>> 6) | rd) << 8;
					break;
				case 8:
					// needs special casing
					if (machineCode == 8) {
						// imm[3:0], [- - - - -], rd ; op, imm, rd
						imm = parseValue(arguments[1]);
						rd = Registers.getRegisterValue(arguments[2]);

						if (imm > 0xF)
							throw new RuntimeException("Cannot find port with value greater than 15 -> " + line);
						if (imm < 0)
							throw new RuntimeException("Cannot find port with value less than 0 -> " + line);

						machineCode |= ((imm << 10) | rd) << 8;
					} else if (machineCode == 40) {
						// imm[3:0], rs1, [- - - - -] ; op, rs1, imm
						rs1 = Registers.getRegisterValue(arguments[1]);
						imm = parseValue(arguments[2]);

						if (imm > 0xF)
							throw new RuntimeException("Cannot find port with value greater than 15 -> " + line);
						if (imm < 0)
							throw new RuntimeException("Cannot find port with value less than 0 -> " + line);

						machineCode |= ((imm << 10) | (rs1 << 5)) << 8;
					}
					break;
				default:
					/*throw new RuntimeException*/
					System.err.println("Could not find opcode: " + opcode);
			}


			output[i++] = machineCode;
		}
		return output;
	}

	/**
	 * Takes in a string value and figures out what the integer value is regardless of the prefix.
	 * For example, it can parse `0b10` (binary), `0765` (octal), `0xABC` (hexadecimal), or regular integers `1234`.
	 * There is also compatibility for converting characters to their respective values, but it needs to be in the
	 * format of `'c'`.
	 *
	 * @param value Some string containing a character string or numerical value.
	 * @return Integer representation of that value.
	 */
	private static int parseValue(String value) {
		// Parse char
		if (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') {
			if (value.length() <= 2 || value.length() > 4)
				throw new IllegalArgumentException("Character parsing requires characters to be a single " +
						"character. Either a single letter, or an escape character.");

			value = value.substring(1, value.length() - 1);

			if (value.charAt(0) == '\\') {
				return switch (value.charAt(1)) {
					case 'r' -> '\r';
					case 'n' -> '\n';
					default -> throw new IllegalStateException("Unexpected value: " + value.charAt(1));
				};
			}
			return value.charAt(0);
		}

		int base = 10;
		if (value.startsWith("0x") || value.startsWith("0X")) {
			value = value.substring(2);
			base = 16;
		} else if (value.startsWith("0b") || value.startsWith("0B")) {
			value = value.substring(2);
			base = 2;
		} else if (value.startsWith("0") && value.length() > 1) {
			value = value.substring(1);
			base = 8;
		}

		try {
			int result = Integer.parseInt(value, base);
			if (result > CPUSpecs.bitMask)
				throw new IllegalArgumentException("Value: \"" + value + "\" is not a legal numerical parsing");
			return result;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Value " + value + " is not a number");
		}
	}
}
