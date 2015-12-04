package org.esbench.elastic.stats.analyzer;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.elastic.stats.FieldInfo;
import org.esbench.generator.field.meta.FieldMetadata;

public interface ExtendedStatsAnalyzer<T extends FieldMetadata> {

	public T parse(FieldInfo info, ExtendedStats stats, int valuesPerDocument);
}
