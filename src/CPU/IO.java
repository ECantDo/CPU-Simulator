package CPU;

import CPU.Peripherals.*;
import CPU.Peripherals.Screen.JavaScreen;

public class IO {

    private final IOInterface[] IOPorts;// = new Object[16];

    public IO() {
        this.IOPorts = new IOInterface[16];

        this.IOPorts[0] = new JavaScreen(16, 64);
    }

    public void ioOutput(int data, int port) {
        port = port & 0xF; // 4 bit for port
        data = data & 0xFFFF; // 16 bit being sent

        if (this.IOPorts[port] == null){
            throw new IllegalArgumentException("Cannot output to port " + port +
                    " because nothing exists for that port");
        }

        this.IOPorts[port].ioInput(data);
    }

    public int ioInput(int port) {
        port = port & 0xF; // 4 bit for port
        return (byte)this.IOPorts[port].ioOutput();
    }

}
