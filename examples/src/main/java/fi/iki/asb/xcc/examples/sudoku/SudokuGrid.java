package fi.iki.asb.xcc.examples.sudoku;

import java.util.Arrays;

public class SudokuGrid {

    /**
     * Board size.
     */
    private final int size;

    /**
     * Numbers given in the puzzle.
     */
    private final int[] givens;

    /**
     * Numbers on the board, including given numbers.
     */
    private final int[] numbers;

    public SudokuGrid(SudokuGrid original) {
        this(original.getSize());
        System.arraycopy(original.numbers, 0, numbers, 0, numbers.length);
        System.arraycopy(original.givens, 0, givens, 0, givens.length);
    }

    public SudokuGrid(int size) {
        validateSize(size);
        this.size = size;

        numbers = new int[size * size];
        givens = new int[size * size];

        Arrays.fill(numbers, -1);
        Arrays.fill(givens, -1);
    }

    public static SudokuGrid parse(String str) {
        str = str.replace("\n", "")
                .replace("\r", "");
        final int size = (int) Math.sqrt(str.length());
        if (size * size != str.length()) {
            throw new IllegalArgumentException("Invalid input");
        }

        final SudokuGrid grid = new SudokuGrid(size);
        int i = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                char ch = str.charAt(i++);
                switch (ch) {
                    case '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                        grid.setGiven(Character.getNumericValue(ch),
                                row, column);
                    default -> {
                        // Treat non-numeric characters as empty.
                    }
                }
            }
        }
        return grid;
    }

    public void reset() {
        System.arraycopy(givens, 0, numbers, 0, givens.length);
    }

    public void setGiven(int number, int row, int column) {
        setGuess(number, row, column);
        givens[indexOf(row, column)] = number;
    }

    public void setGuess(int number, int row, int column) {
        validateNumber(number);
        validateLocation(row, column);

        if (isGiven(row, column)) {
            throw new IllegalArgumentException("Attempt to overwrite given "
                    + "number at [" + row + "," + column
                    + "] with [" + number + "]");
        }

        numbers[indexOf(row, column)] = number;
    }

    public int getSize() {
        return size;
    }

    public int getNumber(int row, int column) {
        validateLocation(row, column);
        return numbers[indexOf(row, column)];
    }

    public boolean isGiven(int row, int column) {
        validateLocation(row, column);
        return givens[indexOf(row, column)] > 0;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                final int n = numbers[indexOf(row, column)];
                sb.append(n > 0
                        ? String.format("%02d, ", n)
                        : "  , ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private void validateLocation(int row, int column) {
        if (row < 0 || row >= size || column < 0 || column >= size) {
            throw new IllegalArgumentException("Illegal location ["
                    + row + "," + column + "]");
        }
    }

    private void validateNumber(int number) {
        if (number < 1 || number > size) {
            throw new IllegalArgumentException("Illegal number ["
                    + number + "]");
        }
    }

    private int indexOf(int row, int column) {
        return row * size + column;
    }

    private static void validateSize(int size) {
        final int sqrt = (int) Math.sqrt(size);
        if (sqrt * sqrt != size || size < 1) {
            throw new IllegalArgumentException("Illegal size [" + size + "]");
        }
    }
}
