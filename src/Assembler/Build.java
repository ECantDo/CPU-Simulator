package Assembler;

import Assembler.Exceptions.OpcodeExistsException;
import Assembler.Operations.Opcodes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
		ArrayList<String> programLinesList = new ArrayList<>();

		// Get file contents
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(programPath));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().toLowerCase();
			programLinesList.add(line);
		}

		programLinesList = filterProgramComments(programLinesList);

		int[] programValues = new int[programLinesList.size()];

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


	private static Map<String, Integer> filterConstants(ArrayList<String> programLines){
		for (int i = 0; i < programLines.size(); i++){
			String line = programLines.get(i);
			String[] elements = line.split(" ");
			if (!elements[0].equals("def"))
				continue;

			if (Opcodes.operationExists(elements[1]))
				throw new OpcodeExistsException();

		}
	}

	private static int parseValue(String value){
		// Parse char
		if (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'' ){
				if (value.length() <= 2 || value.length() > 4)
					throw new IllegalArgumentException("Character parsing requires characters to be a single " +
							"character. Either a single letter, or an escape character.");

			value = value.substring(1, value.length() - 1);

			if (value.charAt(0) == '\\'){
				return switch (value.charAt(1)){
					case 'r' -> '\r';
					case 'n' -> '\n';
					default -> throw new IllegalStateException("Unexpected value: " + value.charAt(1));
				};
			}
			return value.charAt(0);
		}

		// todo: parse int, octal, bin, hex
	}

}
