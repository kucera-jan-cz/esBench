package org.esbench.elastic.stats;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.NumericFieldMetadata;

/**
 * Converts Elasticsearch ExtendedStats to NumericFieldMetadata by extracting lowest and highest value. 
 */
public class NumericStatsParser implements ExtendedStatsParser<NumericFieldMetadata> {

	@Override
	public NumericFieldMetadata parse(FieldInfo info, ExtendedStats stats, int valuesPerDocument) {
		String typeAsText = info.getJson().path("type").asText();
		MetaType type = MetaType.valueOf(typeAsText.toUpperCase());

		switch(type) {
		case INTEGER:
			int intFrom = Math.toIntExact(Math.round(stats.getMin()));
			int intTo = Math.toIntExact(Math.round(stats.getMax()));
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, intFrom, intTo, 1);
		case LONG:
			long longFrom = Math.round(stats.getMin());
			long longTo = Math.round(stats.getMax());
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, longFrom, longTo, 1L);
		case DOUBLE:
			double doubleFrom = stats.getMin();
			double doubleTo = stats.getMax();
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, doubleFrom, doubleTo, 1L);
		case SHORT:
			short shortFrom = (short) Math.round(stats.getMin());
			short shortTo = (short) Math.round(stats.getMax());
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, shortFrom, shortTo, 1L);
		case BYTE:
			byte byteFrom = (byte) Math.round(stats.getMin());
			byte byteTo = (byte) Math.round(stats.getMax());
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, byteFrom, byteTo, 1L);
		case FLOAT:
			float floatFrom = (float) stats.getMin();
			float floatTo = (float) stats.getMax();
			return new NumericFieldMetadata(info.getFullPath(), valuesPerDocument, type, floatFrom, floatTo, 1L);
		default:
			throw new IllegalArgumentException("Unknown type: " + type);
		}
	}

}
