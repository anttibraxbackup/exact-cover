package fi.iki.asb.xcc;

/**
 * An interface for items that are secondary items. Primary items must be
 * covered exactly once for a solution to be valid. Since primary items do
 * not have any special features, all items that do not implement this
 * interface are considered primary items.
 *
 * <p>Secondary items that do not have a color can be covered at most once.
 * Secondary items that have color can be covered unlimited times as long as
 * all the instances of the same secondary item in the solution have the same
 * color.</p>
 */
public interface SecondaryItem {

    /**
     * Get the color of this secondary item. The default implementation
     * returns <code>null</code>, which represents an uncolored item.
     */
    default Object getColor() {
        return null;
    }

}
