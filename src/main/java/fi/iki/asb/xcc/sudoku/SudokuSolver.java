package fi.iki.asb.xcc.sudoku;

import static fi.iki.asb.xcc.sudoku.Sudoku.format;

import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.LinkedXCC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Solver for an arbitrary size sudoku puzzle.
 * 
 * <p>This class is <i>not thread safe</i>.</p>
 */
public class SudokuSolver {

	/**
	 * Map a generic XCC Solution to a SudokuGrid.
	 */
	private static class SolutionMapper implements
			Consumer<List<SudokuCell>> {
		final SudokuGrid grid;
		final Consumer<SudokuGrid> sudokuGridConsumer;

		public SolutionMapper(
				final SudokuGrid grid,
				final Consumer<SudokuGrid> sudokuGridConsumer) {
			this.grid = grid;
			this.sudokuGridConsumer = sudokuGridConsumer;
		}

		@Override
		public void accept(final List<SudokuCell> solution) {
			grid.reset();
			solution.forEach(sc -> {
				final int row = sc.row();
				final int column = sc.column();

				if (! grid.isGiven(row, column)) {
					grid.setGuess(sc.number(), row, column);
				} else {
					if (grid.getNumber(row, column) != sc.number()) {
						throw new IllegalArgumentException("Attempt "
								+ "to overwrite given at "
								+ format(row, column) + " with "
								+ format(sc.number()));
					}
				}
			});
			sudokuGridConsumer.accept(grid);
		}
	}

	private final XCC<SudokuCell> xcc;

	private final int size;

	public SudokuSolver(final int size) {
		this.xcc = new LinkedXCC<>(new SudokuOptionMapper(size));
		this.size = size;
		initializeConstraints();
	}

	private void initializeConstraints() {
		for (int row = 1; row <= size; row++) {
			for (int col = 1; col <= size; col++) {
				for (int number = 1; number <= size; number++) {
					xcc.addOption(new SudokuCell(number, row, col));
				}
			}
		}
	}

	/**
	 * Find all solutions to the sudoku grid.
	 */
	public void solve(
			final SudokuGrid sudoku,
			final Consumer<SudokuGrid> sudokuGridConsumer) {
		solve(sudoku, sudokuGridConsumer, () -> false);
	}

	/**
	 * Find solutions to the sudoku grid until the emergency brake
	 * is pulled.
	 */
	public void solve(
			final SudokuGrid grid,
			final Consumer<SudokuGrid> sudokuGridConsumer,
			final BooleanSupplier emergencyBrake) {

		if (grid.getSize() != size) {
			throw new IllegalArgumentException("Incompatible grid size "
					+ format(grid.getSize()) + " expected " + format(size));
		}

		// Load the given numbers into a list, so they can be passed on
		// to XCC.
		final List<SudokuCell> givenNumbers = new ArrayList<>();
		for (int row = 1; row <= size; row++) {
			for (int col = 1; col <= size; col++) {
				int number = grid.getNumber(row, col);
				if (number > 0) {
					givenNumbers.add(new SudokuCell(number, row, col));
				}
			}
		}

		final Consumer<List<SudokuCell>> solutionMapper =
				new SolutionMapper(grid, sudokuGridConsumer);

		xcc.search(solutionMapper, givenNumbers, emergencyBrake);
	}
}
