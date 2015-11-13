package org.esbench.elastic.stats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsAction;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;
import org.esbench.generator.field.FieldConstants;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class StatsCollector {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatsCollector.class);
	// Type constants
	private static final String NESTED_TYPE = "nested";
	private static final String FIELD_SEPARATOR = ".";
	// Mapping constants
	private static final String TYPE_PROP = "type";
	private static final String PROPERTIES_PROP = "properties";
	private static final String INDEX_NAME = "types";
	// Search constants
	private static final int ZERO_ITEMS = 0;
	private static final int MAX_ITEMS_SIZE = 0;
	// Aggregation constants
	private static final String VALUE_COUNT_AGG = "field_stats";
	private static final String EXTENDED_STATS_AGG = "extended_stats";
	private static final String FILTER_AGG = "filter_agg";
	private static final String TERMS_AGG = "tokens";
	private static final String NESTED_AGG = "nested_";

	private Client client;

	public StatsCollector(Client client) {
		this.client = client;
	}

	public IndexMetadata collectMapping() throws IOException {
		GetMappingsResponse response = new GetMappingsRequestBuilder(client, GetMappingsAction.INSTANCE, INDEX_NAME).get();
		ImmutableOpenMap<String, MappingMetaData> mapping = response.getMappings().get(INDEX_NAME);
		String[] indexTypes = mapping.keys().toArray(String.class);
		ObjectMapper mapper = new ObjectMapper();
		List<IndexTypeMetadata> typesMetadata = new ArrayList<>();
		for(String indexType : indexTypes) {
			MappingMetaData meta = mapping.get(indexType);
			LOGGER.info("Index: {} Type: {}", INDEX_NAME, indexType);
			String mappingsAsJson = meta.source().string();
			LOGGER.info("JSON:\n{}", mappingsAsJson);

			JsonNode root = mapper.readValue(mappingsAsJson, JsonNode.class);
			JsonNode typeProp = root.path(indexType).path(PROPERTIES_PROP);
			ObjectTypeMetadata typeMeta = parseConfiguration(indexType, typeProp, StringUtils.EMPTY, false);
			typesMetadata.add(new IndexTypeMetadata(INDEX_NAME, indexType, typeMeta.getInnerMetadata()));
		}
		IndexMetadata indexMeta = new IndexMetadata(INDEX_NAME, typesMetadata);
		return indexMeta;
	}

	private ObjectTypeMetadata parseConfiguration(String parentName, JsonNode typeProp, String parentFullPath, boolean nested) {
		Validate.isTrue(!typeProp.isMissingNode(), "Parsing of mapping failed to look 'properties'");
		List<FieldMetadata> innerMetadata = new ArrayList<>();
		Multimap<String, FieldInfo> fieldsByteType = ArrayListMultimap.create();
		Iterator<String> it = typeProp.fieldNames();
		while(it.hasNext()) {
			String name = it.next();
			String fullFieldName = parentFullPath + FIELD_SEPARATOR + name;
			fullFieldName = StringUtils.removeStart(fullFieldName, FIELD_SEPARATOR);
			JsonNode fieldJson = typeProp.path(name);
			JsonNode fieldTypeJson = fieldJson.path(TYPE_PROP);
			String fieldType = fieldTypeJson.textValue();
			FieldInfo info = new FieldInfo(fullFieldName, nested, typeProp);

			if(fieldTypeJson.isMissingNode() || NESTED_TYPE.equals(fieldType)) {
				boolean fieldNested = nested || NESTED_TYPE.equals(fieldType);
				ObjectTypeMetadata objectFields = parseConfiguration(name, fieldJson.path(PROPERTIES_PROP), fullFieldName, fieldNested);
				innerMetadata.add(objectFields);
			} else {
				fieldsByteType.put(fieldType, info);
			}

		}
		innerMetadata.addAll(collectMetadata(fieldsByteType));
		return new ObjectTypeMetadata(parentFullPath, innerMetadata);
	}

	private List<FieldMetadata> collectMetadata(Multimap<String, FieldInfo> fieldsByteType) {
		List<FieldMetadata> meta = new ArrayList<>();
		meta.addAll(collectStrings(fieldsByteType.get("string")));
		meta.addAll(collectNumericData(fieldsByteType.get("date"), new DateStatsParser()));
		meta.addAll(collectNumericData(fieldsByteType.get("boolean"), new BooleanStatsParser()));
		meta.addAll(collectNumericData(fieldsByteType.get("ip"), new Ipv4StatsParser()));
		return meta;
	}

	private <T extends FieldMetadata> List<FieldMetadata> collectNumericData(Collection<FieldInfo> fields, NumericStatsParser<T> parser) {
		List<FieldMetadata> metadata = new ArrayList<>(fields.size());
		SearchRequestBuilder builder = createNumericSearchBuilder(fields);
		SearchResponse response = client.search(builder.request()).actionGet();
		for(FieldInfo info : fields) {
			// InternalFilter filter = response.getAggregations().get(FILTER_AGG + info.getFullPath());
			InternalFilter filter = getAggregation(response, info, FILTER_AGG);
			ExtendedStats stats = (ExtendedStats) filter.getAggregations().get(EXTENDED_STATS_AGG + info.getFullPath());
			LOGGER.debug("Field {} total: {} MAX: {} MIN: {}", info.getFullPath(), stats.getCount(), stats.getMaxAsString(), stats.getMinAsString());
			if(stats.getCount() < 1) {
				continue;
			}
			int valuesPerDocument = valuesPerDoc(response, filter, stats);
			FieldMetadata meta = parser.parse(info, stats, valuesPerDocument);
			LOGGER.debug("Registering metadata: {}", meta);
			metadata.add(meta);
		}
		return metadata;
	}

	private List<StringFieldMetadata> collectStrings(Collection<FieldInfo> fields) {
		SearchRequestBuilder builder = createStringSearchBuilder(fields);
		List<StringFieldMetadata> metadata = new ArrayList<>(fields.size());
		SearchResponse response = client.search(builder.request()).actionGet();
		for(FieldInfo info : fields) {
			Terms tokenAgg = getAggregation(response, info, TERMS_AGG);
			InternalFilter filter = getAggregation(response, info, FILTER_AGG);

			ValueCount fieldTotalAgg = (ValueCount) filter.getAggregations().get((VALUE_COUNT_AGG + info.getFullPath()));
			LOGGER.debug("Field {} total: {}", info.getFullPath(), fieldTotalAgg.getValue());
			for(Terms.Bucket bucket : tokenAgg.getBuckets()) {
				LOGGER.debug("\t{}:{}", bucket.getKey(), bucket.getDocCount());
			}
			if(fieldTotalAgg.getValue() < 1) {
				continue;
			}
			long totalHits = filter.getDocCount();
			int tokenCount = (int) (fieldTotalAgg.getValue() / totalHits);
			List<String> tokens = tokenAgg.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
			StringFieldMetadata meta = new StringFieldMetadata(info.getFullPath(), FieldConstants.SINGLE_VALUE, tokenCount, tokens);
			LOGGER.debug("Registering metadata: {}", meta);
			metadata.add(meta);
		}
		return metadata;
	}

	private SearchRequestBuilder createStringSearchBuilder(Collection<FieldInfo> fields) {
		SearchRequestBuilder builder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
		for(FieldInfo info : fields) {
			TermsBuilder tokenBuilder = AggregationBuilders.terms(TERMS_AGG + info.getFullPath()).field(info.getFullPath()).size(MAX_ITEMS_SIZE);
			builder.addAggregation(createNestedIfNecessary(info, tokenBuilder));

			ValueCountBuilder countBuilder = AggregationBuilders.count(VALUE_COUNT_AGG + info.getFullPath()).field(info.getFullPath());
			QueryBuilder filter = new BoolQueryBuilder().filter(new ExistsQueryBuilder(info.getFullPath()));
			FilterAggregationBuilder filterBuilder = AggregationBuilders.filter(FILTER_AGG + info.getFullPath()).filter(filter).subAggregation(countBuilder);
			builder.addAggregation(createNestedIfNecessary(info, filterBuilder));
		}
		builder.setIndices(INDEX_NAME).setSize(ZERO_ITEMS);
		return builder;
	}

	private AbstractAggregationBuilder createNestedIfNecessary(FieldInfo info, AbstractAggregationBuilder builder) {
		if(info.isNested()) {
			LOGGER.debug("Creating nested aggration for field {}", info.getFullPath());
			NestedBuilder nestedBuilder = AggregationBuilders.nested(NESTED_AGG + builder.getName()).path(info.getParent()).subAggregation(builder);
			return nestedBuilder;
		} else {
			return builder;
		}
	}

	private <A extends Aggregation> A getAggregation(SearchResponse response, FieldInfo info, String prefixName) {
		String aggregationName = prefixName + info.getFullPath();
		if(info.isNested()) {
			InternalNested nested = response.getAggregations().get(NESTED_AGG + aggregationName);
			return nested.getAggregations().get(aggregationName);
		} else {
			return response.getAggregations().get(aggregationName);
		}
	}

	private SearchRequestBuilder createNumericSearchBuilder(Collection<FieldInfo> fields) {
		SearchRequestBuilder builder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
		for(FieldInfo info : fields) {
			ExtendedStatsBuilder stats = AggregationBuilders.extendedStats(EXTENDED_STATS_AGG + info.getFullPath()).field(info.getFullPath());
			QueryBuilder filter = new BoolQueryBuilder().filter(new ExistsQueryBuilder(info.getFullPath()));
			FilterAggregationBuilder aggregation = AggregationBuilders.filter(FILTER_AGG + info.getFullPath()).filter(filter).subAggregation(stats);
			builder.addAggregation(createNestedIfNecessary(info, aggregation));
		}
		builder.setSize(ZERO_ITEMS);
		return builder;
	}

	private int valuesPerDoc(SearchResponse response, InternalFilter filter, ExtendedStats stats) {
		return valuesPerDoc(response, filter, stats.getCount());
	}

	private int valuesPerDoc(SearchResponse response, InternalFilter filter, long valuesInDocs) {
		long allDocs = response.getHits().getTotalHits();
		long docFieldPresented = filter.getDocCount();
		int valuesPerDocument = Math.toIntExact(valuesInDocs / docFieldPresented);
		valuesPerDocument = Math.max(valuesPerDocument, FieldConstants.SINGLE_VALUE);
		LOGGER.debug("Total docs: {}, with field: {}, values: {}", allDocs, docFieldPresented, valuesInDocs);
		return valuesPerDocument;
	}
}
