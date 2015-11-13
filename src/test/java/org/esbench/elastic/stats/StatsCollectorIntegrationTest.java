package org.esbench.elastic.stats;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingAction;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.meta.IndexMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.testng.AbstractElasticSearchIntegrationTest;
import org.esbench.testng.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

public class StatsCollectorIntegrationTest extends AbstractElasticSearchIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatsCollectorIntegrationTest.class);
	private static final String INDEX_TYPE = "typeA";
	private static final String INDEX_NAME = "types";
	private Client client;

	private StringFieldMetadata fStringMeta = new StringFieldMetadata("fString", 1, 5,
			Arrays.asList("welcome", "to", "esbench", "the", "desert", "of", "our", "reality"));
	private BooleanFieldMetadata fBooleanMeta = new BooleanFieldMetadata("fBoolean");
	private DateFieldMetadata fDateMeta = new DateFieldMetadata("fDate", 1, Instant.parse("2015-11-08T00:00:00Z"), Instant.parse("2015-11-10T23:59:59Z"), 5,
			ChronoUnit.HALF_DAYS, MetadataConstants.DEFAULT_DATE_PATTERN);
	private IPv4FieldMetadata fIpMeta = new IPv4FieldMetadata("fIp", 1, "192.168.0.0/21");
	private List<FieldMetadata> objectInnerMetadata = Arrays.asList(new StringFieldMetadata("fObject.oString", 1, 3, Arrays.asList("a", "b", "c")),
			new IPv4FieldMetadata("fObject.oIp", 1, "192.168.44.10/32"));
	private ObjectTypeMetadata fObjectMeta = new ObjectTypeMetadata("fObject", objectInnerMetadata);

	private IPv4FieldMetadata nnLongMeta = new IPv4FieldMetadata("fNested.nNested.nIp", 1, "127.0.0.101/32");
	private IPv4FieldMetadata nnIpMeta = new IPv4FieldMetadata("fNested.nNested.nIp", 1, "127.0.0.101/32");
	private StringFieldMetadata nnsStringField = new StringFieldMetadata("fNested.nNested.nString", 1, 4, Arrays.asList("k", "l", "m", "n"));
	private ObjectTypeMetadata nNestedField = new ObjectTypeMetadata("fNested.nNested", Arrays.asList(nnsStringField, nnIpMeta));

	private List<FieldMetadata> nestedInnerMetadata = Arrays.asList(new StringFieldMetadata("fNested.nString", 1, 2, Arrays.asList("xy", "z")), nNestedField);
	private ObjectTypeMetadata fNestedMeta = new ObjectTypeMetadata("fNested", nestedInnerMetadata);

	@BeforeClass
	public void initCluster() throws IOException {
		super.startServer();
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

	@Test
	public void init() throws IOException {
		StatsCollector collector = new StatsCollector(client);
		IndexMetadata indexMeta = collector.collectMapping();
		assertEquals(indexMeta.getName(), INDEX_NAME);
		assertEquals(indexMeta.getTypes().size(), 1);

		IndexTypeMetadata typeMeta = indexMeta.getTypes().get(0);
		assertEquals(typeMeta.getTypeName(), INDEX_TYPE);
		List<StringFieldMetadata> stringMetas = filterMetadata(typeMeta.getFields(), StringFieldMetadata.class);
		assertEquals(stringMetas.size(), 1);
		assertEquals(stringMetas.get(0), fStringMeta);

		List<BooleanFieldMetadata> booleanMetas = filterMetadata(typeMeta.getFields(), BooleanFieldMetadata.class);
		assertEquals(booleanMetas.size(), 1);
		assertEquals(booleanMetas.get(0), fBooleanMeta);

		List<DateFieldMetadata> dateMetas = filterMetadata(typeMeta.getFields(), DateFieldMetadata.class);
		assertEquals(dateMetas.size(), 1);
		assertEquals(dateMetas.get(0), fDateMeta);

		List<IPv4FieldMetadata> ipMetas = filterMetadata(typeMeta.getFields(), IPv4FieldMetadata.class);
		assertEquals(ipMetas.size(), 1);
		assertEquals(ipMetas.get(0), fIpMeta);

		List<ObjectTypeMetadata> objectMeta = filterMetadata(typeMeta.getFields(), ObjectTypeMetadata.class);
		assertEquals(objectMeta.size(), 2);
		Collections.sort(objectMeta, (a, b) -> a.getFullPath().compareTo(b.getFullPath()));
		assertEquals(objectMeta.get(1), fObjectMeta);
		// assertEquals(objectMeta.get(0), fNestedMeta);

		ReflectionAssert.assertReflectionEquals(objectMeta.get(0), fNestedMeta, ReflectionComparatorMode.LENIENT_ORDER);
		client.close();
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> filterMetadata(List<FieldMetadata> metadata, Class<T> clazz) {
		List<FieldMetadata> filtered = metadata.stream().filter(m -> clazz.isInstance(m)).collect(Collectors.toList());
		List<T> meta = (List<T>) filtered;
		return meta;
	}
}