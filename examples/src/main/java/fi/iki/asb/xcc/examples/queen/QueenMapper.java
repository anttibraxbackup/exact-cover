package fi.iki.asb.xcc.examples.queen;

import fi.iki.asb.xcc.OptionItemMapper;
import fi.iki.asb.xcc.examples.queen.item.ColumnOccupation;
import fi.iki.asb.xcc.examples.queen.item.DiagonalOccupation;
import fi.iki.asb.xcc.examples.queen.item.ReverseDiagonalOccupation;
import fi.iki.asb.xcc.examples.queen.item.RowOccupation;
import fi.iki.asb.xcc.examples.queen.option.QueenPlacement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMapper implements OptionItemMapper<QueenPlacement> {

	private final int size;

	public QueenMapper(int size) {
		this.size = size;
	}

	@Override
	public Collection<Object> from(QueenPlacement queen) {
		final int row = queen.row();
		final int col = queen.column();
		final List<Object> items = new ArrayList<>(4);
		items.add(new ColumnOccupation(col));
		items.add(new RowOccupation(row));
		items.add(new DiagonalOccupation(col + row));
		items.add(new ReverseDiagonalOccupation(size - 1 - col + row));
		return items;
	}
}
