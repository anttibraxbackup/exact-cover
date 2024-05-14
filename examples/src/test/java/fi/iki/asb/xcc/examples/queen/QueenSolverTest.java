package fi.iki.asb.xcc.examples.queen;

import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.ReferenceXCC;
import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.examples.queen.option.QueenPlacement;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class QueenSolverTest {

	private int solutionCount = 0;

	private QueenSolver solver;

	private void solutionCounter(QueenGrid grid) {
		solutionCount++;
	}

	protected int solve(final int expectedSolutionCount) {
		solutionCount = 0;
		solver.solve();
		assertEquals(expectedSolutionCount, solutionCount);
		return solutionCount;
	}

	// =================================================================== //
	// Find "solution" to size 0 board.
	// Yes, this is a bit stupid.

	protected void initSize0Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		solver = new QueenSolver(0, this::solutionCounter, xccInitializer);
	}

	@Test
	public void givenLinkedXcc_shouldFindSolutionToSize0Board() {
		initSize0Test(LinkedXCC::new);
		solve(1);
	}

	@Test
	public void givenReferenceXcc_shouldFindSolutionToSize0Board() {
		initSize0Test(ReferenceXCC::new);
		solve(1);
	}

	// =================================================================== //
	// Find solution to size 1 board.

	protected void initSize1Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		solver = new QueenSolver(1, this::solutionCounter, xccInitializer);
	}

	@Test
	public void givenLinkedXcc_shouldFindSolutionToSize1Board() {
		initSize1Test(LinkedXCC::new);
		solve(1);
	}

	@Test
	public void givenReferenceXcc_shouldFindSolutionToSize1Board() {
		initSize1Test(ReferenceXCC::new);
		solve(1);
	}

	// =================================================================== //
	// Find both solutions to size 4 board.

	protected void initSize4Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		solver = new QueenSolver(4, this::solutionCounter, xccInitializer);
	}

	@Test
	public void givenLinkedXcc_shouldFindAllSolutionsToSize4Board() {
		initSize4Test(LinkedXCC::new);
		solve(2);
	}

	@Test
	public void givenReferenceXcc_shouldFindAllSolutionsToSize4Board() {
		initSize4Test(ReferenceXCC::new);
		solve(2);
	}

	// =================================================================== //
	// Find all solutions to size 8 board.

	protected void initSize8Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		solver = new QueenSolver(8, this::solutionCounter, xccInitializer);
	}

	@Test
	public void givenLinkedXcc_shouldFindAllSolutionsToSize8Board() {
		initSize8Test(LinkedXCC::new);
		solve(92);
	}

	@Test
	public void givenReferenceXcc_shouldFindAllSolutionsToSize8Board() {
		initSize8Test(ReferenceXCC::new);
		solve(92);
	}
}
