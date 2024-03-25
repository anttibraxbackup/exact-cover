package fi.iki.asb.xcc.examples.pentomino;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PentominoTest {

	@Test
	public void givenStringData_shouldParsePentomino() {
		Pentomino i = Pentomino.parse("IIIII");

		assertEquals('I', i.identifier());
		assertEquals(1, i.height());
		assertEquals(5, i.width());

		assertTrue(i.hasSquare(0, 0));
		assertTrue(i.hasSquare(0, 1));
		assertTrue(i.hasSquare(0, 2));
		assertTrue(i.hasSquare(0, 3));
		assertTrue(i.hasSquare(0, 4));
	}

	@Test
	public void givenPentomino_whenFlipped_shouldContainCorrectData() {
		Pentomino i = Pentominoes.F;
		i = i.flip();

		assertEquals('F', i.identifier());
		assertEquals(3, i.height());
		assertEquals(3, i.width());

		assertTrue(i.hasSquare(0, 0));
		assertTrue(i.hasSquare(0, 1));
		assertFalse(i.hasSquare(0, 2));

		assertFalse(i.hasSquare(1, 0));
		assertTrue(i.hasSquare(1, 1));
		assertTrue(i.hasSquare(1, 2));

		assertFalse(i.hasSquare(2, 0));
		assertTrue(i.hasSquare(2, 1));
		assertFalse(i.hasSquare(2, 2));
	}
}
