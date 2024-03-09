package fi.iki.asb.xcc.sudoku;

import static fi.iki.asb.xcc.sudoku.Sudoku.indexOf;
import static fi.iki.asb.xcc.sudoku.Sudoku.format;
import static fi.iki.asb.xcc.sudoku.Sudoku.validateNumber;
import static fi.iki.asb.xcc.sudoku.Sudoku.validateSize;

import java.util.Arrays;

public class SudokuGrid {

	private final int size;

	/**
	 * Numbers given in the puzzle.
	 */
	private final int[] givens;

	/**
	 * Guesses made by the player.
	 */
	private final int[] guesses;

	public SudokuGrid(int size) {
		validateSize(size);
		this.size = size;

		guesses = new int[size * size];
		givens = new int[size * size];

		Arrays.fill(guesses, -1);
		Arrays.fill(givens, -1);
	}

	public static SudokuGrid parse(String str) {
		str = str.replace("\n", "")
				.replace("\r", "");
		final int size = (int) Math.sqrt(str.length());
		if (size * size != str.length()) {
			throw new IllegalArgumentException("Invalid input");
		}

		final SudokuGrid grid = new SudokuGrid(size);
		int i = 0;
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				char ch = str.charAt(i++);
				switch (ch) {
					case '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
						grid.setGiven(Character.getNumericValue(ch),
								row + 1, column + 1);
					default -> {
						// Treat non-numeric characters as empty.
					}
				}
			}
		}
		return grid;
	}

	public void reset() {
		System.arraycopy(givens, 0, guesses, 0, givens.length);
	}

	public void setGiven(int number, int row, int column) {
		setGuess(number, row, column);
		givens[indexOf(row, column, size)] = number;
	}

	public void setGuess(int number, int row, int column) {
		validateNumber(number, size);

		if (isGiven(row, column)) {
			throw new IllegalArgumentException("Attempt to overwrite given "
					+ "number at " + format(row, column)
					+ " with " + format(number));
		}

		guesses[indexOf(row, column, size)] = number;
	}

	public int getSize() {
		return size;
	}

	public int getNumber(int row, int column) {
		return guesses[indexOf(row, column, size)];
	}

	public boolean isGiven(int row, int column) {
		return givens[indexOf(row, column, size)] > 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(guesses.length);
		for (int n : guesses) {
			sb.append(n > 0 ? Integer.toString(n) : " ");
		}

		return sb.toString();
	}
}
