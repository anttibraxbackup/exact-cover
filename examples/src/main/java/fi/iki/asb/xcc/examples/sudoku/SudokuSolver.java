package fi.iki.asb.xcc.examples.sudoku;

import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.examples.sudoku.option.PlaceNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A brute force solver for an arbitrary size sudoku puzzle. A brute force
 * sudoku solver beats the purpose of the puzzle, which is using logic to
 * deduct the correct solution.
 *
 * <p>This class is <i>not thread safe</i>.</p>
 */
public class SudokuSolver {

    /**
     * Map a generic XCC Solution to a SudokuGrid.
     */
    private static class SolutionMapper
            implements Consumer<List<PlaceNumber>> {
        final SudokuGrid solution;
        final Consumer<SudokuGrid> sudokuGridConsumer;

        public SolutionMapper(
                final SudokuGrid solution,
                final Consumer<SudokuGrid> sudokuGridConsumer) {
            this.solution = solution;
            this.sudokuGridConsumer = sudokuGridConsumer;
        }

        @Override
        public void accept(final List<PlaceNumber> options) {
            solution.reset();
            options.forEach(sc -> {
                final int row = sc.row();
                final int column = sc.column();

                if (! solution.isGiven(row, column)) {
                    solution.setGuess(sc.number(), row, column);
                } else {
                    if (solution.getNumber(row, column) != sc.number()) {
                        throw new IllegalArgumentException("Attempt "
                                + "to overwrite given at [" + row + ","
                                + column + "] with [" + sc.number() + "]");
                    }
                }
            });
            sudokuGridConsumer.accept(solution);
        }
    }

    private final XCC<PlaceNumber> xcc;

    private final SudokuGrid solution;

    public SudokuSolver(int size) {
        this.solution = new SudokuGrid(size);
        this.xcc = new LinkedXCC<>(new SudokuItemProvider(size));
        initializeConstraints();
    }

    private void initializeConstraints() {
        final int size = solution.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                for (int number = 1; number <= size; number++) {
                    xcc.addOption(new PlaceNumber(number, row, col));
                }
            }
        }
    }

    /**
     * Find all solutions to the empty sudoku grid.
     */
    public void solve(
            final Consumer<SudokuGrid> sudokuGridConsumer) {
        solve(new SudokuGrid(solution.getSize()),
                sudokuGridConsumer,
                () -> false);
    }

    /**
     * Find solutions to the empty sudoku grid until the
     * emergency brake is pulled.
     */
    public void solve(
            final Consumer<SudokuGrid> sudokuGridConsumer,
            final BooleanSupplier emergencyBrake) {
        solve(new SudokuGrid(solution.getSize()),
                sudokuGridConsumer,
                emergencyBrake);
    }

    /**
     * Find all solutions to the sudoku grid.
     */
    public void solve(
            final SudokuGrid givenNumbers,
            final Consumer<SudokuGrid> sudokuGridConsumer) {
        solve(givenNumbers,
                sudokuGridConsumer,
                () -> false);
    }

    /**
     * Find solutions to the sudoku grid until the emergency brake
     * is pulled.
     */
    public void solve(
            final SudokuGrid givenNumbers,
            final Consumer<SudokuGrid> sudokuGridConsumer,
            final BooleanSupplier emergencyBrake) {

        final int size = solution.getSize();
        if (givenNumbers.getSize() != size) {
            throw new IllegalArgumentException("Incompatible size ["
                    + givenNumbers.getSize() + "] expected [" + size + "]");
        }

        // Load the given numbers into a list, so they can be passed on
        // to XCC.
        final List<PlaceNumber> preSelectedOptions = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int givenNumber = givenNumbers.getNumber(row, col);
                if (givenNumber > 0) {
                    preSelectedOptions.add(
                            new PlaceNumber(givenNumber, row, col));
                }
            }
        }

        final Consumer<List<PlaceNumber>> solutionMapper =
                new SolutionMapper(solution, sudokuGridConsumer);

        xcc.search(solutionMapper, preSelectedOptions, emergencyBrake);
    }
}
