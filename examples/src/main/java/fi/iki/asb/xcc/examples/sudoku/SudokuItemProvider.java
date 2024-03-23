package fi.iki.asb.xcc.examples.sudoku;

import fi.iki.asb.xcc.ItemProvider;
import fi.iki.asb.xcc.examples.sudoku.item.CellOccupied;
import fi.iki.asb.xcc.examples.sudoku.item.NumberOccupiesBox;
import fi.iki.asb.xcc.examples.sudoku.item.NumberOccupiesColumn;
import fi.iki.asb.xcc.examples.sudoku.item.NumberOccupiesRow;
import fi.iki.asb.xcc.examples.sudoku.option.PlaceNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SudokuItemProvider implements ItemProvider<PlaceNumber> {

    /**
     * Square root of size. E.g. the width/height of a box.
     */
    private final int sizeSqrt;

    SudokuItemProvider(final int size) {
        this.sizeSqrt = (int) Math.sqrt(size);
    }

    /**
     * Cache to reduce number of duplicate items in memory.
     */
    private final Map<Object, Object> itemCache = new HashMap<>();

    @Override
    public Collection<Object> from(PlaceNumber cell) {
        final int row = cell.row();
        final int col = cell.column();

        final List<Object> columns = new ArrayList<>(4);

        // Cell itself.
        columns.add(cache(new CellOccupied(row, col)));

        // Row constraint.
        columns.add(cache(new NumberOccupiesRow(cell.number(), row)));

        // Column constraint.
        columns.add(cache(new NumberOccupiesColumn(cell.number(), col)));

        // Box constraint.
        final int boxRow = row / sizeSqrt;
        final int boxCol = col / sizeSqrt;
        columns.add(cache(new NumberOccupiesBox(cell.number(), boxRow, boxCol)));

        return columns;
    }

    private Object cache(Object key) {
        Object cached = itemCache.get(key);
        if (cached == null) {
            itemCache.put(key, key);
            cached = key;
        }

        return cached;
    }
}
