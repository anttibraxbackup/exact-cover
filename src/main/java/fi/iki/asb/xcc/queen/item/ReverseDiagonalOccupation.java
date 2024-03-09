package fi.iki.asb.xcc.queen.item;

import fi.iki.asb.xcc.SecondaryItem;

/**
 * Primary item representing a reverse diagonal (\) being occupied by a
 * queen.
 */
public record ReverseDiagonalOccupation(int diagonal)
implements SecondaryItem {
}
