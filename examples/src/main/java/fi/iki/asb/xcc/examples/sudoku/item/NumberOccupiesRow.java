package fi.iki.asb.xcc.examples.sudoku.item;

/**
 * A primary item representing a row being occupied by a specific number.
 */
public record NumberOccupiesRow(int number, int row) {
}
