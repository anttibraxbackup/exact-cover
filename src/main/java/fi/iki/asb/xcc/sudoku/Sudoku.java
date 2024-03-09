package fi.iki.asb.xcc.sudoku;

public class Sudoku {

	// TODO Should start the grid indexing from 0.

	static void validateSize(int size) {
		final int sqrt = (int) Math.sqrt(size);
		if (sqrt * sqrt != size || size < 1) {
			throw new IllegalArgumentException("Illegal size [" + size + "]");
		}
	}

	static void validateNumber(int number, int size) {
		if (number < 1 || number > size) {
			throw new IllegalArgumentException("Illegal number "
					+ format(number));
		}
	}

	static void validateLocation(int row, int column, int size) {
		if (row < 1 || row> size || column < 0 || column > size) {
			throw new IllegalArgumentException("Illegal location "
					+ format(row, column));
		}
	}

	static int indexOf(int row, int column, int size) {
		validateLocation(row, column, size);
		return (row - 1) * size + (column - 1);
	}

	static String format(int row, int column) {
		return "[" + row + ", " + column + "]";
	}

	static String format(int number) {
		return "[" + number + "]";
	}
}
