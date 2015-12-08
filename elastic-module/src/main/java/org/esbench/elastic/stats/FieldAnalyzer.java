package org.esbench.elastic.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
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
import org.esbench.elastic.stats.analyzer.BooleanStatsAnalyzer;
import org.esbench.elastic.stats.analyzer.DateStatsAnalyzer;
import org.esbench.elastic.stats.analyzer.ExtendedStatsAnalyzer;
import org.esbench.elastic.stats.analyzer.Ipv4StatsAnalyzer;
import org.esbench.elastic.stats.analyzer.NumericStatsAnalyzer;
import org.esbench.generator.field.FieldConstants;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

public class FieldAnalyzer {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldAnalyzer.class);
	// Search constants
	private static final int ZERO_ITEMS = 0;
	// Aggregation constants
	private static final String VALUE_COUNT_AGG = "field_stats";
	private static final String EXTENDED_STATS_AGG = "extended_stats";
	private static final String FILTER_AGG = "filter_agg";
	private static final String TERMS_AGG = "tokens";
	private static final String NESTED_AGG = "nested_";

	private final Client client;
	private final String indexName;
	private final CollectorProperties props;

	public FieldAnalyzer(Client client, String indexName, CollectorProperties props) {
		this.client = client;
		this.indexName = indexName;
		this.props = props;
	}

	public List<FieldMetadata> collectMetadata(Multimap<String, FieldInfo> fieldsByteType) {
		List<FieldMetadata> meta = new ArrayList<>();
		meta.addAll(collectStrings(fieldsByteType.get("string")));
		meta.addAll(collectNumericData(fieldsByteType.get("date"), new DateStatsAnalyzer()));
		meta.addAll(collectNumericData(fieldsByteType.get("boolean"), new BooleanStatsAnalyzer()));
		meta.addAll(collectNumericData(fieldsByteType.get("ip"), new Ipv4StatsAnalyzer()));
		meta.addAll(collectNumericData(fieldsByteType.get("integer"), new NumericStatsAnalyzer()));
		meta.addAll(collectNumericData(fieldsByteType.get("long"), new NumericStatsAnalyzer()));
		return meta;
	}

	private <T extends FieldMetadata> List<FieldMetadata> collectNumericData(Collection<FieldInfo> fields, ExtendedStatsAnalyzer<T> parser) {
		if(fields.isEmpty()) {
			return Collections.emptyList();
		}
		List<FieldMetadata> metadata = new ArrayList<>(fields.size());
		SearchRequestBuilder builder = createNumericSearchBuilder(fields);
		SearchResponse response = client.search(builder.request()).actionGet();
		for(FieldInfo info : fields) {
			InternalFilter filter = getAggregation(response, info, FILTER_AGG);
			ExtendedStats stats = (ExtendedStats) filter.getAggregations().get(EXTENDED_STATS_AGG + info.getFullPath());
			LOGGER.debug("Field {} total: {} MAX: {} MIN: {}", info.getFullPath(), stats.getCount(), stats.getMaxAsString(), stats.getMinAsString());
			if(stats.getCount() <= ZERO_ITEMS) {
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
		if(fields.isEmpty()) {
			return Collections.emptyList();
		}
		SearchRequestBuilder builder = createStringSearchBuilder(fields);
		List<StringFieldMetadata> metadata = new ArrayList<>(fields.size());
		SearchResponse response = client.search(builder.request()).actionGet();
		for(FieldInfo info : fields) {
			Terms tokenAgg = getAggregation(response, info, TERMS_AGG);
			InternalFilter filter = getAggregation(response, info, FILTER_AGG);

			ValueCount fieldTotalAgg = (ValueCount) filter.getAggregations().get(VALUE_COUNT_AGG + info.getFullPath());
			LOGGER.debug("Field {} total: {}", info.getFullPath(), fieldTotalAgg.getValue());
			for(Terms.Bucket bucket : tokenAgg.getBuckets()) {
				LOGGER.debug("\t{}:{}", bucket.getKey(), bucket.getDocCount());
			}
			if(fieldTotalAgg.getValue() <= ZERO_ITEMS) {
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
		SearchRequestBuilder builder = client.prepareSearch(indexName);
		for(FieldInfo info : fields) {
			TermsBuilder tokenBuilder = AggregationBuilders.terms(TERMS_AGG + info.getFullPath())
					.field(info.getFullPath())
					.minDocCount(props.getStringTokenMinOccurent())
					.size(props.getStringTokenLimit());
			builder.addAggregation(createNestedIfNecessary(info, tokenBuilder));

			ValueCountBuilder countBuilder = AggregationBuilders.count(VALUE_COUNT_AGG + info.getFullPath()).field(info.getFullPath());
			QueryBuilder filter = new BoolQueryBuilder().filter(new ExistsQueryBuilder(info.getFullPath()));
			FilterAggregationBuilder filterBuilder = AggregationBuilders.filter(FILTER_AGG + info.getFullPath()).filter(filter).subAggregation(countBuilder);
			builder.addAggregation(createNestedIfNecessary(info, filterBuilder));
		}
		builder.setSize(ZERO_ITEMS);
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
		SearchRequestBuilder builder = client.prepareSearch(indexName);
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
