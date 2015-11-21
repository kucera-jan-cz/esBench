package org.esbench.generator.field;

import static org.testng.Assert.assertNull;

import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.Test;

public class NullFieldFactoryTest {

	@Test(invocationCount = 10)
	public void newInstance() {
		NullFieldFactory factory = new NullFieldFactory();
		assertNull(factory.newInstance(RandomUtils.nextInt(0, Integer.MAX_VALUE)));
	}
}
