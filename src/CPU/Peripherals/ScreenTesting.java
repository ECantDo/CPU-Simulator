package CPU.Peripherals;

public class ScreenTesting {

    public static void main(String[] args) throws InterruptedException {
        Screen screen = new Screen();
        screen.putPixel(1, 1, true);
        Thread.sleep(1000);
        screen.clear();
        screen.putPixel(0, 0, true);
        Thread.sleep(5000);

    }
}
