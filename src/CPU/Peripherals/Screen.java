package CPU.Peripherals;

import java.io.*;
import java.net.*;
import java.lang.*;


public class Screen implements IOInterface {

    private Process PROCESS;
    private Socket SOCKET;
    private final DataOutputStream DOUT;
    private final DataInputStream DIN;

    public Screen() {
        this.setupExit();
        try {
            this.PROCESS = this.startPythonProgram();
        } catch (Exception e) {

        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.SOCKET = this.setupClient();
        try {
            this.DOUT = new DataOutputStream(this.SOCKET.getOutputStream());
            this.DIN = new DataInputStream(this.SOCKET.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //======================================================================================================================
// Input Functions
//======================================================================================================================
    public void ioInput(int data) {
        int x_coord = data & 0x3F;
        int y_coord = (data >> 8) & 0x3F;
        int opcode = ((data >> 14) & 0b11);

        if (opcode == 0b00) {
            this.putPixel(x_coord, y_coord, true);
        } else if (opcode == 0b01) {
            this.putPixel(x_coord, y_coord, false);
        } else if (opcode == 0b10) {
            this.clear();
        } else if (opcode == 0b11) { // Always true as there is no other options that there could be
            this.update();
        }
    }

    public int ioOutput() {
        return 0;
    }


    //======================================================================================================================
// Setup Functions
//======================================================================================================================
    public void setupExit() {
        Thread shutdownHook = new Thread() {
            @Override
            public void run() {
//                System.out.println("Stop the python program here.");

                try {
                    DOUT.writeUTF("Stop");
                    System.out.println("Stop Message Sent");
                    DOUT.flush();
                    DOUT.close();
                    SOCKET.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public Process startPythonProgram() {
        Process p = null;
        String directory = System.getProperty("user.dir");
        System.out.println(directory);
        directory += "\\src\\CPU\\Peripherals\\runPythonCmd.bat";
        System.out.println("cmd /c start \"PYTHON OUTPUT \" " + directory);
        try {
            p = Runtime.getRuntime().exec("cmd /c start \"PYTHON OUTPUT \" " + directory);
            p.waitFor();
        } catch (IOException e) {
        } catch (InterruptedException e) {
        }
        return p;


        // TODO: Change back to the following when finished.

//        directory += "\\src\\CPU\\Peripherals\\Python\\Screen.py";
//        String runCommand = "python " + directory;
//
//
//        System.out.println(runCommand);
//
//        try {
//            p = Runtime.getRuntime().exec(runCommand, new String[]{""} /*Arguments*/);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(-1);
//        }
//        return p;
    }

    public Socket setupClient() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 50505);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String msg = (String) in.readUTF();
            System.out.println("Server: " + msg);
            dout.writeUTF("Ok Boss");
            dout.flush();
//            dout.close();


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return socket;
    }

    //======================================================================================================================
// Screen Functions
//======================================================================================================================
    public void waitForScreen() throws IOException {
        String msg = (String) DIN.readUTF(); // Wait for screen to update
    }

    public void putPixel(int x, int y, boolean color) {
        // Talk to the python program here.

        try {
            DOUT.writeUTF("PP [(" + x + ", " + y + "), " +
                    (Boolean.toString(color).replace("t", "T").replace("f", "F")) // Makes it python compatible
                    + "]");
            DOUT.flush();

            waitForScreen();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        // Talk to the python program here.
        try {
            DOUT.writeUTF("Clear");
            DOUT.flush();

            waitForScreen();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {
        try {
            DOUT.writeUTF("Update Display");
            DOUT.flush();

            waitForScreen();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
