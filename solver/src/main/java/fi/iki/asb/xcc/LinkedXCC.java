package fi.iki.asb.xcc;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A generic exact cover with color solver. An implementation of Knuth's
 * "Algorithm X" using dancing links. This implementation uses pointers
 * to implement the doubly linked lists.
 *
 * <p>This class is <i>not thread safe</i>.</p>
 *
 * @param <O>
 *     The type associated to options. For example, in a sudoku solver this
 *     might be a class that represents "placing a number in a given row,
 *     column location".
 */
public final class LinkedXCC<O> implements XCC<O> {

    /**
     * A secret color that is used as a marker for items that have been
     * purified. This is technically not needed, but it removes the need
     * to go through the coloured items again once they have already been
     * purified.
     */
    private static final Object PURIFIED = new Object();

    /**
     * A constraint node in the matrix.
     *
     * @param <OO>
     *     The type associated to options.
     */
    private static class Node<OO> {
        final Column<OO> column;
        final OO option;
        Node<OO> up, down, left, right;
        Object color = null;

        Node(Column<OO> column, OO option) {
            this.column = column;
            this.option = option;
            up = down = left = right = this;
        }
    }

    /**
     * Column header in the matrix.
     *
     * @param <OO>
     *     The type associated to options. The type is not used directly in
     *     the column. It provides type safety for when the nodes in the
     *     column are accessed.
     */
    private static class Column<OO> extends Node<OO> {
        final Object item;

        /**
         * Number of nodes in this column. This information is technically
         * redundant, but it helps optimize the algorithm as the column
         * with the least amount of nodes can now be chosen in O(N) time.
         */
        int size = 0;

        Column(Object item) {
            super(null, null);
            this.item = item;
        }

        @Override
        public String toString() {
            return "Column{" +
                    "name=\"" + item + "\"" +
                    ", size=" + size +
                    '}';
        }
    }

    // =================================================================== //

    /**
     * Mapper that creates items for options.
     */
    private final ItemProvider<O> itemProvider;

    /**
     * A permanent token that is on the left side of the first primary
     * item column. If the matrix is empty, <code>primaryHead.right</code>
     * points back to itself.
     */
    private final Column<O> primaryHead = new Column<>(
            "primaryHead");

    /**
     * A permanent token that is on the left side of the first secondary
     * item column. This is not used during search, it exists to simplify
     * matrix initialization.
     */
    private final Column<O> secondaryHead = new Column<>(
            "secondaryHead");

    /**
     * Columns mapped by their items to simplify matrix initialization.
     * This map is not used when the actual algorithm is running. Items
     * are created by the <code>ObjectItemMapper</code>.
     */
    private final Map<Object, Column<O>> itemColumns
            = new HashMap<>();

    /**
     * Is the matrix dirty? This is set to <code>true</code> when the
     * algorithm starts and restored back to <code>false</code> when it
     * finishes successfully. If the execution is interrupted by an
     * exception, the flag is left "dirty" and subsequent executions
     * are prevented.
     */
    private boolean dirty = false;

    /**
     * Current solution. Cleared when algorithm finishes. Although the
     * solution is only needed during the execution of the search, it
     * is stored as an instance field to reduce the number of parameters
     * that need to be stored in stack during the recursive calls.
     */
    private LinkedList<O> solution;

    /**
     * Current solution consumer. Cleared when algorithm finishes. As with
     * <code>solution</code> this is stored as an instance field to reduce
     * the number of parameters in the recursive calls.
     */
    private Consumer<List<O>> solutionConsumer;

    /**
     * Current emergency brake. Cleared when algorithm finishes. As with
     * <code>solution</code> this is stored as an instance field to reduce
     * the number of parameters in the recursive calls.
     */
    private BooleanSupplier emergencyBrake;

    private XCCTrace trace = null;

    // =================================================================== //

    /**
     * Create a new instance.
     *
     * @param itemProvider
     *      Mapper that creates the items that are covered by each option that
     *      is added to the matrix.
     */
    public LinkedXCC(final ItemProvider<O> itemProvider) {
        this.itemProvider = itemProvider;
    }

    @Override
    public void setTrace(XCCTrace trace) {
        this.trace = trace;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    // =================================================================== //
    // Matrix initialization operations.

    /**
     * Add an option. The items covered by the option are generated using
     * the <code>OptionItemMapper</code>.
     *
     * @throws IllegalStateException
     * 		A previous <code>search</code> was interrupted by an exception
     *      and the matrix was left dirty. The XCC instance must be
     *      recreated.
     */
    @Override
    public void addOption(final O option) {
        ensureClean();

        // Needed for linking nodes horizontally.
        Node<O> previousNode = null;

        // Get the items covered by this option from the mapper and add
        // each one to the matrix.
        for (Object item: itemProvider.from(option)) {
            final Column<O> column = getOrCreateColumn(item);
            final Node<O> newNode = new Node<>(column, option);

            if (isSecondary(item)) {
                newNode.color = ((SecondaryItem) item).getColor();
            }

            // Add new node to the column.
            newNode.down = column;
            newNode.up = column.up;
            column.up.down = newNode;
            column.up = newNode;
            column.size++;

            // Add new node to the row.
            if (previousNode != null) {
                newNode.right = previousNode.right;
                newNode.left = previousNode;
                previousNode.right.left = newNode;
                previousNode.right = newNode;
            }
            previousNode = newNode;
        }
    }

    /**
     * Get the column for a new item or create it if one does not exist yet.
     */
    private Column<O> getOrCreateColumn(final Object item) {
        Column<O> column = itemColumns.get(item);
        if (column == null) {
            column = new Column<>(item);

            // Secondary items are not mapped to the header row. Thus, they
            // get ignored when the search method checks if the matrix is
            // empty.
            final Column<O> head = isPrimary(item)
                    ? primaryHead
                    : secondaryHead;
            column.left = head.left;
            column.right = head;
            head.left.right = column;
            head.left = column;

            itemColumns.put(item, column);
        }
        return column;
    }

    // =================================================================== //
    // The XCC solution.

    /**
     * Search for exact cover solution with pre-selected options
     * and an emergency brake.
     *
     * @param solutionConsumer
     *        The consumer which collects the results. Cannot be null.
     *
     * @param preSelectedOptions
     *      The options that are pre-selected to be part of the solution
     *      (for example the initial numbers given in a sudoku puzzle).
     *      This object will be reused as the container for solutions
     *      found by the algorithm. Cannot be null. Can be empty.
     *
     * @param emergencyBrake
     *      A boolean supplier which is periodically checked to prevent
     *      runaway execution. When this supplier returns true, the
     *      execution is stopped as soon as possible. This can be used, for
     *      example, in conjunction with the solutionConsumer to stop
     *      execution as soon as a solution is found or to check if runtime
     *      limit has been exceeded. Cannot be null.
     *
     * @throws IllegalStateException
     *      A previous <code>search</code> was interrupted by an exception
     *      and the matrix was left dirty. The XCC instance must be
     *      recreated.
     */
    @Override
    public void search(
            final Consumer<List<O>> solutionConsumer,
            final List<O> preSelectedOptions,
            final BooleanSupplier emergencyBrake) {
        Objects.requireNonNull(preSelectedOptions);
        Objects.requireNonNull(solutionConsumer);
        Objects.requireNonNull(emergencyBrake);

        ensureClean();
        dirty = true;

        // Find the distinct set of items that are covered by the
        // pre-selected options and cover them.
        final List<Object> hiddenItems = collectHiddenItems(
                preSelectedOptions);
        hiddenItems.forEach(i -> coverItem(itemColumns.get(i)));

        try {
            this.solution = new LinkedList<>(preSelectedOptions);
            this.solutionConsumer = solutionConsumer;
            this.emergencyBrake = emergencyBrake;

            if (trace != null) {
                trace.onSearchStarted();
            }

            recursiveSearch();
        } finally {
            this.solution = null;
            this.solutionConsumer = null;
            this.emergencyBrake = null;
        }

        // Uncover the initial hidden columns in reverse order to restore
        // the matrix to original state.
        hiddenItems.reversed().forEach(i -> uncoverItem(itemColumns.get(i)));

        dirty = false;
    }

    /**
     * Entry point for the recursive search (step C2).
     */
    private void recursiveSearch() {
        if (emergencyBrake.getAsBoolean()) {
            return;
        }

        // If there are no uncovered columns, the matrix is empty
        // and the list contains a solution (step C8).
        if (primaryHead.right == primaryHead) {
            solutionConsumer.accept(Collections.unmodifiableList(
                     solution));
            return;
        }

        // Select and cover column (step C3 and C4).
        final Column<O> column = findColumn();

        if (trace != null) {
            trace.onRecursionEntered(column.size);
        }

        coverItem(column);

        // If the column returned above by findColumn() has no nodes, it
        // means that this branch is unsolvable and must be backtracked.
        // Since in that case there are no nodes in the column, the rest
        // of this method only performs the cover and uncover operations
        // and the forEach executes zero times.

        // Go through each one of the options that are associated to the
        // covered item, add the option to the solution and recursively go
        // through the rest of the options that are still available.

        for (Node<O> n = column.down; n != column; n = n.down) {
            if (trace != null) {
                trace.onItemSelected();
            }

            // This loop should be a sub-method but that would mean that
            // there were two method calls in the recursion, which consumes
            // an unnecessary amount of stack space.

            // For each row that has a constraint in this column, add the
            // row value to the result, cover all columns that are in
            // conflict with this row constraint and recurse (step C5).
            for (Node<O> n1 = n.right; n1 != n; n1 = n1.right) {
                commitItem(n1);
            }

            solution.add(n.option);
            recursiveSearch();
            solution.removeLast();

            // Rollback changes made before recursion.
            for (Node<O> n1 = n.left; n1 != n; n1 = n1.left) {
                uncommit(n1);
            }
        }

        // Step C7 (or return to C6 if we are in recursion).
        uncoverItem(column);

        if (trace != null) {
            trace.onRecursionEnded();
        }
    }

    /**
     * Find column using "minimum remaining value" (MRV) heuristic (also
     * called "most constrained variable" or "fail-first" heuristics,
     * because it helps in discovering inconsistencies earlier). The
     * column with the fewest remaining values is chosen.
     */
    private Column<O> findColumn() {
        // This method is never called if primaryHead.right points to
        // primaryHead.
        Column<O> fewest = (Column<O>) primaryHead.right;
        Column<O> candidate = (Column<O>) fewest.right;
        while (candidate != primaryHead) {
            if (candidate.size < fewest.size) {
                fewest = candidate;
            }

            candidate = (Column<O>) candidate.right;
        }

        return fewest;
    }

    // =================================================================== //
    // Operations for manipulating the matrix during search.

    /**
     * Commit to an item. If the item is a primary item or an uncolored
     * secondary item, it gets covered. Otherwise, if the item is a
     * non-purified secondary item, the item gets purified.
     */
    private void commitItem(final Node<O> node) {
        if (node.color == null) {
            coverItem(node.column);
        } else if (node.color != PURIFIED) {
            purifyItem(node);
        }
    }

    /**
     * Purify the item by hiding all options that are associated to this
     * item with mismatching color. As the result of purification, the
     * "unpure" options that have wrong color, are purged from the matrix.
     */
    private void purifyItem(final Node<O> node) {
        final Column<O> column = node.column;

        // Save item color for unpurification.
        column.color = node.color;

        for (Node<O> n = column.down; n != column; n = n.down) {
            if (n.color == column.color) {
                n.color = PURIFIED;
            } else {
                hideOption(n);
            }
        }
    }

    /**
     * Hide the column from the matrix. As described in the
     * <a href="https://en.wikipedia.org/wiki/Dancing_Links">dancing links
     * Wikipedia article</a>, the column is hidden by taking the link coming
     * in from the column header on the left, "pulling" it over this column
     * and pointing it to the column header on the right (and vice versa to
     * the other incoming link). The column is thus "covered" by the links
     * pulled over it.
     *
     * <p>The links going out from this column header can be left intact
     * because after the incoming links have been moved, the <code>
     * findColumn()</code> method can no longer find this column.</p>
     *
     * <p>The nodes in the rows connected to this column are also covered
     * from the matrix because placing them in the solution would cause
     * conflicts in the constraints in this column.</p>
     *
     * <p>The column does not get lost at any point. The knowledge that it
     * exists is stored in the recursion stack and will be restored from
     * there when the algorithm backtracks.</p>
     */
    private void coverItem(final Column<O> column) {
        for (Node<O> n = column.down; n != column; n = n.down) {
            hideOption(n);
        }

        column.left.right = column.right;
        column.right.left = column.left;
    }

    /**
     * Hide the option associated to the specific node. The method goes
     * through all other nodes on the same row and unlinks them from the
     * columns. After this method has finished, the option associated to
     * the item can no longer be added to the solution as the search dives
     * deeper into the recursion (because the items still in the matrix no
     * longer link to it).
     */
    private void hideOption(final Node<O> node) {
        for (Node<O> n = node.right; n != node; n = n.right) {
            if (n.color != PURIFIED) {
                n.down.up = n.up;
                n.up.down = n.down;
                n.column.size--;
            }
        }
    }

    /**
     * Reverse of {@see uncommit}.
     */
    private void uncommit(final Node<O> node) {
        if (node.color == null) {
            uncoverItem(node.column);
        } else if (node.color != PURIFIED) {
            unpurifyItem(node);
        }
    }

    /**
     * Reverse of {@see purifyItem}.
     */
    private void unpurifyItem(final Node<O> node) {
        final Column<O> column = node.column;

        for (Node<O> n = column.down; n != column; n = n.down) {
            if (n.color == PURIFIED) {
                n.color = column.color;
            } else {
                unhideOption(n);
            }
        }

        column.color = null;
    }

    /**
     * Reverse of {@see uncoverItem}. Because during the covering operation
     * the links going out from this column (and the rows connected to it)
     * were not changed, we still know the columns that were originally on
     * the left and right side and the links coming in from them can be
     * restored.
     */
    private void uncoverItem(final Column<O> column) {
        for (Node<O> n = column.up; n != column; n = n.up) {
            unhideOption(n);
        }

        column.right.left = column;
        column.left.right = column;
    }

    /**
     * Reverse of {@see hideOption}
     */
    private void unhideOption(final Node<O> node) {
        for (Node<O> n = node.left; n != node; n = n.left) {
            if (n.color != PURIFIED) {
                n.column.size++;
                n.down.up = n;
                n.up.down = n;
            }
        }
    }

    // =================================================================== //
    // Auxiliary methods.

    /**
     * Collect hidden items from the pre-selected options.
     */
    private List<Object> collectHiddenItems(
            final Collection<O> preSelectedOptions) {

        // Gather distinct items that should be hidden.
        return preSelectedOptions.stream()
                .map(itemProvider::from)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    /**
     * Is the item a primary item.
     */
    private boolean isPrimary(Object item) {
        return ! (item instanceof SecondaryItem);
    }

    /**
     * Is the item a secondary item.
     */
    private boolean isSecondary(Object item) {
        return (item instanceof SecondaryItem);
    }

    /**
     * Called when the matrix is expected to be clean.
     *
     * @throws IllegalStateException If the matrix is not clean.
     */
    private void ensureClean() {
        if (dirty) {
            throw new IllegalStateException("matrix is dirty");
        }
    }
}
