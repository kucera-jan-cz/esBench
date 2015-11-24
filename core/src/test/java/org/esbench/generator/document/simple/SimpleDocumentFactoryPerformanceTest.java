package org.esbench.generator.document.simple;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.esbench.testng.ResourcesUtils;
import org.esbench.workload.Workload;
import org.esbench.workload.json.WorkloadParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class SimpleDocumentFactoryPerformanceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDocumentFactoryPerformanceTest.class);
	private static final boolean ENABLED = false;
	private static final int TOTAL_DOCS = 5_000_000;

	@Test(enabled = ENABLED)
	public void newInstance() throws IOException {
		WorkloadParser parser = new WorkloadParser();
		Reader reader = new StringReader(ResourcesUtils.loadAsString("configuration/bgg-histogram.json"));
		Workload config = parser.parse(reader);
		SimpleDocumentFactory factory = new SimpleDocumentFactory(config.getIndiceTypes().get(0));
		long start = System.currentTimeMillis();
		for(int i = 0; i < TOTAL_DOCS; i++) {
			assertNotNull(factory.newInstance(i));
		}
		long end = System.currentTimeMillis();
		long totalTimeInSeconds = (end - start) / 1000L;
		double docsPerSec = (TOTAL_DOCS * 1.0) / totalTimeInSeconds;
		LOGGER.info("{} docs in {} seconds, {} docs/per sec ", TOTAL_DOCS, totalTimeInSeconds, docsPerSec);
	}
}
