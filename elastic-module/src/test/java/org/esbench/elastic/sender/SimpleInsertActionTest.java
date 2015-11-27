package org.esbench.elastic.sender;

import org.esbench.workload.Workload;

public class SimpleInsertActionTest {

	public void getIndexType() {
		SimpleInsertAction action = new SimpleInsertAction();
		InsertProperties properties = null;
		Workload configuration = null;
		action.getIndexType(properties, configuration);
	}
}
