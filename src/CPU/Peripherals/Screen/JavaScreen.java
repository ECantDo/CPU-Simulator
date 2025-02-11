package CPU.Peripherals.Screen;

import CPU.Peripherals.IOInterface;

import javax.swing.*;
import java.awt.*;

public class JavaScreen extends JFrame implements ScreenInterface, IOInterface {

	private final int rows;
	private final int columns;
	private final int cellSize;

	private Color[][] gridColorsBuffer;
	private Color[][] gridColors;

	public static final Color PIXEL_OFF = Color.getColor("#45260b");
	public static final Color PIXEL_ON = Color.getColor("#97743e");

	public JavaScreen(int cellSquareWidth, int gridSquareWidth) {
		this.cellSize = cellSquareWidth;
		this.columns = gridSquareWidth;
		this.rows = gridSquareWidth;

		this.gridColorsBuffer = new Color[this.rows][this.columns];
		this.gridColors = new Color[this.rows][this.columns];
		this.setAll(false);
		this.update();

		this.setTitle("Redstone Lamp Display");
		this.setSize(this.columns * this.cellSize, this.rows * this.cellSize);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Sets all the color of the entire screen.
	 *
	 * @param color Boolean; True for on; False for off.
	 */
	private void setAll(boolean color) {
		Color colorValue = color ? PIXEL_ON : PIXEL_OFF;

		for (int x = 0; x < this.columns; x++) {
			for (int y = 0; y < this.rows; y++) {
				this.gridColorsBuffer[x][y] = colorValue;
			}
		}
	}

	/**
	 * Sets the color of a pixel to the given color.
	 *
	 * @param color Boolean; True for on; False for off.
	 */
	@Override
	public void putPixel(int x, int y, boolean color) {

	}

	/**
	 * Sets the color of the screen to the {@link JavaScreen#PIXEL_OFF} color.
	 */
	@Override
	public void clear() {
		this.setAll(false);
	}

	/**
	 * Moves the colors from the buffer, into the live screen.
	 */
	@Override
	public void update() {
		for (int i = 0; i < this.gridColors.length; i++) {
			System.arraycopy(this.gridColorsBuffer[i], 0, this.gridColors[i], 0, this.gridColors.length);
		}
	}
	//==================================================================================================================
	// IO Stuff
	//==================================================================================================================

	@Override
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

	@Override
	public int ioOutput() {
		return 0;
	}
}
