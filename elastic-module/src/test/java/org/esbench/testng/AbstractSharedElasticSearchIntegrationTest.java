package org.esbench.testng;

import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.xml.XmlTest;

public class AbstractSharedElasticSearchIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSharedElasticSearchIntegrationTest.class);
	private static EmbeddedElasticSearchServer embeddedServer;

	@BeforeSuite
	public void startServer(ITestContext context, XmlTest test) {
		String name = context.getSuite().getName();
		String dir = "target/es-data" + name;
		LOGGER.warn("Creating Elasticsearch {}", name);
		embeddedServer = new EmbeddedElasticSearchServer(dir, name);
	}

	@AfterClass
	public void clearServer() throws InterruptedException, ExecutionException {
		Client client = getClient();
		client.admin().indices().delete(new DeleteIndexRequest("_all")).get();
	}

	@AfterSuite
	public void stopServer(ITestContext context) {
		String name = context.getSuite().getName();
		LOGGER.warn("Stopping Elasticsarch {}", name);
		embeddedServer.shutdown();
	}

	protected Client getClient() {
		return embeddedServer.getClient();
	}
}
