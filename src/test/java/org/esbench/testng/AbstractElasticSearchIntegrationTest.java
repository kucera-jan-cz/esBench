package org.esbench.testng;

import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Before;

public class AbstractElasticSearchIntegrationTest {
	private EmbeddedElasticSearchServer embeddedServer;

	@Before
	public void startServer() {
		embeddedServer = new EmbeddedElasticSearchServer();
	}

	@After
	public void stopServer() {
		embeddedServer.shutdown();
	}

	protected Client getClient() {
		return embeddedServer.getClient();
	}
}
