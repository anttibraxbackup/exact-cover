package fi.iki.asb.xcc.examples.sudoku;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SudokuSolverTest {

    private int solutionCount = 0;

    private SudokuGrid lastSolution = null;

    /**
     * We said we support arbitrary size puzzles as long as the square root
     * of the size is an integer... so puzzle of size 1 should work.
     *
     * <p>Technically we would also be able to support puzzles of size zero.
     * There wouldn't be any constraints so the XCC matrix is empty and the
     * recursive search hits the first <code>head.right == head</code>
     * test with an empty solution.</p>
     */
    @Test
    public void givenSize1Sudoku_findsTheSolution() {
        SudokuSolver solver = new SudokuSolver(1);
        SudokuGrid grid = new SudokuGrid(1);
        solver.solve(grid, this::solutionCounter);

        assertEquals(1, solutionCount);
        assertEquals(1, lastSolution.getNumber(0, 0));
    }

    @Test
    public void givenSize4Sudoku_findsAllSolutions() {
        SudokuSolver solver = new SudokuSolver(4);
        SudokuGrid grid = new SudokuGrid(4);
        solver.solve(grid, this::solutionCounter);

        assertEquals(288, solutionCount);
    }

    @Test
    public void givenSize4Sudoku_whenGivenNumbersExist_findsTheSolution() {
        SudokuSolver solver = new SudokuSolver(4);
        SudokuGrid grid = new SudokuGrid(4);
        grid.setGiven(1, 0, 0);
        grid.setGiven(4, 0, 2);
        grid.setGiven(1, 3, 1);
        grid.setGiven(2, 3, 3);

        // +-----+-----+
        // | 1 2 | 4 3 |
        // | 3 4 | 2 1 |
        // +-----+-----+
        // | 2 3 | 1 4 |
        // | 4 1 | 3 2 |
        // +-----+-----+

        solver.solve(grid, this::solutionCounter);
        assertEquals(1, solutionCount);
        assertEquals(2, lastSolution.getNumber(0, 1));
        assertEquals(3, lastSolution.getNumber(0, 3));
        assertEquals(3, lastSolution.getNumber(1, 0));
        assertEquals(4, lastSolution.getNumber(1, 1));
        assertEquals(2, lastSolution.getNumber(1, 2));
        assertEquals(1, lastSolution.getNumber(1, 3));
        assertEquals(2, lastSolution.getNumber(2, 0));
        assertEquals(3, lastSolution.getNumber(2, 1));
        assertEquals(1, lastSolution.getNumber(2, 2));
        assertEquals(4, lastSolution.getNumber(2, 3));
        assertEquals(4, lastSolution.getNumber(3, 0));
        assertEquals(3, lastSolution.getNumber(3, 2));
    }

    /**
     * Puzzle from the <a href="https://en.wikipedia.org/wiki/Sudoku">Sudoku
     * article at Wikipedia</a>.
     */
    @Test
    public void givenSize9Sudoku_findsTheSolution() {
        SudokuSolver solver = new SudokuSolver(9);
        SudokuGrid grid = SudokuGrid.parse(
                    "53  7    " +
                        "6  195   " +
                        " 98    6 " +
                        "8   6   3" +
                        "4  8 3  1" +
                        "7   2   6" +
                        " 6    28 " +
                        "   419  5" +
                        "    8  79");

        solver.solve(grid, this::solutionCounter, this::stopAfterFirstResult);

        assertEquals(1, solutionCount);
        assertEquals(4, lastSolution.getNumber(0, 2));
        assertEquals(2, lastSolution.getNumber(1, 2));
        assertEquals(9, lastSolution.getNumber(3, 2));
        assertEquals(6, lastSolution.getNumber(4, 2));
    }

    private void solutionCounter(SudokuGrid solution) {
        solutionCount++;
        lastSolution = solution;
    }

    private boolean stopAfterFirstResult() {
        return solutionCount > 0;
    }
}
