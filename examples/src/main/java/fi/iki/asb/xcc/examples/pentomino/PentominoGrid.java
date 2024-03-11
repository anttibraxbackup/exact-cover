package fi.iki.asb.xcc.examples.pentomino;

public class PentominoGrid {

	private final int height;
	private final int width;
	private final char[] grid;

	public PentominoGrid(int width) {
		this.width = width;
		this.height = 60 / width;
		if (height * width != 60) {
			throw new IllegalArgumentException("Illegal width [" + width + "]");
		}

		grid = new char[height * width];
	}

	public void setChar(int row, int col, char ch) {
		grid[row * width + col] = ch;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder((width + 3) * (height + 2));

		sb.append('+').append("-".repeat(width)).append("+\n");
		for (int row = 0; row < height; row++) {
			sb.append('|');
			for (int col = 0; col < width; col++) {
				sb.append(grid[row * width + col]);
			}
			sb.append("|\n");
		}
		sb.append('+').append("-".repeat(width)).append("+\n");
		return sb.toString();
	}

}
