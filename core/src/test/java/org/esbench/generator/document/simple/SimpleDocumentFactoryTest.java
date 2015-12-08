package org.esbench.generator.document.simple;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.esbench.testng.ResourcesUtils;
import org.esbench.workload.Workload;
import org.esbench.workload.json.WorkloadParser;
import org.testng.annotations.Test;

import net.javacrumbs.jsonunit.JsonAssert;

public class SimpleDocumentFactoryTest {
	@Test
	public void newInstance() throws IOException {
		WorkloadParser parser = new WorkloadParser();
		Reader reader = new StringReader(ResourcesUtils.loadAsString("workloads/simple-config-01.json"));
		Workload config = parser.parse(reader);
		SimpleDocumentFactory factory = new SimpleDocumentFactory(config.getIndiceTypes().get(0));
		String fstJson = factory.newInstance(0);
		String expected = ResourcesUtils.loadAsString("documents/expected-simple-config-01-A.json");
		JsonAssert.assertJsonEquals(expected, fstJson);

		String sndJson = factory.newInstance(0);
		expected = ResourcesUtils.loadAsString("documents/expected-simple-config-01-A.json");
		JsonAssert.assertJsonEquals(expected, sndJson);
	}
}
