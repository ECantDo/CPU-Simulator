package Assembler;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import Assembler.Operations.*;

public class Build {

    public static void main(String[] args) {
        String filePath = "src\\ramStackTest.as";

        System.out.println("PROGRAM: " + Arrays.toString(build(filePath)));

    }

    public static int[] build(String filePath) {
        String[] fileContents = formatFile(readFile(filePath));
        Map<String, Integer> labels = getLabels(fileContents);
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


    private static Integer[] assemble(String[] fileContents, Map<String, Integer> labels) {

        System.out.println(labels);

        ArrayList<Integer> instructions = new ArrayList<>();

        for (String line : fileContents) {
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
                    System.err.println("Invalid operation: " + part);
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
                System.err.println("Invalid operation: " + operation);
                System.exit(-1);
            }
            if (opData[0] != parts.length - numberOfImmediateValues - 1) {

                System.err.println("Invalid operation: '" + operation + "'\nInvalid number of arguments, " +
                        "expected " + opData[0] + " got " + (parts.length - numberOfImmediateValues - 1));
                System.exit(-1);
            }

            operationValue |= opData[1] << opData[2];

            int length = parts.length - numberOfImmediateValues - 1;
            String[] non_imm_execution = Arrays.copyOfRange(parts, numberOfImmediateValues + 1, parts.length);
//            System.out.println(Arrays.toString(non_imm_execution));
            if (opData[0] == 1) {

                String value = non_imm_execution[0];
                if (Registers.contains(value)) {
                    operationValue |= Registers.getRegister(value);
                } else if (labels.containsKey(value)) {
                    operationValue |= labels.get(value);
                } else {
                    operationValue |= Integer.parseInt(value);
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
                        operationValue |= Integer.parseInt(value) << shiftAmount;
                    } catch (NumberFormatException e) {
                        System.err.println("Value '" + value + "' is not a number, expected an immediate value.\n" +
                                "Failed ");
                        System.exit(-1);
                    }
                } else {
                    if (Registers.contains(value)) {
                        operationValue |= Registers.getRegister(value) << shiftAmount;
                    } else {
                        System.err.println("Register " + value + " not found, " + value + " might not be a register");
                        System.exit(-1);
                    }
                }
                if (operation.equals("not")) {
//                    byte02_value = byte01_value
                    operationValue |= ((operationValue & (0xFF << 16)) >> 8); // Take the value from byte 01, and put it in byte 02
                }

                value = non_imm_execution[1];
                if (operation.equals("str")) {
                    shiftAmount = 8;
                } else {
                    shiftAmount = 0;
                }
                if (Registers.contains(value)) {
//                    byte03_value = registers.index(value);
                    operationValue |= Registers.getRegister(value) << shiftAmount;
                } else if (labels.containsKey(value)) {
//                    byte03_value = labels[value];
                    operationValue |= labels.get(value) << shiftAmount;
                } else {
//                    byte03_value = int(value);
                    operationValue |= Integer.parseInt(value) << shiftAmount;
                }
            } else if (opData[0] == 3) {

                String value = non_imm_execution[0];
                if (imm_a) {
                    try {
                        operationValue |= Integer.parseInt(value) << 16;
                    } catch (NumberFormatException e) {
                        System.err.println("Value " + value + " is not a number, expected an immediate");
                        System.exit(-1);
                    }
//                    byte01_value = int(value)
                } else {
                    if (Registers.contains(value)) {
//                        byte01_value = registers.index(value);
                        operationValue |= Registers.getRegister(value) << 16;
                    } else {
                        System.err.println("Register " + value + " not found, " + value + " might not be a register");
                        System.exit(-1);
                    }
                }

                value = non_imm_execution[1];
                if (imm_b) {
                    try {
                        operationValue |= Integer.parseInt(value) << 8;
                    } catch (NumberFormatException e) {
                        System.err.println("Value " + value + " is not a number, expected an immediate");
                        System.exit(-1);
                    }
                } else {
                    if (Registers.contains(value)) {
//                        byte02_value = registers.index(value)
                        operationValue |= Registers.getRegister(value) << 8;
                    } else {
                        System.err.println("Register " + value + " not found, " + value + " might not be a register");
                    }
                }

                value = non_imm_execution[2];
                if (Registers.contains(value)) {
//                    byte03_value = registers.index(value)
                    operationValue |= Registers.getRegister(value);
                } else if (labels.containsKey(value)) {
//                    byte03_value = labels[value]
                    operationValue |= labels.get(value);
                } else {
//                    byte03_value = int(value)
                    operationValue |= Integer.parseInt(value);
                }
            }
//        elif arguments == 0:
//            pass


            instructions.add(operationValue);
        }
        return instructions.toArray(new Integer[0]);
    }

    /**
     * Returns a map of labels and their addresses
     *
     * @param fileContents the contents of the file, formatted
     */
    private static Map<String, Integer> getLabels(String[] fileContents) {
        Map<String, Integer> labels = new HashMap<>();
        int address = 0;
        for (String line : fileContents) {
            if (line.charAt(0) == ':') {
                labels.put(line, address);
                continue;
            }
            address++;
        }
        return labels;
    }

    private static String[] formatFile(String[] fileContents) {
        ArrayList<String> lines = new ArrayList<>();
        for (String line : fileContents) {
            line = line.trim();
            if (line.isBlank()) { // Remove blank lines
                continue;
            }
            if (line.startsWith("//")) { // Remove comments
                continue;
            }
            line = line.split("//")[0]; // Remove in-line comments
            lines.add(line);
        }
        return lines.toArray(new String[0]);
    }

    private static String[] readFile(String filePath) {
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
            System.err.println("An error occurred.  File likely not found.");
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
