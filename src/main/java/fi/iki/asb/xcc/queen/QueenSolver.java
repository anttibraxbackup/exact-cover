package fi.iki.asb.xcc.queen;

import fi.iki.asb.xcc.DLX;
import fi.iki.asb.xcc.LinkedDLX;

import java.util.List;
import java.util.function.Consumer;

/**
 * Solver for the N Queens problem. See the
 * <a href="https://en.wikipedia.org/wiki/Eight_queens_puzzle">
 *     Wikipedia artile</a> about the subject.
 */
public class QueenSolver {
	
	private final int size;

	private final DLX<QueenPlacement> dlx;

	private final Consumer<QueenGrid> solutionConsumer;

	public QueenSolver(
			final int size,
			final Consumer<QueenGrid> solutionConsumer) {
		this.size = size;
		this.dlx = new LinkedDLX<>(new QueenMapper(size));
		this.solutionConsumer = solutionConsumer;

		initializeConstraints();
	}

	private void initializeConstraints() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				dlx.addOption(new QueenPlacement(row, col));
			}
		}
	}

	public void solve() {
		dlx.search(this::acceptSolution);
	}

	private void acceptSolution(List<QueenPlacement> solution) {
		final QueenGrid grid = new QueenGrid(size);
		solution.forEach(pl -> pl.placeOn(grid));
		solutionConsumer.accept(grid);
	}
		
}
