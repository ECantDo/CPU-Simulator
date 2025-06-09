package Assembler;

import Assembler.Exceptions.OpcodeExistsException;
import Assembler.Operations.Opcodes;
import Assembler.Operations.PseudoOpcodes;
import CPU.CPUSpecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Build {

	/**
	 * @param programPath
	 * @return
	 */
	public static int[] build(String programPath) {
//		TODO:
//		 Make the stack pointer point to the proper position in the stack
		System.out.println("Building program: " + programPath);
		ArrayList<String> programLinesList = new ArrayList<>();

		// Get all file contents including empty lines.
		Scanner scanner = null;
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

		// Todo: Check for duplicates in the label table and constants

		// Print things out for debugging.
		for (String key : constants.keySet())
			System.out.println(key + " " + constants.get(key));

		System.out.println("----"); // Todo; remove
		for (String label : labelTable.keySet())
			System.out.println(label + " " + labelTable.get(label));

		System.out.println("----"); // Todo; remove
		for (String line : programLinesList)
			System.out.println(line);

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

		System.out.println("----"); // Todo; remove
		for (String line : programLines)
			System.out.println(line);

		int[] programValues = new int[programLines.length];

		return programValues;
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
	 * @param programLines
	 * @param constants
	 * @param labelTable
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

		// todo: parse int, octal, bin, hex
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
