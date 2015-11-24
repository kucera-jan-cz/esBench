package org.esbench.generator.field.meta;

import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.Test;

public class MetadataConstantsTest {
	@Test
	public void validate() {
		UtilityClassValidator.validate(MetadataConstants.class);
	}
}
