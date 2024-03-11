package fi.iki.asb.xcc.examples.pentomino;

import java.util.concurrent.atomic.AtomicInteger;


public class Pentomino {

	private final char identifier; 
	private final boolean[] form;
	private final int height;
	private final int width;

	private Pentomino(char identifier,
					  int height, int width,
					  boolean[] form) {
		this.identifier = identifier;
		this.form = form;
		this.height = height;
		this.width = width;
	}

	public char identifier() {
		return identifier;
	}

	public int height() {
		return height;
	}

	public int width() {
		return width;
	}

	public boolean hasSquare(int row, int column) {
		validate(row, column);
		return form[row * width + column];
	}

	/**
	 * Create a mirror image of this Pentomino.
	 */
	public Pentomino flip() {
		final Pentomino p = new Pentomino(
				identifier,
				height(), width(),
				new boolean[form.length]);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				p.setSquare(row, width() - col - 1, hasSquare(row, col));
			}
		}

		return p;
	}

	/**
	 * Rotate this Pentomino 90 degrees.
	 */
	public Pentomino rotate() {
		final Pentomino p = new Pentomino(
				identifier,
				width(), height(),
				new boolean[form.length]);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				p.setSquare(col, (p.width() - row) - 1,
						hasSquare(row, col));
			}
		}

		return p;
	}

	public static Pentomino parse(String ... data) {
		final AtomicInteger squareCount = new AtomicInteger();
		final int height = data.length;
		final int width = data[0].length();
		final Pentomino p = new Pentomino(
				data[0].replace(" ", "").charAt(0),
				height, width,
				new boolean[height * width]);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (data[row].charAt(col) != ' ') {
					p.setSquare(row, col, true);
					squareCount.set(squareCount.get() + 1);
				}
			}
		}

		if (squareCount.get() != 5) {
			throw new IllegalArgumentException(
					"Pentomino must have 5 squares");
		}

		return p;
	}

	private void setSquare(int row, int column, boolean hasSquare) {
		validate(row, column);
		form[row * width + column] = hasSquare;
	}

	private void validate(int row, int column) {
		if (row < 0 || row >= height || column < 0 || column >= width) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString() {
		return "(" + identifier + ')';
	}
}
