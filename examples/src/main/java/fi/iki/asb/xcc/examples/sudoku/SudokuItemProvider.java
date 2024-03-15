package fi.iki.asb.xcc.examples.sudoku;

import fi.iki.asb.xcc.ItemProvider;
import fi.iki.asb.xcc.examples.sudoku.item.CellOccupied;
import fi.iki.asb.xcc.examples.sudoku.item.NumberOccupiesBox;
import fi.iki.asb.xcc.examples.sudoku.item.NumberOccupiesColumn;
import fi.iki.asb.xcc.examples.sudoku.item.NumberOccupiesRow;
import fi.iki.asb.xcc.examples.sudoku.option.PlaceNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SudokuItemProvider implements ItemProvider<PlaceNumber> {

    /**
     * Square root of size. E.g. the width/height of a box.
     */
    private final int sizeSqrt;

    SudokuItemProvider(final int size) {
        this.sizeSqrt = (int) Math.sqrt(size);
    }

    @Override
    public Collection<Object> from(PlaceNumber cell) {
        final int row = cell.row();
        final int col = cell.column();

        final List<Object> columns = new ArrayList<>(4);

        // Cell itself.
        columns.add(new CellOccupied(row, col));

        // Row constraint.
        columns.add(new NumberOccupiesRow(cell.number(), row));

        // Column constraint.
        columns.add(new NumberOccupiesColumn(cell.number(), col));

        // Box constraint.
        final int boxRow = row / sizeSqrt;
        final int boxCol = col / sizeSqrt;
        columns.add(new NumberOccupiesBox(cell.number(), boxRow, boxCol));

        return columns;
    }
}
