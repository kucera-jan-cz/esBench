package org.esbench.elastic.stats;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.testng.AbstractSharedElasticSearchIntegrationTest;
import org.esbench.testng.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

public class StatsCollectorIntegrationTest extends AbstractSharedElasticSearchIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatsCollectorIntegrationTest.class);
	private static final String INDEX_TYPE = "typeA";
	private static final String INDEX_NAME = "types";
	private Client client;

	private final StringFieldMetadata fStringMeta = new StringFieldMetadata("fString", 1, 5,
			Arrays.asList("welcome", "to", "esbench", "the", "desert", "of", "our", "reality"));
	private final BooleanFieldMetadata fBooleanMeta = new BooleanFieldMetadata("fBoolean");
	private final NumericFieldMetadata fIntegerMeta = new NumericFieldMetadata("fInteger", 1, MetaType.INTEGER, 0, 10, 1);
	private final NumericFieldMetadata fLongMeta = new NumericFieldMetadata("fLong", 1, MetaType.LONG, 2147483647L, Integer.MAX_VALUE + 10L, 1L);
	private final DateFieldMetadata fDateMeta = new DateFieldMetadata("fDate", 1, Instant.parse("2015-11-08T00:00:00Z"), Instant.parse("2015-11-10T23:59:59Z"),
			2, ChronoUnit.HALF_DAYS, MetadataConstants.DEFAULT_DATE_PATTERN);
	private final IPv4FieldMetadata fIpMeta = new IPv4FieldMetadata("fIp", 1, "192.168.0.0/21");
	private final List<FieldMetadata> objectInnerMetadata = Arrays.asList(new StringFieldMetadata("fObject.oString", 1, 3, Arrays.asList("a", "b", "c")),
			new IPv4FieldMetadata("fObject.oIp", 1, "192.168.44.10/32"));
	private final ObjectTypeMetadata fObjectMeta = new ObjectTypeMetadata("fObject", objectInnerMetadata);

	private final IPv4FieldMetadata nnLongMeta = new IPv4FieldMetadata("fNested.nNested.nIp", 1, "127.0.0.101/32");
	private final IPv4FieldMetadata nnIpMeta = new IPv4FieldMetadata("fNested.nNested.nIp", 1, "127.0.0.101/32");
	private final StringFieldMetadata nnsStringField = new StringFieldMetadata("fNested.nNested.nString", 1, 4, Arrays.asList("k", "l", "m", "n"));
	private final ObjectTypeMetadata nNestedField = new ObjectTypeMetadata("fNested.nNested", Arrays.asList(nnsStringField, nnIpMeta));

	private final List<FieldMetadata> nestedInnerMetadata = Arrays.asList(new StringFieldMetadata("fNested.nString", 1, 2, Arrays.asList("xy", "z")),
			nNestedField);
	private final ObjectTypeMetadata fNestedMeta = new ObjectTypeMetadata("fNested", nestedInnerMetadata);
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
		props.put("tokens.string.min_occurence", "0");
		defaultProperties = new DefaultProperties(props, ResourceUtils.asProperties("default.properties"));
	}

	@AfterClass
	public void deleteIndex() throws InterruptedException, ExecutionException {
		client.admin().indices().delete(new DeleteIndexRequest(INDEX_NAME)).get();
	}

	@Test
	public void init() throws IOException {
		StatsCollector collector = new StatsCollector(client, new CollectorProperties(defaultProperties), INDEX_NAME);
		List<IndexTypeMetadata> types = collector.collectIndex();

		assertEquals(types.size(), 1);

		IndexTypeMetadata typeMeta = types.get(0);
		assertEquals(typeMeta.getIndexName(), INDEX_NAME);
		assertEquals(typeMeta.getTypeName(), INDEX_TYPE);
		List<StringFieldMetadata> stringMetas = filterMetadata(typeMeta.getFields(), StringFieldMetadata.class);
		assertEquals(stringMetas.size(), 1);
		assertEquals(stringMetas.get(0), fStringMeta);

		List<BooleanFieldMetadata> booleanMetas = filterMetadata(typeMeta.getFields(), BooleanFieldMetadata.class);
		assertEquals(booleanMetas.size(), 1);
		assertEquals(booleanMetas.get(0), fBooleanMeta);

		List<NumericFieldMetadata> integerMetas = filterMetadata(typeMeta.getFields(), NumericFieldMetadata.class);
		assertEquals(integerMetas.size(), 2);
		assertEquals(integerMetas.get(0), fIntegerMeta);
		assertEquals(integerMetas.get(1), fLongMeta);

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
