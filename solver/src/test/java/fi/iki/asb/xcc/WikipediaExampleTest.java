package fi.iki.asb.xcc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Basic tests based on the Wikipedia example.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X">Knuth%27s_Algorithm_X</a>
 */
public class WikipediaExampleTest {

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

    private final List<String> solutions = new ArrayList<>();

    private XCC<String> createSolver(Function<ItemProvider<String>, XCC<String>> init) {
        final XCC<String> xcc = init.apply(new WikipediaExample());
        xcc.addOption("A");
        xcc.addOption("B");
        xcc.addOption("C");
        xcc.addOption("D");
        xcc.addOption("E");
        xcc.addOption("F");
        return xcc;
    }

    private void solutionConsumer(List<String> solution) {
        solutions.add(String.join(",", solution));
    }

    private void exceptionThrowingSolutionConsumer(List<String> solution) {
        throw new RuntimeException("leaving matrix dirty");
    }

    // =================================================================== //
    // Happy path test.

    public void runBasicTest(XCC<String> xcc) {
        xcc.search(this::solutionConsumer);
        assertEquals(1, solutions.size());
        assertEquals("B,D,F", solutions.getFirst());
    }

    @Test
    public void givenLinkedXcc_shouldFindSolution() {
        runBasicTest(createSolver(LinkedXCC::new));
    }

    @Test
    public void givenReferenceXcc_shouldFindSolution() {
        runBasicTest(createSolver(ReferenceXCC::new));
    }

    // =================================================================== //
    // Happy path test can be run twice in a row.

    @Test
    public void givenLinkedXcc_shouldFindSolutionTwice() {
        XCC<String> solver = createSolver(LinkedXCC::new);
        runBasicTest(solver);
        solutions.clear();
        runBasicTest(solver);
    }

    @Test
    public void givenReferenceXcc_shouldFindSolutionTwice() {
        XCC<String> solver = createSolver(ReferenceXCC::new);
        runBasicTest(solver);
        solutions.clear();
        runBasicTest(solver);
    }

    // =================================================================== //
    // Test that solution is found with pre-selected options.

    public void runPreSelectedTest(XCC<String> xcc) {
        xcc.search(this::solutionConsumer, List.of("F"));
        assertEquals(1, solutions.size());
        // Order is different since pre-selected options are added to the
        // solution first.
        assertEquals("F,B,D", solutions.getFirst());
    }

    @Test
    public void givenLinkedXcc_withPreSelectedOptions_shouldFindSolution() {
        runPreSelectedTest(createSolver(LinkedXCC::new));
    }

    @Test
    public void givenReferenceXcc_withPreSelectedOptions_shouldFindSolution() {
        runPreSelectedTest(createSolver(ReferenceXCC::new));
    }

    // =================================================================== //
    // Test that the search cannot be performed if the matrix is left
    // dirty.

    public void runDirtyTest(XCC<String> xcc) {
        try {
            xcc.search(this::exceptionThrowingSolutionConsumer);
            fail();
        } catch (RuntimeException ex) {
            assertEquals("leaving matrix dirty", ex.getMessage());
        }

        assertTrue(xcc.isDirty());
        xcc.search(this::exceptionThrowingSolutionConsumer);
    }

    @Test(expected = IllegalStateException.class)
    public void givenLinkedXcc_withDirtyMatrix_shouldThrowException() {
        runDirtyTest(createSolver(LinkedXCC::new));
    }

    @Test(expected = IllegalStateException.class)
    public void givenReferenceXcc_withDirtyMatrix_shouldThrowException() {
        runDirtyTest(createSolver(ReferenceXCC::new));
    }
}
