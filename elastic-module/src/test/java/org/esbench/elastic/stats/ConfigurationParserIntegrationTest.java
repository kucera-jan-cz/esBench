package org.esbench.elastic.stats;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
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
import org.esbench.core.DefaultProperties;
import org.esbench.core.ResourceUtils;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.testng.AbstractSharedElasticSearchIntegrationTest;
import org.esbench.testng.ResourcesUtils;
import org.esbench.workload.Workload;
import org.esbench.workload.WorkloadConstants;
import org.esbench.workload.json.WorkloadParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ConfigurationParserIntegrationTest extends AbstractSharedElasticSearchIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationParserIntegrationTest.class);
	private static final String INDEX_TYPE = "typeA";
	private static final String INDEX_NAME = "types";
	private Client client;
	private DefaultProperties defaultProperties;

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
		Properties props = new Properties();
		defaultProperties = new DefaultProperties(props, ResourceUtils.asProperties("default.properties"));

	}

	@AfterClass
	public void deleteIndex() throws InterruptedException, ExecutionException {
		client.admin().indices().delete(new DeleteIndexRequest(INDEX_NAME)).get();
	}

	@Test
	public void init() throws IOException {
		StatsCollector collector = new StatsCollector(client, new CollectorProperties(defaultProperties), INDEX_NAME);
		List<IndexTypeMetadata> indexList = collector.collectIndex();
		assertEquals(indexList.size(), 1);
		IndexTypeMetadata meta = indexList.get(0);
		assertEquals(meta.getIndexName(), INDEX_NAME);
		Workload config = new Workload(WorkloadConstants.CURRENT_VERSION, MetadataConstants.DEFAULT_META_BY_TYPE, Arrays.asList(meta));
		WorkloadParser parser = new WorkloadParser();
		StringWriter writer = new StringWriter();
		parser.parse(writer, config);
		LOGGER.info("\n{}", writer.toString());
	}
}