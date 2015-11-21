package org.esbench.elastic.stats;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingAction;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.esbench.testng.AbstractSharedElasticSearchIntegrationTest;
import org.esbench.testng.ResourcesUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ConfigurationWriterTest extends AbstractSharedElasticSearchIntegrationTest {
	private static final String INDEX_TYPE = "typeA";
	private static final String INDEX_NAME = "types";
	private Client client;

	@BeforeClass
	public void initCluster() throws IOException {
		client = getClient();

		CreateIndexRequest indexRequest = new CreateIndexRequest(INDEX_NAME);
		assertTrue(client.admin().indices().create(indexRequest).actionGet().isAcknowledged());

		String mapping = ResourcesUtils.loadAsString("mapping_request.json");
		PutMappingRequestBuilder builder = new PutMappingRequestBuilder(client, PutMappingAction.INSTANCE);
		PutMappingRequest request = builder.setIndices(INDEX_NAME).setType(INDEX_TYPE).setSource(mapping).request();
		assertTrue(client.admin().indices().putMapping(request).actionGet().isAcknowledged());

		String doc01 = ResourcesUtils.loadAsString("documents/doc01.json");
		String doc02 = ResourcesUtils.loadAsString("documents/doc02.json");
		IndexRequestBuilder indexBuilder = new IndexRequestBuilder(client, IndexAction.INSTANCE, INDEX_NAME).setType(INDEX_TYPE);
		assertTrue(client.index(indexBuilder.setId("1").setSource(doc01).request()).actionGet().isCreated());
		assertTrue(client.index(indexBuilder.setId("2").setSource(doc02).request()).actionGet().isCreated());
		client.admin().indices().flush(new FlushRequest(INDEX_NAME)).actionGet();
	}

	@AfterClass
	public void deleteIndex() throws InterruptedException, ExecutionException {
		client.admin().indices().delete(new DeleteIndexRequest(INDEX_NAME)).get();
	}

	@Test
	public void writeConfiguration() throws IOException {
		ConfigurationWriter writer = new ConfigurationWriter(getClient());
		writer.saveConfiguration();
	}
}
