package fi.iki.asb.xcc;

import java.util.Collection;

/**
 * Interface providing the items covered by an option.
 */
@FunctionalInterface
public interface ItemProvider<O> {

	/**
	 * Get items covered by the option. Objects that are instances of {@link
	 * SecondaryItem} are treated as secondary items. The returned objects
	 * are compared using <code>equals</code> and <code>hashCode</code>
	 * methods.
	 *
	 * <p>Objects in the returned array must be unique (i.e. no two objects
	 * in the collection can return true when compared with
	 * <code>Objects.equals(...)</code>.</p>
	 *
	 * @return Unique set of items the option covers.
	 */
	Collection<Object> from(O option);

}
