package fi.iki.asb.xcc.examples.queen;

import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.examples.queen.option.QueenPlacement;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Solver for the N Queens problem. See the
 * <a href="https://en.wikipedia.org/wiki/Eight_queens_puzzle">
 *     Wikipedia artile</a> about the subject.
 */
public final class QueenSolver {

	private final int size;

	private final XCC<QueenPlacement> xcc;

	private final Consumer<QueenGrid> solutionConsumer;

	public QueenSolver(
			final int size,
			final Consumer<QueenGrid> solutionConsumer,
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		this.size = size;
		this.xcc = xccInitializer.apply(new QueenItemProvider(size));
		this.solutionConsumer = solutionConsumer;
		initializeOptions();
	}

	public QueenSolver(
			final int size,
			final Consumer<QueenGrid> solutionConsumer) {
		this(size, solutionConsumer, LinkedXCC::new);
	}

	/**
	 * Initialize the options. This adds options for placing a queen to
	 * each square on the chess board.
	 */
	private void initializeOptions() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				xcc.addOption(new QueenPlacement(row, col));
			}
		}
	}

	public void solve() {
		xcc.search(this::acceptSolution);
	}

	/**
	 * Accept the solution and convert the queen placements into a
	 * two-dimensional chess board representation.
	 */
	private void acceptSolution(List<QueenPlacement> solution) {
		final QueenGrid grid = new QueenGrid(size);
		solution.forEach(pl -> pl.placeOn(grid));
		solutionConsumer.accept(grid);
	}
}
