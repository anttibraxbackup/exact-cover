package fi.iki.asb.xcc.examples.sudoku.item;

/**
 * A primary item representing a column being occupied by a specific number.
 */
public record NumberOccupiesColumn(int number, int column) {
}
