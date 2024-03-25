package fi.iki.asb.xcc.examples.words;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.ReferenceXCC;
import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.examples.words.option.WordPlacement;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class WordsSolverTest {

    // =================================================================== //
    // Trivial test with one result.

    private void runTrivialTest(final Function<WordsItemProvider, XCC<WordPlacement>> xccInitializer) {
        final WordsSolver solver = new WordsSolver(3, 2, xccInitializer);
        solver.addWord("abc");
        solver.addWord("def");
        solver.addWord("xxx");

        solver.addWord("ad");
        solver.addWord("be");
        solver.addWord("cf");
        solver.addWord("yy");

        solver.solve();
        assertEquals("abcdef", new String(solver.getSolution()));
    }

    @Test
    public void givenLinkedXcc_shouldFindSolutionToTrivialExample() {
        runTrivialTest(LinkedXCC::new);
    }

    @Test
    public void givenReferenceXcc_shouldFindSolutionToTrivialExample() {
        runTrivialTest(ReferenceXCC::new);
    }

    // =================================================================== //
    // Test with Finnish four letter words.

    private void runFinnish4x4Test(final Function<WordsItemProvider, XCC<WordPlacement>> xccInitializer)
            throws IOException {
        final WordsSolver solver = new WordsSolver(4, 4, xccInitializer);

        Files.lines(Path.of("src/test/resources/sanat4.txt"), StandardCharsets.UTF_8)
                .map(String::toLowerCase)
                .forEach(solver::addWord);

        final Set<String> expected = new HashSet<>();
        expected.add("ahtiliedtukeasua");
        expected.add("äppihuutkusiehiö");

        solver.solve();
        assertTrue(expected.contains(new String(solver.getSolution())));
    }

    @Test
    public void givenLinkedXcc_shouldFindSolutionToFinnish4x4Box()
            throws IOException {
        runFinnish4x4Test(LinkedXCC::new);
    }

    @Test
    public void givenReferenceXcc_shouldFindSolutionToFinnish4x4Box()
            throws IOException {
        runFinnish4x4Test(ReferenceXCC::new);
    }
}
