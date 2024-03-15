package fi.iki.asb.xcc.examples.sudoku.item;

/**
 * A primary item representing a cell being occupied by a number.
 */
public record NumberOccupiesBox(int number, int boxRow, int boxColumn) {
}
