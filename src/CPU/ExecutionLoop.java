package CPU;

public class ExecutionLoop {

    ProgramCounter programCounter;
    Registers registers;
    ALU alu;
    CLU clu;
    ProgramMemory programMemory;
    Stack stack;
    RAM ram;
    IO io;
    int speed;

    public ExecutionLoop() {
        programCounter = new ProgramCounter();
        registers = new Registers();
        alu = new ALU();
        clu = new CLU();
        programMemory = new ProgramMemory();
        stack = new Stack();
        ram = new RAM();
        io = new IO();
        speed = 10;
    }

    public ExecutionLoop(int[] program, int speed) {
        this();
        programMemory = new ProgramMemory(program);
        this.speed = speed;
    }

    public void loop() {

        System.out.println("PROGRAM START\n");
        long step = 0;

        do {
            System.out.print(programCounter.getProgramCounter() + " : " + registers.toString() + " : S" + step++ + "\r");
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (cycle());
        System.out.println();
        System.out.println("Program finished\n\nREGISTERS:");
        System.out.println(registers.toString());

        System.out.println();
        System.out.println("RAM:");
        System.out.println(ram.toString());

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
            ram.set(valueB, valueA);
//            System.out.println("set ram idx: " + valueB + " val: " + valueA);
        } else if (opcode == 25) { // LOD
            registers.set(byte03, (byte) ram.get(valueB)); // Get from the ram, set in the registers
        }
        // Execute the stack
        else if (opcode == 26) { // CAL
            stack.push(programCounter.getProgramCounter()); // THE COUNTER WAS INCREMENTED AT THE START OF THE CYCLE
            programCounter.set(byte03);
            // REMEMBER THAT THE COUNTER VALUE IS NOT THE SAME AS THE CURRENT INSTRUCTION, IT IS ONE AHEAD
        } else if (opcode == 27) { // RET
            programCounter.set(stack.pop());
        } else if (opcode == 28) { // IO Out
            io.ioOutput(valueA, byte03);
        } else if(opcode == 29) { // IO In
            registers.set(byte03, (byte)io.ioInput(valueA));
        }

        // Execute the CPU.ALU and CPU.CLU
        boolean runALU = ALU.getOpcode(opcode) != -1;
        byte result = alu.run(opcode, valueA, valueB); // Execute the CPU.ALU

        boolean runCLU = CLU.getOpcode(opcode) != -1;
        boolean setCounter = clu.run(opcode, valueA, valueB); // Execute the CPU.CLU

//        System.out.println("\nBYTES: " +
//                String.format("%8s", Integer.toBinaryString(byte01)).replace(' ', '0') + " " +
//                String.format("%8s", Integer.toBinaryString(byte02)).replace(' ', '0') + " " +
//                String.format("%8s", Integer.toBinaryString(byte03)).replace(' ', '0'));
//        System.out.println("Op value: " + String.format("%32s", Integer.toBinaryString(instruction)).replace(' ', '0'));

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
