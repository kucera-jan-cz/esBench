package org.esbench.elastic.stats;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.generator.field.meta.FieldMetadata;

public interface NumericStatsParser<T extends FieldMetadata> {

	public T parse(FieldInfo info, ExtendedStats stats, int valuesPerDocument);
}
