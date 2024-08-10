package CPU;

public class RAM {


    private byte[] ram;// = new byte[256];

    public RAM() {
        int byteCount = 8;
        ram = new byte[(int) Math.pow(2, byteCount)];
    }


    public byte get(int address) {
        return ram[address];
    }

    public void set(int address, byte value) {
        ram[address] = value;
    }
}
