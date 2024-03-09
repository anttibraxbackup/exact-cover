package fi.iki.asb.xcc.sudoku;

import fi.iki.asb.xcc.OptionItemMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SudokuOptionMapper implements OptionItemMapper<SudokuCell> {

	private final int size;

	/**
	 * Square root of size. E.g. the width/height of a box.
	 */
	private final int sizeSqrt;

	/**
	 * Square of size. Number of cells in the grid.
	 */
	private final int sizeSq;

	public SudokuOptionMapper(int size) {
		Sudoku.validateSize(size);
		this.size = size;
		this.sizeSqrt = (int) Math.sqrt(size);
		this.sizeSq = size * size;
	}

	public int getSize() {
		return size;
	}

	@Override
	public Collection<Object> from(SudokuCell cell) {
		final int numOffset = cell.number() - 1;
		final int row = cell.row() - 1;
		final int col = cell.column() - 1;

		final List<Object> columns = new ArrayList<>(4);

		// Cell itself.
		int i = row * size + col;
		columns.add(i);

		// Row constraint.
		i = sizeSq + (row * size) + numOffset;
		columns.add(i);

		// Column constraint.
		i = (2 * sizeSq) + (col * size) + numOffset;
		columns.add(i);

		// Box constraint.
		int boxRow = row / sizeSqrt;
		int boxCol = col / sizeSqrt;

		i = (3 * sizeSq)
				+ (boxRow * sizeSqrt + boxCol) * size + numOffset;
		columns.add(i);

		return columns;
	}
}
