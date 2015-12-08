package org.esbench.generator.document.simple.builder;

import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.Test;

public class NumericFieldBuilderTest {
	@Test
	public void validate() {
		UtilityClassValidator.validate(NumericFieldBuilder.class);
	}
}
