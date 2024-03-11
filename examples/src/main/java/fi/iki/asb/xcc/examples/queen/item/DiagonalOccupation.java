package fi.iki.asb.xcc.examples.queen.item;

import fi.iki.asb.xcc.SecondaryItem;

/**
 * Primary item representing a diagonal (/) being occupied by a queen.
 */
public record DiagonalOccupation(int diagonal)
implements SecondaryItem {
}
