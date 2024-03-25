package fi.iki.asb.xcc.examples.words;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Ignore
public class WordsSolverTest {

    @Test
    public void givenSimpleInput_shouldFindSolution() {
        WordsSolver solver = new WordsSolver(3, 2);

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
    public void givenFinnish4LetterWords_shouldFindSolution() throws IOException {
        WordsSolver solver = new WordsSolver(4, 4);

        Files.lines(Path.of("src/test/resources/sanat4.txt"), StandardCharsets.UTF_8)
                .map(String::toLowerCase)
                .forEach(solver::addWord);

        final Set<String> expected = new HashSet<>();
        expected.add("ahtiliedtukeasua");
        expected.add("äppihuutkusiehiö");

        solver.solve();
        assertTrue(expected.contains(new String(solver.getSolution())));
    }
}
