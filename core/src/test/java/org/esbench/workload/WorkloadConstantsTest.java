package org.esbench.workload;

import org.esbench.testng.UtilityClassValidator;
import org.esbench.workload.WorkloadConstants;
import org.testng.annotations.Test;

public class WorkloadConstantsTest {
	@Test
	public void validate() {
		UtilityClassValidator.validate(WorkloadConstants.class);
	}
}
