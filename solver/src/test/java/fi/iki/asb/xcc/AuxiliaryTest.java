package fi.iki.asb.xcc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for auxiliary features (emergency brake and tracing).
 */
public class AuxiliaryTest {

    /**
     * Item provider with two solutions: A,B and C,D.
     */
    private static class MultipleSolutionExample implements ItemProvider<String> {
        @Override
        public Collection<Object> from(String rowValue) {
            return switch (rowValue) {
                case "A" -> asList(0, 1, 2);
                case "B" -> asList(3, 4);
                case "C" -> asList(0, 1);
                case "D" -> asList(2, 3, 4);
                default -> Collections.emptyList();
            };
        }
    }

    private static class Tracer implements XCCTrace {

        int onSearchStarted = 0;

        int onRecursionEntered = 0;

        int totalItemCount = 0;

        int onItemSelected = 0;

        int onRecursionEnded = 0;


        @Override
        public void onSearchStarted() {
            onSearchStarted++;
        }

        @Override
        public void onRecursionEntered(int itemCount) {
            onRecursionEntered++;
            totalItemCount += itemCount;
        }

        @Override
        public void onItemSelected() {
            onItemSelected++;

        }

        @Override
        public void onRecursionEnded() {
            onRecursionEnded++;
        }
    }

    private final List<String> solutions = new ArrayList<>();

    private XCC<String> createSolver(Function<ItemProvider<String>, XCC<String>> init) {
        final XCC<String> xcc = init.apply(new MultipleSolutionExample());
        xcc.addOption("A");
        xcc.addOption("B");
        xcc.addOption("C");
        xcc.addOption("D");
        return xcc;
    }

    // =================================================================== //
    // Test that emergency brake works.

    private final AtomicBoolean emergencyBrake = new AtomicBoolean(false);

    private void emergencyBrakingSolutionConsumer(List<String> solution) {
        solutions.add(String.join(",", solution));
        emergencyBrake.set(true);
    }

    public void runEmergencyBrakeTest(XCC<String> xcc) {
        xcc.search(this::emergencyBrakingSolutionConsumer, emergencyBrake::get);
        assertFalse(xcc.isDirty());
        assertEquals(1, solutions.size());
        assertEquals("A,B", solutions.getFirst());
    }

    @Test
    public void givenLinkedXcc_shouldPullEmergencyBrake() {
        runEmergencyBrakeTest(createSolver(LinkedXCC::new));
    }

    @Test
    public void givenReferenceXcc_shouldPullEmergencyBrake() {
        runEmergencyBrakeTest(createSolver(ReferenceXCC::new));
    }

    // =================================================================== //
    // Test that trace is called.

    private void solutionConsumer(List<String> solution) {
        solutions.add(String.join(",", solution));
    }

    public void runTracingTest(XCC<String> xcc) {
        Tracer tracer = new Tracer();
        xcc.setTrace(tracer);
        xcc.search(this::solutionConsumer);

        assertEquals(2, solutions.size());
        assertEquals("A,B", solutions.getFirst());
        assertEquals("C,D", solutions.getLast());

        assertEquals(1, tracer.onSearchStarted);
        assertEquals(3, tracer.onRecursionEntered);
        assertEquals(4, tracer.totalItemCount);
        assertEquals(3, tracer.onRecursionEnded);
    }

    @Test
    public void givenLinkedXcc_shouldCallTracer() {
        runTracingTest(createSolver(LinkedXCC::new));
    }

    @Test
    public void givenReferenceXcc_shouldCallTracer() {
        runTracingTest(createSolver(ReferenceXCC::new));
    }

}
