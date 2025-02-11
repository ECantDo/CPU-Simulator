package CPU.Peripherals;

public interface ScreenInterface extends IOInterface {
	void putPixel(int x, int y, boolean color);
	void clear();
	void update();
}
