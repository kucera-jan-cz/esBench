package org.esbench.elastic.sender.cluster;

import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.Test;

public class ClusterConstantsTest {
	@Test
	public void validate() {
		UtilityClassValidator.validate(ClusterConstants.class);
	}
}
