package org.esbench.elastic.sender;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.esbench.cmd.CommandPropsConstants;
import org.esbench.core.DefaultProperties;
import org.esbench.core.ResourceUtils;
import org.esbench.generator.document.DocumentFactory;
import org.esbench.testng.AbstractSharedElasticSearchIntegrationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DocumentSenderIntegrationTest extends AbstractSharedElasticSearchIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentSenderIntegrationTest.class);
	private static final String INDEX_TYPE = "type";
	private static final String INDEX_NAME = "index";
	private static final int TOTAL_DOCS = 100;
	private Client client;

	@BeforeClass
	public void initCluster() throws IOException {
		client = getClient();

		CreateIndexRequest indexRequest = new CreateIndexRequest(INDEX_NAME);
		assertTrue(client.admin().indices().create(indexRequest).actionGet().isAcknowledged());

		client.admin().indices().flush(new FlushRequest(INDEX_NAME)).actionGet();
		insertDocuments();
	}

	private void insertDocuments() throws IOException {
		SimpleInsertAction action = new SimpleInsertAction(new DocumentSenderFactory());
		Properties inputProps = new Properties();
		inputProps.put(CommandPropsConstants.INDEX_OPT, INDEX_NAME);
		inputProps.put(CommandPropsConstants.TYPE_OPT, INDEX_TYPE);
		inputProps.put(InsertProperties.DOCS, String.valueOf(TOTAL_DOCS));
		Properties resourceProps = ResourceUtils.asProperties("default.properties");
		DefaultProperties defaults = new DefaultProperties(inputProps, resourceProps);
		InsertProperties insProperties = new InsertProperties(defaults);

		String workloadAsText = ResourceUtils.asString("configuration/config02.json");
		DocumentFactory<String> factory = action.getFactory(insProperties, new StringReader(workloadAsText));

		DocumentSender sender = new DocumentSenderImpl(client);
		sender.send(factory, insProperties);

		client.admin().indices().flush(new FlushRequest(INDEX_NAME)).actionGet();
	}

	@Test
	public void insert() {

		verifySearch(new MatchQueryBuilder("type", "x y z"), TOTAL_DOCS);

		verifySearch(new MatchQueryBuilder("type", "z"), TOTAL_DOCS);

		verifySearch(new MatchQueryBuilder("title", "x"), TOTAL_DOCS / 2);

		verifySearch(new RangeQueryBuilder("page_views").from(50).to(100), TOTAL_DOCS);

		verifySearch(new TermQueryBuilder("latest", true), TOTAL_DOCS / 2);
		verifySearch(new TermQueryBuilder("latest", false), TOTAL_DOCS / 2);
	}

	private void verifySearch(QueryBuilder query, int expectedNumOfDocs) {
		SearchRequestBuilder builder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
		builder.setQuery(query);
		SearchResponse response = client.search(builder.request()).actionGet();
		assertEquals(response.getHits().getTotalHits(), expectedNumOfDocs);
	}
}
