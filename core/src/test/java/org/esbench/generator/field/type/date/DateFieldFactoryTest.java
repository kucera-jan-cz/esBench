package org.esbench.generator.field.type.date;

import static org.testng.Assert.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

public class DateFieldFactoryTest {
	@Test
	public void newInstance() {
		ChronoUnit unit = ChronoUnit.MINUTES;
		Instant from = Instant.parse("2011-12-03T00:00:00Z");
		int modulo = 10;
		long step = 5L;
		DateFieldFactory factory = new DateFieldFactory(from, step, unit, modulo);
		Instant expected = from;

		for(int i = 0; i < modulo; i++) {
			Instant instance = factory.newInstance(i);
			assertEquals(instance, expected);
			expected = expected.plus(step, unit);
		}

		assertEquals(factory.newInstance(modulo), from);
	}
}
