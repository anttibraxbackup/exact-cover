package fi.iki.asb.xcc.examples.queen.option;

import fi.iki.asb.xcc.examples.queen.QueenGrid;

/**
 * Option for placing a queen on a location in the chess board.
 */
public record QueenPlacement(
		int row,
		int column) {

	@Override
	public String toString() {
		return STR."(\{row}\{','}\{column}\{')'}";
	}

	public void placeOn(QueenGrid grid) {
		grid.setQueen(row, column);
	}
}
