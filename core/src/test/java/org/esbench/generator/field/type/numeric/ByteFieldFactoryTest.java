package org.esbench.generator.field.type.numeric;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ByteFieldFactoryTest {

	@Test
	public void newInstance() {
		byte from = 0;
		byte to = Byte.MAX_VALUE;
		byte step = 1;

		ByteFieldFactory factory = new ByteFieldFactory(from, to, step);
		for(int i = 0; i < 126; i++) {
			Byte value = factory.newInstance(i);
			assertEquals(value.byteValue(), new Integer(i).byteValue());
		}
		for(int i = 127; i < 254; i++) {
			Byte value = factory.newInstance(i);
			assertEquals(value.byteValue(), new Integer(i - 127).byteValue());
		}
	}

	@Test
	public void newInstanceNegative() {
		byte from = Byte.MIN_VALUE;
		byte to = Byte.MAX_VALUE;
		byte step = 1;

		Byte value;
		ByteFieldFactory factory = new ByteFieldFactory(from, to, step);
		int expected = -128;
		for(int i = 0; i < 255; i++) {
			value = factory.newInstance(i);
			assertEquals(value.byteValue(), new Integer(expected).byteValue());
			expected++;
		}
		value = factory.newInstance(255);
		assertEquals(value.byteValue(), new Integer(-128).byteValue());
		value = factory.newInstance(256);
		assertEquals(value.byteValue(), new Integer(-127).byteValue());
	}
}
