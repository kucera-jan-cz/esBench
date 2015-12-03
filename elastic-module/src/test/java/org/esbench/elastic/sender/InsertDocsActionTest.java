package org.esbench.elastic.sender;

import org.esbench.workload.Workload;

public class InsertDocsActionTest {

	public void getIndexType() {
		InsertDocsAction action = new InsertDocsAction();
		InsertProperties properties = null;
		Workload configuration = null;
		action.getIndexType(properties, configuration);
	}
}
