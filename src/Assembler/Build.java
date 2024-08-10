package Assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import Assembler.Operations.*;

import static Assembler.Operations.Registers.registers;

public class Build {

    public static void main(String[] args) {
        String filePath = "src\\program.as";
        String[] fileContents = formatFile(readFile(filePath));

        Map<String, Integer> labels = getLabels(fileContents);

//        System.out.println(Arrays.toString(fileContents));
//        System.out.println(labels);

        Integer[] instructions = assemble(fileContents, labels);

        System.out.println(Arrays.toString(instructions));
    }


    private static Integer[] assemble(String[] fileContents, Map<String, Integer> labels) {
        ArrayList<Integer> instructions = new ArrayList<>();

        for (String line : fileContents) {
            int operationValue = 0;
            if (line.charAt(0) == ':') { // Skip labels
                continue;
            }

            String[] parts = line.split(" ");
            System.out.println(Arrays.toString(parts));

            int immediates = 0;
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
                    immediates++;
                    continue;
                }
                break;
            }
            String operation = parts[immediates];
            System.out.println(operation);

            int[] opData = Opcodes.getOperation(operation);

            if (opData == null) {
                System.err.println("Invalid operation: " + operation);
                System.exit(-1);
            }
            if (opData[0] != parts.length - immediates - 1) {

                System.err.println("Invalid operation: '" + operation + "'\nInvalid number of arguments, " +
                        "expected " + opData[0] + " got " + (parts.length - immediates - 1));
                System.exit(-1);
            }

            operationValue |= opData[1] << opData[2];

            int length = parts.length - immediates - 1;
            String[] non_imm_execution = Arrays.copyOfRange(parts, immediates + 1, parts.length);
            if (opData[0] == 1) {

                String value = non_imm_execution[1];
                if (Registers.contains(value)) {
                    operationValue |= Registers.getRegister(value);
                } else if (labels.containsKey(value)) {
                    operationValue |= labels.get(value);
                } else {
                    operationValue |= Integer.parseInt(value);
                }
            } else if (opData[0] == 2) {
                String value = non_imm_execution[1];
                if (imm_a) {
                    try {
                        operationValue |= Integer.parseInt(value) << 16;
                    } catch (NumberFormatException e) {
                        System.err.println("Value " + value + " is not a number, expected an immediate");
                        System.exit(-1);
                    }
                } else {
                    if (Registers.contains(value)) {
                        operationValue |= Registers.getRegister(value) << 16;
                    } else {
                        System.err.println("Register " + value + " not found, " + value + " might not be a register");
                        System.exit(-1);
                    }
                }
                if (operation.equals("not")) {
//                    byte02_value = byte01_value
                    operationValue |= ((operationValue & (0xFF << 16)) >> 8);
                }
// TODO: FINISH REWRITING THE PYTHON CODE TO JAVA
                value = non_imm_execution[2]
                if value in registers:
                byte03_value = registers.index(value)
                elif value in labels:
                byte03_value = labels[value]
            else:
                byte03_value = int(value)
            }
            elif opData[ 0] ==3:
            if length > 3:
            raise Exception (f "Too many arguments for opcode {opcode}, expected {arguments} arguments"
            f " got {len(non_imm_execution[1:])}, (line {idx})")
            if length < 3:
            raise Exception (f "Not enough arguments for opcode {opcode}, expected {arguments} arguments,"
            f " got {len(non_imm_execution[1:])}, (line {idx})")

            value = non_imm_execution[1]
            if imm_a:
            if value.isnumeric():
            byte01_value = int(value)
                else:
            raise Exception (
                    f
            "Value {non_imm_execution[1]} is not a number, expected an immediate, numeric, value, (line {idx})")
            else:
            if value in registers:
            byte01_value = registers.index(value)
                else:
            raise Exception (f "Register {value} not found, "
            f "value {value} might not be a register, (line {idx})")

            ##
            value = non_imm_execution[2]
            if imm_b:
            if value.isnumeric():
            byte02_value = int(value)
                else:
            raise Exception (
                    f
            "Value {non_imm_execution[1]} is not a number, expected an immediate, numeric, value, (line {idx})")
            else:
            if value in registers:
            byte02_value = registers.index(value)
                else:
            raise Exception (f "Register {value} not found, "
            f "value {value} might not be a register, (line {idx})")

            ##
            value = non_imm_execution[3]
            if value in registers:
            byte03_value = registers.index(value)
            elif value in labels:
            byte03_value = labels[value]
            else:
            byte03_value = int(value)
                    elif arguments == 0:
            pass
        else:
            raise Exception (f "Opcode {opcode} has {arguments} arguments, but it should have {arguments} arguments"
            f " got {len(non_imm_execution[1:])}, (line {idx})")


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
                labels.put(line.substring(1), address);
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
