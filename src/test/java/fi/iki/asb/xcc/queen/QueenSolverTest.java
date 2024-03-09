package fi.iki.asb.xcc.queen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class QueenSolverTest {
	
	private List<String> solutions = new ArrayList<>();

	@Test
	public void givenSize4Board_shouldFindBothSolutions() {
		QueenSolver solver = new QueenSolver(4, this::solutionCounter);
		solver.solve();

		assertEquals(2, solutions.size());
		assertTrue(solutions.contains("1302"));
		assertTrue(solutions.contains("2031"));
	}

	@Test
	public void givenSize8Board_shouldFindAllSolutions() {
		QueenSolver solver = new QueenSolver(8, this::solutionCounter);
		solver.solve();

		assertEquals(92, solutions.size());
	}

	@Test
	public void givenSize12Board_shouldFindAllSolutions() {
		QueenSolver solver = new QueenSolver(12, this::solutionCounter);
		solver.solve();

		assertEquals(14200, solutions.size());
	}

	@Test
	@Ignore("Takes too long")
	public void givenSize14Board_shouldFindAllSolutions() {
		QueenSolver solver = new QueenSolver(14, this::solutionCounter);
		solver.solve();

		assertEquals(365596, solutions.size());
	}

	private void solutionCounter(QueenGrid grid) {
		solutions.add(grid.identity());
	}
}
