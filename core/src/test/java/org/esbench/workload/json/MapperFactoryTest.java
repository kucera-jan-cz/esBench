package org.esbench.workload.json;

import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.Test;

public class MapperFactoryTest {
	@Test
	public void validate() {
		UtilityClassValidator.validate(MapperFactory.class);
	}
}
