package fi.iki.asb.xcc;

import java.util.LinkedList;

/**
 * Progress reporter that estimates the progress based on the number of
 * items processed on each recursion level. The estimate is more accurate
 * if the matrix is consistent (each column has a similar number of items
 * and the items are linked similarly).
 */
public class ProgressReporter implements XCCTrace {

    public static ProgressReporter systemOut(long reportInterval) {
        return new ProgressReporter(
                (i, p) -> System.out.printf("Progress: %d items, %.5f\n", i, p),
                reportInterval);
    }

    @FunctionalInterface
    public interface ProgressConsumer {
        void consume(long itemCount, double progress);
    }

    /**
     * Progress record for one recursion level.
     */
    private static class Progress {

        /**
         * Product of the item counts on each recursion level so far.
         */
        final double itemCountProduct;

        /**
         * Progress when this recursion level was entered.
         */
        final double initialProgress;

        /**
         * Current progress.
         */
        double currentProgress;

        /**
         * Number of item being processed on this recursion level.
         */
        int currentItem;

        Progress() {
            this.initialProgress = 0.0;
            this.currentProgress = 0.0;
            this.itemCountProduct = 1;
        }

        Progress(Progress previousProgress, int itemCount) {
            itemCountProduct = previousProgress.itemCountProduct * itemCount;
            initialProgress = previousProgress.currentProgress;
            currentProgress = previousProgress.currentProgress;
            currentItem = 0;
        }

        final void nextItem() {
            // The currentItem++ is a shorthand for (c - 1). See TAOCP for
            // (not much) more information on the progress calculation.
            currentProgress = initialProgress + (currentItem++ / itemCountProduct);
        }
    }

    // =================================================================== //

    private final ProgressConsumer progressConsumer;

    /**
     * Report progress when every N items have been tried.
     */
    private final long reportInterval;

    /**
     * "Stack" of progress records.
     */
    private final LinkedList<Progress> stack = new LinkedList<>();

    /**
     * Progress record for current recursion level.
     */
    private Progress progress;

    /**
     * Number of items tried so far.
     */
    private long itemsTried = 0;

    // =================================================================== //

    /**
     * @param reportInterval
     *      Report progress when every N items have been tried.
     */
    public ProgressReporter(
            final ProgressConsumer progressConsumer,
            final long reportInterval) {
        this.progressConsumer = progressConsumer;
        this.reportInterval = reportInterval;
    }

    @Override
    public void onSearchStarted() {
        stack.clear();
        progress = new Progress();
    }

    @Override
    public void onRecursionEntered(int itemCount) {
        stack.push(progress);
        progress = new Progress(progress, itemCount);
    }

    @Override
    public void onItemSelected() {
        progress.nextItem();

        if (++itemsTried % reportInterval == 0) {
            progressConsumer.consume(itemsTried, getProgress());
        }
    }

    @Override
    public void onRecursionEnded() {
        progress = stack.pop();
    }

    public double getProgress() {
        return (progress != null)
                ? progress.currentProgress + (0.5 / progress.itemCountProduct)
                : Double.NaN;
    }

    public long getItemsTried() {
        return itemsTried;
    }
}
