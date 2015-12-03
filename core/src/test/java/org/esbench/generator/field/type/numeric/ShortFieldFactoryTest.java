package org.esbench.generator.field.type.numeric;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ShortFieldFactoryTest {

	@Test
	public void newInstance() {
		short from = 0;
		short to = Short.MAX_VALUE;
		short step = 1;

		ShortFieldFactory factory = new ShortFieldFactory(from, to, step);
		for(int i = 0; i < 32767; i++) {
			Short value = factory.newInstance(i);
			assertEquals(value.shortValue(), new Integer(i).shortValue());
		}
		for(int i = 32767; i < 32767 * 2; i++) {
			Short value = factory.newInstance(i);
			assertEquals(value.shortValue(), new Integer(i - 32767).shortValue());
		}
	}

	@Test
	public void newInstanceNegative() {
		short from = Short.MIN_VALUE;
		short to = Short.MAX_VALUE;
		short step = 1;

		Short value;
		ShortFieldFactory factory = new ShortFieldFactory(from, to, step);
		int expected = -32768;
		for(int i = 0; i < 32767 * 2; i++) {
			value = factory.newInstance(i);
			assertEquals(value.shortValue(), new Integer(expected).shortValue());
			expected++;
		}
		value = factory.newInstance(32768);
		assertEquals(value.shortValue(), new Integer(0).shortValue());
		value = factory.newInstance(32769);
		assertEquals(value.shortValue(), new Integer(1).shortValue());
	}
}
