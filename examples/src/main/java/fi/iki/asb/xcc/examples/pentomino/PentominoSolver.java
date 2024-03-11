package fi.iki.asb.xcc.examples.pentomino;

import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.LinkedXCC;

import java.util.List;
import java.util.function.Consumer;

public class PentominoSolver {

	private final int height;
	private final int width;
	private final XCC<PentominoPlacement> xcc;
	private final Consumer<PentominoGrid> solutionConsumer;

	public PentominoSolver(
			final int width,
			final Consumer<PentominoGrid> solutionConsumer) {
		height = 60 / width;
		if (height * width != 60) {
			throw new IllegalArgumentException("Illegal width [" + width + "]");
		}

		this.width = width;
		this.xcc = new LinkedXCC<>(new PentominoMapper());
		this.solutionConsumer = solutionConsumer;

		initializeConstraints();
	}

	public void solve() {
		xcc.search(this::acceptSolution);
	}

	private void initializeConstraints() {
		rotate3AndFlip(Pentominoes.F);
		rotate1(Pentominoes.I);
		rotate3AndFlip(Pentominoes.L);
		rotate3AndFlip(Pentominoes.N);
		rotate3AndFlip(Pentominoes.P);
		rotate3(Pentominoes.T);
		rotate3(Pentominoes.U);
		rotate3(Pentominoes.V);
		rotate3(Pentominoes.W);
		createPentominoPlacements(Pentominoes.X);
		rotate3AndFlip(Pentominoes.Y);
		rotate1AndFlip(Pentominoes.Z);
	}

	private void rotate1(Pentomino p) {
		createPentominoPlacements(p.rotate());
		createPentominoPlacements(p);
	}

	private void rotate1AndFlip(Pentomino p) {
		rotate1(p);
		rotate1(p.flip());
	}

	private void rotate3AndFlip(Pentomino p) {
		rotate3(p);
		rotate3(p.flip());
	}

	private void rotate3(Pentomino p) {
		createPentominoPlacements(p);
		createPentominoPlacements(p = p.rotate());
		createPentominoPlacements(p = p.rotate());
		createPentominoPlacements(p.rotate());
	}

	private void createPentominoPlacements(Pentomino p) {
		final int maxRow = height - p.height() + 1;
		final int maxCol = width - p.width() + 1;

		for (int row = 0; row < maxRow; row++) {
			for (int col = 0; col < maxCol; col++) {
				xcc.addOption(new PentominoPlacement(p, row, col));
			}
		}
	}

	private void acceptSolution(List<PentominoPlacement> solution) {
		final PentominoGrid grid = new PentominoGrid(width);
		solution.forEach(pl -> pl.placeOn(grid));
		solutionConsumer.accept(grid);
	}
}
