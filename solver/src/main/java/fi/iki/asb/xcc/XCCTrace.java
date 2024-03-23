package fi.iki.asb.xcc;

/**
 * Interface for tracing the XCC search. This can be used, for example,
 * to implement progress reporting.
 */
public interface XCCTrace {

    /**
     * Called when search starts.
     */
    void onSearchStarted();

    /**
     * Called when recursion is entered.
     *
     * @param itemCount Number of items on this recursion level.
     */
    void onRecursionEntered(int itemCount);

    /**
     * Called when item is selected.
     */
    void onItemSelected();

    /**
     * Called when one level of recursion ends.
     */
    void onRecursionEnded();

}
