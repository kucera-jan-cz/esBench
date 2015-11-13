package org.esbench.elastic.stats;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.MetadataConstants;

public class DateStatsParser implements NumericStatsParser<DateFieldMetadata> {
	private static final String FORMAT_PROP = "format";

	@Override
	public DateFieldMetadata parse(FieldInfo info, ExtendedStats stats, int valuesPerDocument) {
		Instant from = Instant.ofEpochMilli(Math.round(stats.getMin()));
		Instant to = Instant.ofEpochMilli(Math.round(stats.getMax()));

		// @TODO - Elastic support multiple formats
		String pattern = info.getJson().path(FORMAT_PROP).asText(MetadataConstants.DEFAULT_DATE_PATTERN);
		ChronoUnit unit = calculateTimeStep(from, to, stats.getCount());
		long increment = unit.between(from, to);
		return new DateFieldMetadata(info.getFullPath(), valuesPerDocument, from, to, increment, unit, pattern);
	}

	private ChronoUnit calculateTimeStep(Instant from, Instant to, long numOfDocs) {
		for(int i = ChronoUnit.DAYS.ordinal(); i >= 0; i--) {
			ChronoUnit unit = ChronoUnit.values()[i];
			long duration = unit.between(from, to);
			if(duration > numOfDocs) {
				return unit;
			}
		}
		return ChronoUnit.HOURS;
	}
}
