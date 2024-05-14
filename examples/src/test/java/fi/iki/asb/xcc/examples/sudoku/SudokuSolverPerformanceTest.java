package fi.iki.asb.xcc.examples.sudoku;

import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.ReferenceXCC;
import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.examples.sudoku.option.PlaceNumber;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class SudokuSolverPerformanceTest {

    private int solutionCount = 0;

    private void solutionCounter(SudokuGrid solution) {
        solutionCount++;
    }

    // =================================================================== //
    // Finds all solutions to size 4 Sudoku

    private void runSize9SudokuTest(
            final Function<SudokuItemProvider, XCC<PlaceNumber>> xccInitializer) {
        SudokuSolver solver = new SudokuSolver(9, xccInitializer);
        SudokuGrid grid = new SudokuGrid(9);
        solver.solve(grid, this::solutionCounter, () -> solutionCount >= 10000000);
    }

    @Test
    public void givenLinkedXCC_findsAllSolutionsToSize9Sudoku() {
        runSize9SudokuTest(LinkedXCC::new);
    }

    @Test
    public void givenReferenceXCC_findsAllSolutionsToSize9Sudoku() {
        runSize9SudokuTest(ReferenceXCC::new);
    }

}
