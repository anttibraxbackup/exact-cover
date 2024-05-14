package fi.iki.asb.xcc.examples.pentomino;

import static org.junit.Assert.assertEquals;

import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.ReferenceXCC;
import fi.iki.asb.xcc.XCC;
import org.junit.Test;

import java.util.function.Function;

public class PentominoSolverTest {

	private int solutionCount = 0;

	private PentominoSolver solver;

	/**
	 * Count number of solutions. Used as a solution consumer lambda.
	 */
	private void solutionCounter(PentominoGrid grid) {
		solutionCount++;
	}

	/**
	 * Run solver.
	 *
	 * @param expectedSolutionCount Expected number of solutions.
	 */
	private void solve(final int expectedSolutionCount) {
		solutionCount = 0;
		solver.solve();
		assertEquals(expectedSolutionCount, solutionCount);
	}

	// =================================================================== //
	// Find solutions to size 3x20 board.

	private void init3x20Test(final Function<PentominoItemProvider, XCC<PentominoPlacement>> xccInitializer) {
		solver = new PentominoSolver(20, this::solutionCounter, xccInitializer);
	}

	@Test
	public void givenLinkedXcc_shouldFindSolutionsTo3x20Puzzle() {
		init3x20Test(LinkedXCC::new);
		// 2 unique solutions excluding rotations and reflections.
		solve(4 * 2);
	}

	@Test
	public void givenReferenceXcc_shouldFindSolutionsTo3x20Puzzle() {
		init3x20Test(ReferenceXCC::new);
		// 2 unique solutions excluding rotations and reflections.
		solve(4 * 2);
	}

	// =================================================================== //
	// Find solutions to size 4x15 board.

	private void init4x15Test(final Function<PentominoItemProvider, XCC<PentominoPlacement>> xccInitializer) {
		solver = new PentominoSolver(15, this::solutionCounter, xccInitializer);
	}

	@Test
	public void givenLinkedXcc_shouldFindSolutionsTo4x15Puzzle() {
		init4x15Test(LinkedXCC::new);
		// 368 unique solutions excluding rotations and reflections.
		solve(4 * 368);
	}

	@Test
	public void givenReferenceXcc_shouldFindSolutionsTo4x15Puzzle() {
		init4x15Test(ReferenceXCC::new);
		// 368 unique solutions excluding rotations and reflections.
		solve(4 * 368);
	}

	// =================================================================== //
	// Find solutions to size 4x15 board.

	private void init6x10Test(final Function<PentominoItemProvider, XCC<PentominoPlacement>> xccInitializer) {
		solver = new PentominoSolver(10, this::solutionCounter, xccInitializer);
	}

	@Test
	public void givenLinkedXcc_shouldFindSolutionsTo6x10Puzzle() {
		init6x10Test(LinkedXCC::new);
		// 2339 unique solutions excluding rotations and reflections.
		solve(4 * 2339);
	}

	@Test
	public void givenReferenceXcc_shouldFindSolutionsTo6x10Puzzle() {
		init6x10Test(ReferenceXCC::new);
		// 2339 unique solutions excluding rotations and reflections.
		solve(4 * 2339);
	}
}
