package fi.iki.asb.xcc.examples.pentomino;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PentominoSolverTest {
	
	private int solutionCount = 0;

	@Test
	public void given3x20Puzzle_shouldFindAllSolutions() {
		PentominoSolver solver = new PentominoSolver(
				20, this::solutionCounter);
		solver.solve();

		// 2 unique solutions excluding rotations and reflections.
		assertEquals(4 * 2, solutionCount);
	}

	@Test
	public void given4x15Puzzle_shouldFindAllSolutions() {
		PentominoSolver solver = new PentominoSolver(
				15, this::solutionCounter);
		solver.solve();

		// 368 unique solutions excluding rotations and reflections.
		assertEquals(4 * 368, solutionCount);
	}

	/**
	 * This takes about 25 seconds.
	 */
	@Test
	public void given6x10Puzzle_shouldFindAllSolutions() {
		PentominoSolver solver = new PentominoSolver(
				10, this::solutionCounter);
		solver.solve();

		// 2339 unique solutions excluding rotations and reflections.
		assertEquals(4 * 2339, solutionCount);
	}

	private void solutionCounter(PentominoGrid grid) {
		solutionCount++;

		// System.out.println(grid);
	}
}
