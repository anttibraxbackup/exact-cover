package fi.iki.asb.xcc.examples.queen;

import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.ReferenceXCC;
import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.examples.queen.option.QueenPlacement;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

/**
 * Performance test that uses a "bare bones" N queens solver, which does
 * not waste time on generating a board from the chosen queen placements.
 */
public class QueenSolverPerf {

    private static final int SIZE = 8;

    // =================================================================== //

    private abstract static class QueenSolverState {
        protected XCC<QueenPlacement> xcc;

        protected abstract XCC<QueenPlacement> getXccInitializer();

        @Setup
        public void setUp() {
            xcc = getXccInitializer();
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    xcc.addOption(new QueenPlacement(row, col));
                }
            }
        }

        public void solve(Blackhole sink) {
            xcc.search(sink::consume);
        }
    }

    @State(Scope.Benchmark)
    public static class LinkedSolverState extends QueenSolverState {
        public XCC<QueenPlacement> getXccInitializer() {
            return new LinkedXCC<>(new QueenItemProvider(SIZE));
        }
    }

    @State(Scope.Benchmark)
    public static class ReferenceSolverState extends QueenSolverState {
        public XCC<QueenPlacement> getXccInitializer() {
            return new ReferenceXCC<>(new QueenItemProvider(SIZE));
        }
    }

    // =================================================================== //

    @Benchmark
    public void testLinkedSolver(LinkedSolverState state, Blackhole sink) {
        state.solve(sink);
    }

    @Benchmark
    public void testReferenceSolver(ReferenceSolverState state, Blackhole sink) {
        state.solve(sink);
    }

    public static void main(String[] args) throws IOException {
        Main.main(args);
    }
}
