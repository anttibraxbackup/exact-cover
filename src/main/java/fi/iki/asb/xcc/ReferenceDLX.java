package fi.iki.asb.xcc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
public final class ReferenceDLX<O> implements DLX<O> {

    // TODO: Convert to using arrays instead of Lists. Getting rid of method
    // calls will likely improve the performance to match LinkedDLX.

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
    private final List<Object> NAME = new ArrayList<>();

    /**
     * Column header: index is the index of column <code>i</code>, value is
     * the index of column to the left of <code>i</code>.
     */
    private final List<Integer> LLINK = new ArrayList<>();

    /**
     * Column header: index is the index of column <code>i</code>, value is
     * the index of column to the right of <code>i</code>.
     */
    private final List<Integer> RLINK = new ArrayList<>();

    /**
     * Index is the index of item <code>x</code> value is the index of
     * <column>c</column> in which the index is.
     */
    private final List<Integer> TOP = new ArrayList<>();

    /**
     * The number of options the item in index <code>x</code> is part of.
     */
    private final List<Integer> LEN = TOP;

    /**
     * Row: index is the index of item <code>x</code>, value is the index of
     * item above <code>x</code>. Except when the item is a spacer between
     * rows, when the value is the index of the item in the beginning of the
     * row.
     */
    private final List<Integer> ULINK = new ArrayList<>();

    /**
     * Row: index is the index of item <code>x</code>, value is the index of
     * item below <code>x</code>. Except when the item is a spacer between
     * rows, when the value is the index of the item in the end of the row
     * below. Except when the item is the last item when the value is empty.
     */
    private final List<Integer> DLINK = new ArrayList<>();

    /**
     * The color associated to each item. Null corresponds to 0 in the Knuth
     * algorithm, non-null value corresponds to a positive value and PURIFIED
     * corresponds to negative value.
     */
    private final List<Object> COLOR = new ArrayList<>();

    /**
     * The option associated to each node in the matrix.
     */
    private final List<O> OPTION = new ArrayList<>();

    /**
     * Mapper that creates items for options.
     */
    private final OptionItemMapper<O> optionItemMapper;

    /**
     * Number of options. The value is negative. The actual number of options
     * is the absolute value.
     */
    private int numOptions = 0;

    private boolean dirty = false;

    /**
     * Has the data structure been initialized.
     */
    private boolean initialized = false;

    /**
     * Current solution. Cleared when algorithm finishes.
     */
    private LinkedList<O> solution;

    /**
     * Current solution consumer. Cleared when algorithm finishes.
     */
    private Consumer<List<O>> solutionConsumer;

    /**
     * Current emergency brake. Cleared when algorithm finishes.
     */
    private BooleanSupplier emergencyBrake;

    /**
     * @param optionItemMapper
     *      Mapper that creates the items that are covered by each option
     *      that is added to the matrix.
     */
    public ReferenceDLX(
            final OptionItemMapper<O> optionItemMapper) {
        this.optionItemMapper = optionItemMapper;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void addOption(O option) {
        ensureOpen();
        OPTION.add(option);
    }

    /**
     * Step C1
     */
    private void initMatrix() {
        ensureOpen();
        initialized = true;

        // Gather all items.
        final List<O> options = new ArrayList<>(OPTION);
        OPTION.clear();

        // Add header elements.
        NAME.add("header");
        LLINK.add(0);
        RLINK.add(0);
        LEN.add(null);
        COLOR.add(null);
        OPTION.add(null);
        ULINK.add(null);
        DLINK.add(null);

        // Gather items from the options and add them.
        final Set<Object> uniqueItems = options.stream()
                .map(optionItemMapper::from)
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
        ULINK.add(null);
        DLINK.add(null);

        for (O option : options) {
            final Collection<Object> items = optionItemMapper.from(option);

            int rowStart = TOP.size() - 1;
            for (Object item : items) {
                // Add item.
                int column = columnIndex(item);
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
            DLINK.add(null);
        }
    }

    // =========================================================== //
    // The DLX solution.

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

        this.solutionConsumer = solutionConsumer;
        this.emergencyBrake = emergencyBrake;

        if (isInitialized()) {
            initMatrix();
        }

        try {
            // Remove pre-selected options from the matrix.
            List<Object> items = preSelectedOptions
                    .stream()
                    .map(optionItemMapper::from)
                    .flatMap(Collection::stream)
                    .distinct()
                    .toList();
            for (Object item : items) {
                cover(columnIndex(item));
            }

            this.solution = new LinkedList<>(preSelectedOptions);
            recursiveSearch();

            for (Object item : items.reversed()) {
                uncover(columnIndex(item));
            }
        } finally {
            this.solution = null;
            this.solutionConsumer = null;
            this.emergencyBrake = null;
        }
        dirty = false;
    }

    /**
     * Step C2
     */
    private void recursiveSearch() {
        if (emergencyBrake.getAsBoolean()) {
            return;
        }

        if (RLINK.getFirst() == 0) {
            // Step C8
            solutionConsumer.accept(solution);
            return;
        }

        // Step C3
        int i = findColumn();

        // Step C4
        cover(i);
        int x1 = DLINK.get(i);

        // Step C5
        while (x1 != i) {
            int p = x1 + 1;
            while (p != x1) {
                int j = TOP.get(p);
                if (j <= 0) {
                    p = ULINK.get(p);
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
                int j = TOP.get(p);
                if (j <= 0) {
                    p = DLINK.get(p);
                } else {
                    uncommit(p, j);
                    p = p - 1;
                }
            }

            // Return to C5
            i = TOP.get(x1);
            x1 = DLINK.get(x1);
        }

        // C7 or return to C6 if we are in recursion.
        uncover(i);
    }

    /**
     * Find column using Minimum-Remaining-Value (MRV) heuristic. It chooses
     * the column with the fewest remaining values. Also called most
     * constrained variable or fail-first heuristics, because it helps in
     * discovering inconsistencies earlier.
     */
    private int findColumn() {
        int candidate = RLINK.getFirst();
        int smallest = candidate;
        while (candidate != 0) {
            if (LEN.get(candidate) < LEN.get(smallest)) {
                smallest = candidate;
            }

            candidate = RLINK.get(candidate);
        }

        return smallest;
    }

    // =================================================================== //
    // Operations for manipulating the matrix during search.

    private void commit(int p, int j) {
        Object c = COLOR.get(p);
        if (c == null) {
            cover(j);
        } else if (c != PURIFIED) {
            purify(p);
        }
    }

    private void purify(int p) {
        Object c = COLOR.get(p);
        int i = TOP.get(p);
        int q = DLINK.get(i);

        // Save color.
        COLOR.set(i, c);

        while (q != i) {
            if (Objects.equals(COLOR.get(q), c)) {
                COLOR.set(q, PURIFIED);
            } else {
                hide(q);
            }

            q = DLINK.get(q);
        }
    }

    private void cover(int i) {
        int p = DLINK.get(i);
        while (p != i) {
            hide(p);
            p = DLINK.get(p);
        }

        final int l = LLINK.get(i);
        final int r = RLINK.get(i);
        RLINK.set(l, r);
        LLINK.set(r, l);
    }

    private void hide(int p) {
        int q = p + 1;
        while (q != p) {
            int x = TOP.get(q);
            int u = ULINK.get(q);

            if (x <= 0) {
                q = u;
            } else {
                if (COLOR.get(q) != PURIFIED) {
                    int d = DLINK.get(q);
                    DLINK.set(u, d);
                    ULINK.set(d, u);
                    LEN.set(x, LEN.get(x) - 1);
                }
                q++;
            }

        }
    }

    private void uncommit(int p, int j) {
        Object c = COLOR.get(p);
        if (c == null) {
            uncover(j);
        } else if (c != PURIFIED) {
            unpurify(p);
        }
    }

    private void unpurify(int p) {
        int i = TOP.get(p);
        int q = ULINK.get(i);
        Object c = COLOR.get(i);

        while (q != i) {
            if (COLOR.get(q) == PURIFIED) {
                COLOR.set(q, c);
            } else {
                unhide(q);
            }

            q = ULINK.get(q);
        }

        COLOR.set(i, null);
    }

    private void uncover(int i) {
        final int l = LLINK.get(i);
        final int r = RLINK.get(i);
        RLINK.set(l, i);
        LLINK.set(r, i);

        int p = ULINK.get(i);
        while (p != i) {
            unhide(p);
            p = ULINK.get(p);
        }

    }

    private void unhide(int p) {
        int q = p - 1;
        while (q != p) {
            int x = TOP.get(q);
            int d = DLINK.get(q);

            if (x <= 0) {
                q = d;
            } else {
                if (COLOR.get(q) != PURIFIED) {
                    int u = ULINK.get(q);
                    DLINK.set(u, q);
                    ULINK.set(d, q);
                    LEN.set(x, LEN.get(x) + 1);
                }
                q--;
            }
        }
    }

    // =========================================================== //
    // Auxiliary methods.

    private int columnIndex(Object item) {
        for (int i = 1; i < NAME.size(); i++) {
            if (Objects.equals(item, NAME.get(i))) {
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
