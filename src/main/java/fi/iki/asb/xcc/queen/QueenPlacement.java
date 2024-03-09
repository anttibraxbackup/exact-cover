package fi.iki.asb.xcc.queen;

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
