package org.esbench.generator.field;

import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.Test;

public class FieldConstantsTest {

	@Test
	public void validate() {
		UtilityClassValidator.validate(FieldConstants.class);
	}
}
