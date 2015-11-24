package org.esbench.cmd;

import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.Test;

public class CommandPropsConstantsTest {
	@Test
	public void validate() {
		UtilityClassValidator.validate(CommandPropsConstants.class);
	}
}
