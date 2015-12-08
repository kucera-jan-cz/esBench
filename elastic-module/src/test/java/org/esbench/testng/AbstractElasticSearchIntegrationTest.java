package org.esbench.testng;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.xml.XmlTest;

public class AbstractElasticSearchIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticSearchIntegrationTest.class);
	private EmbeddedElasticSearchServer embeddedServer;

	@BeforeClass
	public void startServer(ITestContext context, XmlTest test) {
		String name = getTestName();
		String dir = "target/es-data" + name;
		LOGGER.warn("Creating Elasticsearch {}", name);
		embeddedServer = new EmbeddedElasticSearchServer(dir, name);
	}

	@AfterClass
	public void stopServer(XmlTest test) {
		String name = getTestName();
		LOGGER.warn("Stopping Elasticsarch {}", name);
		embeddedServer.shutdown();
	}

	private String getTestName() {
		String name = this.getClass().getName();
		name = name.substring(name.lastIndexOf('.') + 1);
		return name;
	}

	protected Client getClient() {
		return embeddedServer.getClient();
	}

	protected String getClusterName() {
		return embeddedServer.getClusterName();
	}
}
