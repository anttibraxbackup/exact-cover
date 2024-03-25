package fi.iki.asb.xcc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ColoredSecondaryItemTest {

    /**
     * Item provider with two solutions: A,B and D,E. The colored secondary
     * items prevent options C and F from being selected.
     */
    private static class ColoredSolutionExample implements ItemProvider<String> {
        @Override
        public Collection<Object> from(String rowValue) {
            return switch (rowValue) {
                case "A" -> asList(0, 1, 2, s(5, 8), s(6, 7));
                case "B" -> asList(3, 4, s(5, 8));
                case "C" -> asList(3, 4, s(5, 9));

                case "D" -> asList(0, 1, s(5, 9), s(6, 7));
                case "E" -> asList(2, 3, 4, s(5, 9));
                case "F" -> asList(2, 3, 4, s(5, 8));
                default -> Collections.emptyList();
            };
        }

        private Object s(int value, int color) {
            return new TestSecondaryItem(value, color);
        }
    }

    private record TestSecondaryItem(int item, int color)
    implements SecondaryItem{

        @Override
        public Object getColor() {
            return color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return item == ((TestSecondaryItem) o).item;
        }

        @Override
        public int hashCode() {
            return Objects.hash(item);
        }
    }

    private final List<String> solutions = new ArrayList<>();

    private XCC<String> createSolver(Function<ItemProvider<String>, XCC<String>> init) {
        final XCC<String> xcc = init.apply(new ColoredSolutionExample());
        xcc.addOption("A");
        xcc.addOption("B");
        xcc.addOption("C");
        xcc.addOption("D");
        xcc.addOption("E");
        xcc.addOption("F");
        return xcc;
    }

    // =================================================================== //
    // Test that colored secondary items work.

    private void solutionConsumer(List<String> solution) {
        solutions.add(String.join(",", solution));
    }

    public void runColoredTest(XCC<String> xcc) {
        xcc.search(this::solutionConsumer);

        assertEquals(2, solutions.size());
        assertEquals("A,B", solutions.getFirst());
        assertEquals("D,E", solutions.getLast());
    }

    @Test
    public void givenLinkedXcc_shouldNotIncludeWrongColor() {
        runColoredTest(createSolver(LinkedXCC::new));
    }

    @Test
    public void givenReferenceXcc_shouldNotIncludeWrongColor() {
        runColoredTest(createSolver(ReferenceXCC::new));
    }
}
