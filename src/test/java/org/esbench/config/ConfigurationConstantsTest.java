package org.esbench.config;

import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.Test;

public class ConfigurationConstantsTest {
	@Test
	public void validate() {
		UtilityClassValidator.validate(ConfigurationConstants.class);
	}
}
