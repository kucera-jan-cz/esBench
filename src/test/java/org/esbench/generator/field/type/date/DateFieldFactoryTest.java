package org.esbench.generator.field.type.date;

import static org.testng.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

public class DateFieldFactoryTest {
	@Test
	public void newInstance() {
		ChronoUnit unit = ChronoUnit.MINUTES;
		LocalDateTime from = LocalDateTime.parse("2011-12-03T00:00:00");
		int modulo = 10;
		long step = 5L;
		DateFieldFactory factory = new DateFieldFactory(from, step, unit, modulo);
		LocalDateTime expected = from;

		for(int i = 0; i < modulo; i++) {
			LocalDateTime instance = factory.newInstance(i);
			assertEquals(instance, expected);
			expected = expected.plus(step, unit);
		}

		assertEquals(factory.newInstance(modulo), from);
	}
}
