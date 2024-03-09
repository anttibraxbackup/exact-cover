package fi.iki.asb.xcc;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A generic exact cover with color solver. An implementation of Knuth's
 * "Algorithm X" using dancing links.
 *
 * <p>This class is <i>not thread safe</i>.</p>
 *
 * @param <O>
 *     The type associated to options. For example, in a sudoku solver this
 *     might be a class that represents "placing a number in a given row,
 *     column location".
 */
public interface XCC<O> {

    /**
     * Is the matrix dirty? If a search is interrupted by an exception, the
     * matrix is left dirty and cannot be reused. The XCC instance must be
     * recreated.
     */
    boolean isDirty();

    /**
     * Add an option. The items covered by the option are generated using
     * the <code>OptionItemMapper</code>.
     *
     * @throws IllegalStateException
     * 		A previous <code>search</code> was interrupted by an exception
     *      and the matrix was left dirty. The XCC instance must be
     *      recreated.
     */
    void addOption(final O option);

    /**
     * Search for exact cover solution. The default implementation will not
     * use an emergency brake, therefore it will find every possible solution
     * for the problem and can call the solutionConsumer multiple times.
     *
     * @param solutionConsumer
     * 		The consumer which collects the results. Cannot be null.
     *
     * @throws IllegalStateException
     *      A previous <code>search</code> was interrupted by an exception
     *      and the matrix was left dirty. The XCC instance must be
     *      recreated.
     */
    default void search(final Consumer<List<O>> solutionConsumer) {
        search(solutionConsumer, new ArrayList<>(), () -> false);
    }

    /**
     * Search for exact cover solution with an emergency brake.
     *
     * @param solutionConsumer
     *      The consumer which collects the results. Cannot be null.
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
    default void search(
            final Consumer<List<O>> solutionConsumer,
            final BooleanSupplier emergencyBrake) {
        search(solutionConsumer, new ArrayList<>(), emergencyBrake);
    }

    /**
     * Search for exact cover solution with pre-selected options. The default
     * implementation will not use an emergency brake, therefore it will find
     * every possible solution for the problem and can call the
     * solutionConsumer multiple times.
     *
     * @param solutionConsumer
     *      The consumer which collects the results. Cannot be null.
     *
     * @param preSelectedOptions
     *      The options that are pre-selected to be part of the solution
     *      (for example the initial numbers given in a sudoku puzzle).
     *      This object will be reused as the container for solutions
     *      found by the algorithm. Cannot be null. Can be empty.
     *
     * @throws IllegalStateException
     *      A previous <code>search</code> was interrupted by an exception
     *      and the matrix was left dirty. The XCC instance must be
     *      recreated.
     */
    default void search(
            final Consumer<List<O>> solutionConsumer,
            final List<O> preSelectedOptions) {
        search(solutionConsumer, preSelectedOptions, () -> false);
    }

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
    void search(
            final Consumer<List<O>> solutionConsumer,
            final List<O> preSelectedOptions,
            final BooleanSupplier emergencyBrake);

}
