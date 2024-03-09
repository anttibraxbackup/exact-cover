package fi.iki.asb.xcc.queen;

import fi.iki.asb.xcc.OptionItemMapper;
import fi.iki.asb.xcc.SecondaryItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMapper implements OptionItemMapper<QueenPlacement> {

	private record RowPlacement(int row) { }

	private record ColumnPlacement(int column) { }

	private record DiagonalPlacement(int diagonal)
			implements SecondaryItem { }

	private record ReverseDiagonalPlacement(int reverseDiagonal)
			implements SecondaryItem { }

	private final int size;

	public QueenMapper(int size) {
		this.size = size;
	}

	@Override
	public Collection<Object> from(QueenPlacement queen) {
		final int row = queen.row();
		final int col = queen.column();
		final List<Object> items = new ArrayList<>(4);
		items.add(new ColumnPlacement(col));
		items.add(new RowPlacement(row));
		items.add(new DiagonalPlacement(col + row));
		items.add(new ReverseDiagonalPlacement(size - 1 - col + row));
		return items;
	}
}
