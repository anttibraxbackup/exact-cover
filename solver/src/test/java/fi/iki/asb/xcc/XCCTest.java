package fi.iki.asb.xcc;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class XCCTest {

	// From https://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X
	private static class WikipediaExample implements ItemProvider<String> {
		@Override
		public Collection<Object> from(String rowValue) {
			return switch (rowValue) {
				case "A" -> asList(0, 3, 6);
				case "B" -> asList(0, 3);
				case "C" -> asList(3, 4, 6);
				case "D" -> asList(2, 4, 5);
				case "E" -> asList(1, 2, 5, 6);
				case "F" -> asList(1, 6);
				default -> Collections.emptyList();
			};
		}
	}

	private static class NoSolution implements ItemProvider<String> {
		@Override
		public Collection<Object> from(String rowValue) {
			return switch (rowValue) {
				case "A" -> asList(0      );
				case "B" -> asList(0, 8   );
				case "C" -> asList(0,   -7);
				default -> Collections.emptyList();
			};
		}
	}

	private final List<String> lastSolution = new ArrayList<>();

	private XCC<String> createWikipediaExample() {
		final XCC<String> xcc = new LinkedXCC<>(new WikipediaExample());
		xcc.addOption("A");
		xcc.addOption("B");
		xcc.addOption("C");
		xcc.addOption("D");
		xcc.addOption("E");
		xcc.addOption("F");
		return xcc;
	}

	@Test
	public void givenWikipediaExample_shouldFindSolution() {
		final XCC<String> xcc = createWikipediaExample();
		xcc.search(this::solutionConsumer);

		assertEquals(3, lastSolution.size());
		assertEquals("B", lastSolution.get(0));
		assertEquals("D", lastSolution.get(1));
		assertEquals("F", lastSolution.get(2));
	}

	@Test
	public void givenImpossibleUniverse_shouldNotFindSolution() {
		final XCC<String> xcc = new LinkedXCC<>(new NoSolution());
		xcc.addOption("A");
		xcc.addOption("B");
		xcc.addOption("C");
		xcc.search(this::solutionConsumer);

		assertEquals(0, lastSolution.size());
	}

	@Test
	public void givenDirtyMatrix_shouldFailImmediately() {
		final XCC<String> xcc = createWikipediaExample();

		// First run with a failing solution consumer to
		// make the matrix dirty.
		try {
			xcc.search(this::failingSolutionConsumer);
			fail("Expected a RuntimeException");
		} catch (RuntimeException ex) {
			// Ok.
		}

		try {
			xcc.search(this::solutionConsumer);
			fail("Expected an IllegalStateException");
		} catch (IllegalStateException ex) {
			// Ok.
		}

		// Make sure no solution was found.
		assertEquals(0, lastSolution.size());
	}

	private void solutionConsumer(List<String> solution) {
		lastSolution.clear();
        lastSolution.addAll(solution);
	}

	private void failingSolutionConsumer(List<String> solution) {
		throw new RuntimeException("interrupted");
	}
}
