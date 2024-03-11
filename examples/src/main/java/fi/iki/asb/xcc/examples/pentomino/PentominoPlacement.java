package fi.iki.asb.xcc.examples.pentomino;

/**
 * Placement of a specific pentomino in a specific location.
 */
public record PentominoPlacement(
		Pentomino pentomino,
		int row,
		int column
) {

	public void placeOn(PentominoGrid grid) {
		for (int r = 0; r < pentomino.height(); r++) {
			for (int c = 0; c < pentomino.width(); c++) {
				if (pentomino.hasSquare(r, c)) {
					grid.setChar(row + r, column + c,
							pentomino.identifier());
				}
			}
		}
	}

	@Override
	public String toString() {
		return "(" + pentomino +
				",r=" + row +
				",c=" + column +
				')';
	}
}
