package fi.iki.asb.xcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProgressReporterTest {

    private static final double DELTA = 0.00001;

    @Test
    public void shouldCalculateProgress() {
        ProgressReporter pr = new ProgressReporter(
                (i, p) -> {}, 1);

        pr.onSearchStarted();
        assertEquals(0.5, pr.getProgress(), DELTA);
        assertEquals(0, pr.getItemsTried());

        pr.onRecursionEntered(2);
        assertEquals(0.25, pr.getProgress(), DELTA);
        assertEquals(0, pr.getItemsTried());

        pr.onItemSelected();
        assertEquals(0.25, pr.getProgress(), DELTA);
        assertEquals(1, pr.getItemsTried());

        pr.onItemSelected();
        assertEquals(0.75, pr.getProgress(), DELTA);
        assertEquals(2, pr.getItemsTried());

        pr.onRecursionEntered(2);
        assertEquals(0.625, pr.getProgress(), DELTA);
        assertEquals(2, pr.getItemsTried());

        pr.onItemSelected();
        assertEquals(0.625, pr.getProgress(), DELTA);
        assertEquals(3, pr.getItemsTried());

        pr.onItemSelected();
        assertEquals(0.875, pr.getProgress(), DELTA);
        assertEquals(4, pr.getItemsTried());

        pr.onRecursionEnded();
    }

    @Test
    public void shouldNotCalculateProgressBeforeExecution() {
        ProgressReporter pr = ProgressReporter.systemOut(5);
        assertTrue(Double.isNaN(pr.getProgress()));
    }
}
