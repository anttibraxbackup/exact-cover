package fi.iki.asb.xcc.examples.sudoku.option;

/**
 * Option for placing a number in a cell.
 *
 * @param number Number in cell, starts from 1.
 * @param row Row number, starts from 0.
 * @param column Column number, starts from 0.
 */
public record PlaceNumber(
        int number,
        int row,
        int column
) {
}
