package fi.iki.asb.xcc;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A generic exact cover with color solver. An implementation of Knuth's
 * "Algorithm X" using dancing links. This implementation uses arrays to
 * implement the doubly linked lists.
 *
 * <p>The algorithm is described in
 * <a href="https://www-cs-faculty.stanford.edu/~knuth/fasc5c.ps.gz">
 *     fasc5c.ps.gz
 * </a>.
 */
public final class ReferenceXCC<O> implements XCC<O> {

    // TODO: Convert to using arrays instead of Lists. Getting rid of method
    // calls will likely improve the performance to match LinkedXCC.

    /**
     * A secret color that is used as a marker for items that have been
     * purified. This is technically not needed, but it removes the need to
     * go through the coloured items again once they have already been
     * purified.
     */
    private static final Object PURIFIED = new Object();

    /**
     * Names of items.
     */
    private Object[] NAME;

    /**
     * Column header: index is the index of column <code>i</code>, value is
     * the index of column to the left of <code>i</code>.
     */
    private int[] LLINK;

    /**
     * Column header: index is the index of column <code>i</code>, value is
     * the index of column to the right of <code>i</code>.
     */
    private int[] RLINK;

    /**
     * Index is the index of item <code>x</code> value is the index of
     * <column>c</column> in which the index is.
     */
    private int[] TOP;

    /**
     * The number of options the item in index <code>x</code> is part of.
     */
    private int[] LEN;

    /**
     * Row: index is the index of item <code>x</code>, value is the index of
     * item above <code>x</code>. Except when the item is a spacer between
     * rows, when the value is the index of the item in the beginning of the
     * row.
     */
    private int[] ULINK;

    /**
     * Row: index is the index of item <code>x</code>, value is the index of
     * item below <code>x</code>. Except when the item is a spacer between
     * rows, when the value is the index of the item in the end of the row
     * below. Except when the item is the last item when the value is empty.
     */
    private int[] DLINK;

    /**
     * The color associated to each item. Null corresponds to 0 in the Knuth
     * algorithm, non-null value corresponds to a positive value and PURIFIED
     * corresponds to negative value.
     */
    private Object[] COLOR;

    /**
     * The option associated to each node in the matrix.
     */
    private final List<O> OPTION = new ArrayList<>();

    /**
     * Mapper that creates items for options.
     */
    private final ItemProvider<O> itemProvider;

    /**
     * Number of options. The value is negative. The actual number of options
     * is the absolute value.
     */
    private int numOptions = 0;

    /**
     * Has the data structure been initialized.
     */
    private boolean initialized = false;

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

    /**
     * @param itemProvider
     *      Mapper that creates the items that are covered by each option
     *      that is added to the matrix.
     */
    public ReferenceXCC(
            final ItemProvider<O> itemProvider) {
        this.itemProvider = itemProvider;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    // =================================================================== //
    // Matrix initialization operations.

    @Override
    public void addOption(O option) {
        ensureOpen();
        OPTION.add(option);
    }

    @Override
    public void setTrace(XCCTrace trace) {
        this.trace = trace;
    }

    /**
     * Step C1
     */
    private void initMatrix() {
        ensureOpen();
        initialized = true;

        final List<Object> NAME = new ArrayList<>();
        final List<Integer> LLINK = new ArrayList<>();
        final List<Integer> RLINK = new ArrayList<>();
        final List<Integer> TOP = new ArrayList<>();
        final List<Integer> LEN = TOP;
        final List<Integer> ULINK = new ArrayList<>();
        final List<Integer> DLINK = new ArrayList<>();
        final List<Object> COLOR = new ArrayList<>();

        // Gather all items.
        final List<O> options = new ArrayList<>(OPTION);
        OPTION.clear();

        // Add header elements.
        NAME.add("header");
        LLINK.add(0);
        RLINK.add(0);
        LEN.add(-1);
        COLOR.add(null);
        OPTION.add(null);
        ULINK.add(-1);
        DLINK.add(-1);

        // Gather items from the options and add them.
        final Set<Object> uniqueItems = options.stream()
                .map(itemProvider::from)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        final Set<Object> primaryItems = uniqueItems.stream()
                .filter(this::isPrimary)
                .collect(Collectors.toSet());
        final Set<Object> secondaryItems = uniqueItems.stream()
                .filter(this::isSecondary)
                .collect(Collectors.toSet());

        for (Object item : primaryItems) {
            int i = NAME.size();

            // Initialize column header.
            NAME.add(item);
            LLINK.add(i - 1);
            RLINK.add(0);
            RLINK.set(LLINK.getFirst(), i);
            LLINK.set(0, i);

            LEN.add(0);
            COLOR.add(null);
            OPTION.add(null);
            ULINK.add(i);
            DLINK.add(i);
        }

        boolean firstSecondary = true;
        for (Object item : secondaryItems) {
            int i = NAME.size(); // Current item.
            int p = i - 1;

            // Initialize column header.
            NAME.add(item);
            if (firstSecondary) {
                firstSecondary = false;
                LLINK.add(i);
                RLINK.add(i);
            } else {
                RLINK.add(RLINK.get(p));
                LLINK.add(p);
                LLINK.set(RLINK.get(i), i);
                RLINK.set(LLINK.get(i), i);
            }

            LEN.add(0);
            COLOR.add(null);
            OPTION.add(null);
            ULINK.add(i);
            DLINK.add(i);
        }

        // Add first spacer.
        TOP.add(numOptions--);
        COLOR.add(null);
        OPTION.add(null);
        ULINK.add(-1);
        DLINK.add(-1);

        for (O option : options) {
            final Collection<Object> items = itemProvider.from(option);

            int rowStart = TOP.size() - 1;
            for (Object item : items) {
                // Add item.
                int column = NAME.indexOf(item);
                int i = TOP.size();
                TOP.add(column);

                if (isSecondary(item)) {
                    COLOR.add(((SecondaryItem) item).getColor());
                } else {
                    COLOR.add(null);
                }

                OPTION.add(option);
                ULINK.add(ULINK.get(column));
                DLINK.add(column);

                // Update existing links.
                DLINK.set(ULINK.get(column), i);
                ULINK.set(column, i);
                LEN.set(column, LEN.get(column) + 1);
            }

            // Link row start spacer to the end of row.
            DLINK.set(rowStart, TOP.size() - 1);

            // Row start spacer for next row.
            TOP.add(numOptions--);
            COLOR.add(null);
            OPTION.add(null);
            ULINK.add(rowStart + 1);
            DLINK.add(-1);
        }


        this.NAME = NAME.toArray();
        this.LLINK = toArray(LLINK);
        this.RLINK = toArray(RLINK);
        this.TOP = toArray(TOP);
        this.LEN = this.TOP;
        this.ULINK = toArray(ULINK);
        this.DLINK = toArray(DLINK);
        this.COLOR = COLOR.toArray();
    }

    private int[] toArray(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    // =========================================================== //
    // The XCC solution.

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

        if (isInitialized()) {
            initMatrix();
        }

        // Find the distinct set of items that are covered by the
        // pre-selected options and cover them.
        final List<Object> hiddenItems = collectHiddenItems(
                preSelectedOptions);
        hiddenItems.forEach(i -> cover(columnIndex(i)));

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
        hiddenItems.reversed().forEach(i -> uncover(columnIndex(i)));

        dirty = false;
    }

    /**
     * Step C2
     */
    private void recursiveSearch() {
        if (emergencyBrake.getAsBoolean()) {
            return;
        }

        // If there are no uncovered columns, the matrix is empty
        // and the list contains a solution (step C8).
        if (RLINK[0] == 0) {
            solutionConsumer.accept(Collections.unmodifiableList(
                    solution));
            return;
        }

        // Select and cover column (steps C3 and C4).
        int i = findColumn();

        if (trace != null) {
            trace.onRecursionEntered(LEN[i]);
        }

        cover(i);
        int x1 = DLINK[i];

        // Step C5
        while (x1 != i) {
            int p = x1 + 1;

            if (trace != null) {
                trace.onItemSelected();
            }

            while (p != x1) {
                int j = TOP[p];
                if (j <= 0) {
                    p = ULINK[p];
                } else {
                    commit(p, j);
                    p = p + 1;
                }
            }

            // Return to C2
            solution.add(OPTION.get(x1));
            recursiveSearch();
            solution.removeLast();

            // Step C6
            p = x1 - 1;
            while (p != x1) {
                int j = TOP[p];
                if (j <= 0) {
                    p = DLINK[p];
                } else {
                    uncommit(p, j);
                    p = p - 1;
                }
            }

            // Return to C5
            i = TOP[x1];
            x1 = DLINK[x1];
        }

        // C7 or return to C6 if we are in recursion.
        uncover(i);

        if (trace != null) {
            trace.onRecursionEnded();
        }
    }

    /**
     * Find column using Minimum-Remaining-Value (MRV) heuristic. It chooses
     * the column with the fewest remaining values. Also called most
     * constrained variable or fail-first heuristics, because it helps in
     * discovering inconsistencies earlier.
     */
    private int findColumn() {
        int candidate = RLINK[0];
        int smallest = candidate;
        while (candidate != 0) {
            if (LEN[candidate] < LEN[smallest]) {
                smallest = candidate;
            }

            candidate = RLINK[candidate];
        }

        return smallest;
    }

    // =================================================================== //
    // Operations for manipulating the matrix during search.

    private void commit(int p, int j) {
        Object c = COLOR[p];
        if (c == null) {
            cover(j);
        } else if (c != PURIFIED) {
            purify(p);
        }
    }

    private void purify(int p) {
        Object c = COLOR[p];
        int i = TOP[p];
        int q = DLINK[i];

        // Save color.
        COLOR[i] = c;

        while (q != i) {
            if (Objects.equals(COLOR[q], c)) {
                COLOR[q] = PURIFIED;
            } else {
                hide(q);
            }

            q = DLINK[q];
        }
    }

    private void cover(int i) {
        int p = DLINK[i];
        while (p != i) {
            hide(p);
            p = DLINK[p];
        }

        final int l = LLINK[i];
        final int r = RLINK[i];
        RLINK[l] = r;
        LLINK[r] = l;
    }

    private void hide(int p) {
        int q = p + 1;
        while (q != p) {
            int x = TOP[q];
            int u = ULINK[q];

            if (x <= 0) {
                q = u;
            } else {
                if (COLOR[q] != PURIFIED) {
                    int d = DLINK[q];
                    DLINK[u] = d;
                    ULINK[d] = u;
                    LEN[x]--;
                }
                q++;
            }

        }
    }

    private void uncommit(int p, int j) {
        Object c = COLOR[p];
        if (c == null) {
            uncover(j);
        } else if (c != PURIFIED) {
            unpurify(p);
        }
    }

    private void unpurify(int p) {
        int i = TOP[p];
        int q = ULINK[i];
        Object c = COLOR[i];

        while (q != i) {
            if (COLOR[q] == PURIFIED) {
                COLOR[q] = c;
            } else {
                unhide(q);
            }

            q = ULINK[q];
        }

        COLOR[i] = null;
    }

    private void uncover(int i) {
        final int l = LLINK[i];
        final int r = RLINK[i];
        RLINK[l] = i;
        LLINK[r] = i;

        int p = ULINK[i];
        while (p != i) {
            unhide(p);
            p = ULINK[p];
        }

    }

    private void unhide(int p) {
        int q = p - 1;
        while (q != p) {
            int x = TOP[q];
            int d = DLINK[q];

            if (x <= 0) {
                q = d;
            } else {
                if (COLOR[q] != PURIFIED) {
                    int u = ULINK[q];
                    DLINK[u] = q;
                    ULINK[d] = q;
                    LEN[x]++;
                }
                q--;
            }
        }
    }

    // =========================================================== //
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

    private int columnIndex(Object item) {
        for (int i = 1; i < NAME.length; i++) {
            if (Objects.equals(item, NAME[i])) {
                return i;
            }
        }

        throw new IllegalArgumentException(item.toString());
    }

    private boolean isInitialized() {
        return !initialized;
    }

    private boolean isPrimary(Object item) {
        return !(item instanceof SecondaryItem);
    }

    private boolean isSecondary(Object item) {
        return (item instanceof SecondaryItem);
    }

    private void ensureClean() {
        if (dirty) {
            throw new IllegalStateException("matrix is dirty");
        }
    }

    private void ensureOpen() {
        if (initialized) {
            throw new IllegalStateException("matrix is locked");
        }
    }
}
