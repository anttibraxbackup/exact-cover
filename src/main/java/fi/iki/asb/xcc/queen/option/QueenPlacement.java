package fi.iki.asb.xcc.queen.option;

import fi.iki.asb.xcc.queen.QueenGrid;

/**
 * Option for placing a queen on a locatin in the chess board.
 */
public record QueenPlacement(
		int row,
		int column) {

	@Override
	public String toString() {
		return "(" + row + ',' + column + ')';
	}

	public void placeOn(QueenGrid grid) {
		grid.setQueen(row, column);
	}
}
