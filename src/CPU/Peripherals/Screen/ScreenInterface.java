package CPU.Peripherals.Screen;

import CPU.Peripherals.IOInterface;

public interface ScreenInterface extends IOInterface {
	void putPixel(int x, int y, boolean color);

	void clear();

	void update();

	void ioInput(int data);
}
