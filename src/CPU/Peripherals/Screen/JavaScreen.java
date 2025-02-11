package CPU.Peripherals.Screen;

import CPU.Peripherals.IOInterface;

import javax.swing.*;
import java.awt.*;

public class JavaScreen extends JFrame implements ScreenInterface, IOInterface {

	private final int rows;
	private final int columns;
	private final int cellSize;

	private Color[][] gridColors;

	public JavaScreen(int cellSquareWidth, int gridSquareWidth) {
		this.cellSize = cellSquareWidth;
		this.columns = gridSquareWidth;
		this.rows = gridSquareWidth;

		this.gridColors = new Color[this.rows][this.columns];
	}

	@Override
	public void putPixel(int x, int y, boolean color) {

	}

	@Override
	public void clear() {

	}

	@Override
	public void update() {

	}

	@Override
	public void ioInput(int data) {

	}

	@Override
	public int ioOutput() {
		return 0;
	}
}
