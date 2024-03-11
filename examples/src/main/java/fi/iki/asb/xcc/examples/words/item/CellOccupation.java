package fi.iki.asb.xcc.examples.words.item;

import fi.iki.asb.xcc.SecondaryItem;

import java.util.Objects;

/**
 * Secondary item representing a letter occupying a cell.
 * Letter represents the item color.
 */
public record CellOccupation(int row, int col, Character letter) implements SecondaryItem {

    @Override
    public Object getColor() {
        return letter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellOccupation that = (CellOccupation) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
