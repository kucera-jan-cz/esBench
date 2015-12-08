package org.esbench.elastic.stats.analyzer;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.elasticsearch.common.joda.FormatDateTimeFormatter;
import org.elasticsearch.common.joda.Joda;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.elastic.stats.FieldInfo;
import org.esbench.generator.field.FieldConstants;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class DateStatsAnalyzerTest {
	private final DateStatsAnalyzer parser = new DateStatsAnalyzer();

	@DataProvider
	public Object[][] parsePatternDataProvider() {
		List<String> formats = DateStatsAnalyzerConstants.SUPPORTED_FORMATS;
		Object[][] values = new Object[formats.size()][1];
		for(int i = 0; i < formats.size(); i++) {
			values[i] = new Object[] { formats.get(i) };
		}
		return values;
	}

	@Test(dataProvider = "parsePatternDataProvider")
	public void parsePattern(String pattern) {

		Instant max = Instant.ofEpochMilli(RandomUtils.nextLong(Instant.EPOCH.getEpochSecond(), Instant.now().getEpochSecond()));
		Instant min = max.minusMillis(RandomUtils.nextLong(1, 10_000_000));
		ExtendedStats stats = mockStats(min, max);

		FieldInfo info = mockInfo(pattern);
		DateFieldMetadata meta = (DateFieldMetadata) parser.parse(info, stats, FieldConstants.SINGLE_VALUE);
		String dateAsText = meta.getFormatter().format(meta.getFrom());
		FormatDateTimeFormatter formatter = Joda.forPattern(pattern);
		String jodaDateAsText = formatter.printer().print(min.toEpochMilli());
		assertEquals(dateAsText, jodaDateAsText, "JDK: " + dateAsText + " JODA: " + jodaDateAsText);
	}

	@DataProvider
	public Object[][] parseNumericDataProvider() {
		Object[][] values = { { Instant.now().minus(60, SECONDS), Instant.now(), 2, 1L }, { Instant.now().minus(60, SECONDS), Instant.now(), 150, 1L },
				{ Instant.now().minus(5, SECONDS), Instant.now(), 2, 1L }, { Instant.now().minus(5, HOURS), Instant.now(), 2, 60 * 60 * 1L },
				{ Instant.now().minus(5, HOURS), Instant.now(), 5_000, 1L }, };
		return values;
	}

	@Test(dataProvider = "parseNumericDataProvider")
	public void parseNumericSeconds(Instant from, Instant to, long numOfDocs, long expectedStep) {
		ExtendedStats stats = mockStats(from, to, numOfDocs);
		FieldInfo info = mockInfo("epoch_second");
		NumericFieldMetadata meta = (NumericFieldMetadata) parser.parse(info, stats, FieldConstants.SINGLE_VALUE);
		assertEquals(meta.getStep(), expectedStep);
	}

	@DataProvider
	public Object[][] parseNumericMilisDataProvider() {
		Object[][] values = { { Instant.now().minus(60, SECONDS), Instant.now(), 2, 1000L }, { Instant.now().minus(60, SECONDS), Instant.now(), 150, 1L },
				{ Instant.now().minus(5, SECONDS), Instant.now(), 2, 1000L }, { Instant.now().minus(5, HOURS), Instant.now(), 2, 60 * 60 * 1000L },
				{ Instant.now().minus(5, HOURS), Instant.now(), 5_000, 1000L }, };
		return values;
	}

	@Test(dataProvider = "parseNumericMilisDataProvider")
	public void parseNumericMilis(Instant from, Instant to, long numOfDocs, long expectedStepInMilis) {
		ExtendedStats stats = mockStats(from, to, numOfDocs);
		FieldInfo info = mockInfo("epoch_millis");
		NumericFieldMetadata meta = (NumericFieldMetadata) parser.parse(info, stats, FieldConstants.SINGLE_VALUE);
		assertEquals(meta.getStep(), expectedStepInMilis);
	}

	@Test(dataProvider = "calculateTimeStepDataProvider")
	public void calculateTimeStep(Instant from, Instant to, long numOfDocs, ChronoUnit expectedUnit) {
		ChronoUnit unit = parser.calculateTimeStep(from, to, numOfDocs);
		assertEquals(unit, expectedUnit);
	}

	@DataProvider
	public Object[][] calculateTimeStepDataProvider() {
		Object[][] values = { { Instant.now().minus(24, HOURS), Instant.now(), 2, HOURS }, { Instant.now().minus(24, HOURS), Instant.now(), 50_000, SECONDS },
				{ Instant.now().minus(24, HOURS), Instant.now(), 100_000, MILLIS }, { Instant.now().minus(60, SECONDS), Instant.now(), 2, SECONDS } };
		return values;
	}

	private ExtendedStats mockStats(Instant min, Instant max) {
		return mockStats(min, max, 2L);
	}

	private ExtendedStats mockStats(Instant min, Instant max, long count) {
		ExtendedStats stats = mock(ExtendedStats.class);
		when(stats.getMin()).thenReturn(min.toEpochMilli() * 1.0);
		when(stats.getMax()).thenReturn(max.toEpochMilli() * 1.0);
		when(stats.getCount()).thenReturn(count);
		return stats;
	}

	private FieldInfo mockInfo(String pattern) {
		JsonNode mockedFormat = mock(JsonNode.class);
		JsonNode mockedRoot = mock(JsonNode.class);
		when(mockedRoot.path(eq("format"))).thenReturn(mockedFormat);
		when(mockedFormat.asText(anyString())).thenReturn(pattern);
		FieldInfo info = new FieldInfo("date", false, mockedRoot);
		return info;
	}

}
