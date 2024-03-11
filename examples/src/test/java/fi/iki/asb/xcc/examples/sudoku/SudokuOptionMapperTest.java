package fi.iki.asb.xcc.examples.sudoku;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

public class SudokuOptionMapperTest {

	@Test
	public void givenSize4_shouldReturnCorrect() {
		SudokuOptionMapper mapper = new SudokuOptionMapper(4);

		Collection<Object> expected = Arrays.asList(5, 20, 36, 48);
		Collection<Object> actual = mapper.from(new SudokuCell(1, 2, 2));
		assertEquals(expected, actual);

		expected = Arrays.asList(12, 30, 34, 58);
		actual = mapper.from(new SudokuCell(3, 4, 1));
		assertEquals(expected, actual);
	}
}
