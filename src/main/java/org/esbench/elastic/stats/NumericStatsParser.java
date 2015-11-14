package org.esbench.elastic.stats;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.generator.field.meta.NumericFieldMetadata;

public class NumericStatsParser implements ExtendedStatsParser<NumericFieldMetadata> {

	@Override
	public NumericFieldMetadata parse(FieldInfo info, ExtendedStats stats, int valuesPerDocument) {
		String typeAsText = info.getJson().path("type").asText();
		NumericFieldMetadata.Type type = NumericFieldMetadata.Type.valueOf(typeAsText.toUpperCase());

		switch(type) {
		case INTEGER:
			int intFrom = Math.toIntExact(Math.round(stats.getMin()));
			int intTo = Math.toIntExact(Math.round(stats.getMax()));
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, intFrom, intTo, 1);
		case LONG:
			long longFrom = Math.round(stats.getMin());
			long longTo = Math.round(stats.getMax());
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, longFrom, longTo, 1L);
		default:
			throw new IllegalArgumentException("Unknown type: " + type);
		}
	}

}
