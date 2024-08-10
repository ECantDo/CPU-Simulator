package CPU;

public class ExecutionLoop {

    ProgramCounter programCounter;
    Registers registers;
    ALU alu;
    CLU clu;
    ProgramMemory programMemory;
    Stack stack;
    RAM ram;

    public ExecutionLoop() {
        programCounter = new ProgramCounter();
        registers = new Registers();
        alu = new ALU();
        clu = new CLU();
        programMemory = new ProgramMemory();
        stack = new Stack();
        ram = new RAM();
    }

    public void loop() {

        System.out.println("PROGRAM START\n");

        do {
            System.out.print(programCounter.getProgramCounter() + " : " + registers.toString() + "\r");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (cycle());
        System.out.println();
        System.out.println("Program finished\n\nREGISTERS:");
        System.out.println(registers.toString());

    }

    public boolean cycle() {
        int instruction = programMemory.getInstruction(programCounter.getProgramCounter());
        programCounter.increment();

        // Extract the opcode, byte01, byte02, and byte03 from the instruction
        byte opcode = (byte) ((instruction & (0xFF00_0000)) >> 24);
        byte byte01 = (byte) ((instruction & (0x00FF_0000)) >> 16);
        byte byte02 = (byte) ((instruction & (0x0000_FF00)) >> 8);
        byte byte03 = (byte) ((instruction & (0x0000_00FF)) >> 0);

        boolean immediate_a = (opcode & 0b1000_0000) != 0; // is the immediate bit set for value A?
        boolean immediate_b = (opcode & 0b0100_0000) != 0; // is the immediate bit set for value B?
        opcode = (byte) (opcode & 0b0011_1111); // remove the immediate bits

        if (opcode == 0) {
            return true; // If the opcode is 0, then the loop should continue; noop instruction
        }
        if (opcode == 1) {
            return false; // If the opcode is 1, then the loop should stop; halt instruction
        }

        byte[] registerValues = registers.get(byte01, byte02); // Get the values in the registers


        // THIS HAS TO HAPPEN
        byte valueA = immediate_a ? byte01 : registerValues[0]; // Mux for value A; immediate_a vs registerValues[0]
        byte valueB = immediate_b ? byte02 : registerValues[1]; // Mux for value B; immediate_b vs registerValues[1]


        // Execute the RAM
        if (opcode == 24) { // STR
            ram.set(byte02, byte03);
        } else if (opcode == 25) { // LOD
            registers.set(byte03, ram.get(byte02)); // Get from the ram, set in the registers
        }
        // Execute the stack
        if (opcode == 26) { // CAL
            stack.push(programCounter.getProgramCounter());
        } else if (opcode == 27) { // RET
            programCounter.set(stack.pop());
        }

        // Execute the CPU.ALU and CPU.CLU
        boolean runALU = ALU.getOpcode(opcode) != -1;
        byte result = alu.run(opcode, valueA, valueB); // Execute the CPU.ALU

        boolean runCLU = CLU.getOpcode(opcode) != -1;
        boolean setCounter = clu.run(opcode, valueA, valueB); // Execute the CPU.CLU

        if (runALU) {
            registers.set(byte03, result); // Set the value in the register
        } else if (runCLU && setCounter) {
            programCounter.set(byte03);
        }


//        registers.set(byte03, result); // Set the value in the register

        return true; // Returns true if the loop should continue
    }

    public String printValue(int value) {
        String binary = Integer.toBinaryString(value);
        binary = "00000000000000000000000000000000" + binary;
        binary = binary.substring(binary.length() - 32);
        return binary;
    }

    public String printValue(byte value) {
        String binary = Integer.toBinaryString(value);
        binary = "00000000" + binary;
        binary = binary.substring(binary.length() - 8);
        return binary;
    }

}
