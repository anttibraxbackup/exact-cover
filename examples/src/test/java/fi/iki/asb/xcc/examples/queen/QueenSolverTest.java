package fi.iki.asb.xcc.examples.queen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.ReferenceXCC;
import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.examples.queen.option.QueenPlacement;
import org.junit.Test;

public class QueenSolverTest {

	private final List<String> solutions = new ArrayList<>();

	private void solutionCounter(QueenGrid grid) {
		solutions.add(grid.identity());
	}

	// =================================================================== //
	// Find "solution" to size 0 board.

	private void runSize0Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		final QueenSolver solver = new QueenSolver(0, this::solutionCounter, xccInitializer);
		solver.solve();

		assertEquals(1, solutions.size());
		assertTrue(solutions.contains(""));
	}

	@Test
	public void givenLinkedXcc_shouldFindSolutionToSize0Board() {
		runSize0Test(LinkedXCC::new);
	}

	@Test
	public void givenReferenceXcc_shouldFindSolutionToSize0Board() {
		runSize0Test(ReferenceXCC::new);
	}

	// =================================================================== //
	// Find solution to size 1 board.

	private void runSize1Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		final QueenSolver solver = new QueenSolver(1, this::solutionCounter, xccInitializer);
		solver.solve();

		assertEquals(1, solutions.size());
		assertTrue(solutions.contains("0"));
	}

	@Test
	public void givenLinkedXcc_shouldFindSolutionToSize1Board() {
		runSize1Test(LinkedXCC::new);
	}

	@Test
	public void givenReferenceXcc_shouldFindSolutionToSize1Board() {
		runSize1Test(ReferenceXCC::new);
	}

	// =================================================================== //
	// Find both solutions to size 4 board.

	private void runSize4Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		final QueenSolver solver = new QueenSolver(4, this::solutionCounter, xccInitializer);
		solver.solve();

		assertEquals(2, solutions.size());
		assertTrue(solutions.contains("1302"));
		assertTrue(solutions.contains("2031"));
	}

	@Test
	public void givenLinkedXcc_shouldFindAllSolutionsToSize4Board() {
		runSize4Test(LinkedXCC::new);
	}

	@Test
	public void givenReferenceXcc_shouldFindAllSolutionsToSize4Board() {
		runSize4Test(ReferenceXCC::new);
	}

	// =================================================================== //
	// Find all solutions to size 8 board.

	private void runSize8Test(
			final Function<QueenItemProvider, XCC<QueenPlacement>> xccInitializer) {
		final QueenSolver solver = new QueenSolver(8, this::solutionCounter, xccInitializer);
		solver.solve();

		assertEquals(92, solutions.size());
		assertTrue(solutions.contains("53607142"));
	}

	@Test
	public void givenLinkedXcc_shouldFindAllSolutionsToSize8Board() {
		runSize8Test(LinkedXCC::new);
	}

	@Test
	public void givenReferenceXcc_shouldFindAllSolutionsToSize8Board() {
		runSize8Test(ReferenceXCC::new);
	}
}
