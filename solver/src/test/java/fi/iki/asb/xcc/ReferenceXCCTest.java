package fi.iki.asb.xcc;

import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Collections;

/**
 * Tests unique to reference XCC.
 */
public class ReferenceXCCTest {

    @Test
    public void cannotModifyMatrixAfterInitialization() {
        ReferenceXCC<Object> xcc = new ReferenceXCC<>(Collections::singletonList);
        xcc.addOption("A");
        xcc.search(s -> {});

        try {
            xcc.addOption("B");
            fail();
        } catch (IllegalStateException ex) {
            // Ok.
        }
    }
}
