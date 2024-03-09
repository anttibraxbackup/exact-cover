package fi.iki.asb.xcc.queen;

public class QueenGrid {

	private final int size;
	private final boolean[] grid;

	public QueenGrid(int size) {
		this.size = size;
		grid = new boolean[size * size];
	}

	public void setQueen(int row, int col) {
		grid[row * size + col] = true;
	}

	public boolean hasQueen(int row, int col) {
		return grid[row * size + col];
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder((size + 3) * (size + 2));

		sb.append('+').append("-".repeat(size)).append("+\n");
		for (int row = 0; row < size; row++) {
			sb.append('|');
			for (int col = 0; col < size; col++) {
				sb.append(grid[row * size + col] ? 'Q' : ' ');
			}
			sb.append("|\n");
		}
		sb.append('+').append("-".repeat(size)).append("+\n");
		return sb.toString();
	}

	public String identity() {
		StringBuilder sb = new StringBuilder(size);
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (hasQueen(row, col)) {
					sb.append(col);
				}
			}
		}
		return sb.toString();
	}
}
