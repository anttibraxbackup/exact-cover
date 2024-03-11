package fi.iki.asb.xcc.examples.sudoku;

/**
 * @param number Number in cell, starts from 1.
 * @param row Row number, starts from 1.
 * @param column Column number, starts from 1.
 */
public record SudokuCell(
		int number,
		int row,
		int column
) {
}
