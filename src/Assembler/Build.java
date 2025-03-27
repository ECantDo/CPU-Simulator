package Assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Build {
	public static int[] build(String programPath) {
		ArrayList<String> programLinesList = new ArrayList<>();

		// Get file contents
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

		String[] filteredProgramLines = filterProgram(programLinesList);
		int[] programValues = new int[filteredProgramLines.length];

		return programValues;
	}

	private static String[] filterProgram(ArrayList<String> programLines) {
		if (programLines == null)
			throw new IllegalArgumentException("programLines cannot be null");

		ArrayList<String> outputLines = new ArrayList<>(programLines.size());

		for (String line : programLines) {
			line = line.strip();
			if (line.startsWith("//"))
				continue;

			outputLines.add(line); // todo: finish
		}
		return outputLines.toArray(new String[]{});
	}

}
