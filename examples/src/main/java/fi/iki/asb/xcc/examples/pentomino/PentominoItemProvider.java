package fi.iki.asb.xcc.examples.pentomino;

import fi.iki.asb.xcc.ItemProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Map Pentomino placement constraints.
 */
public class PentominoItemProvider
implements ItemProvider<PentominoPlacement> {

	/**
	 * Item that marks a specific shape as used so that each shape
	 * gets used only once.
	 */
	private record PentominoUsed(char identifier) { };

	/**
	 * Item that marks a specific cell on the board as used.
	 */
	private record CellUsed(int row, int column) { };

	@Override
	public Collection<Object> from(PentominoPlacement placement) {
		final Pentomino p = placement.pentomino();
		final List<Object> options = new ArrayList<>(6);

		// Item that consumes the pentomino shape so that it cannot be
		// reused.
		options.add(new PentominoUsed(p.identifier()));

		for (int r = 0; r < p.height(); r++) {
			for (int c = 0; c < p.width(); c++) {
				if (p.hasSquare(r, c)) {
					final int row = placement.row() + r;
					final int column = placement.column() + c;
					options.add(new CellUsed(row, column));
				}
			}
		}

		return options;
	}
}
