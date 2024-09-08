package CPU;

import CPU.Peripherals.Screen;

public class IO {

    private final Object IOPorts[];// = new Object[16];

    public IO() {
        this.IOPorts = new Object[16];

        this.IOPorts[0] = new Screen();
    }
}
