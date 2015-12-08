package org.esbench.elastic.stats.analyzer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.elastic.stats.FieldInfo;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class NumericStatsAnalyzerTest {
	@DataProvider
	public Object[][] parseDataProvider() {
		Object[][] values = { { new Double("0"), new Double("10"), "integer" }, { new Double("2147483647"), new Double("2147483648"), "long" },
				{ new Double("0"), Double.MAX_VALUE - 1, "double" }, { new Double("-32768"), new Double("32767"), "short" },
				{ new Double("-128"), new Double("127"), "byte" } };
		return values;
	}

	@Test(dataProvider = "parseDataProvider")
	public void parse(Double from, Double to, String typeAsText) {
		NumericStatsAnalyzer parser = new NumericStatsAnalyzer();
		ExtendedStats stats = mockStats(from, to);
		FieldInfo info = mockInfo(typeAsText);
		NumericFieldMetadata meta = parser.parse(info, stats, 1);
		assertEquals(meta.getMetaType(), MetaType.valueOf(typeAsText.toUpperCase()));
		assertEquals(meta.getFrom().doubleValue(), from);
		assertEquals(meta.getTo().doubleValue(), to);
	}

	public void parseFloat() {
		Double from = new Double("0.00001");
		Double to = new Double("1.111");
		NumericStatsAnalyzer parser = new NumericStatsAnalyzer();
		ExtendedStats stats = mockStats(from, to);
		FieldInfo info = mockInfo("float");
		NumericFieldMetadata meta = parser.parse(info, stats, 1);
		assertEquals(meta.getMetaType(), MetaType.FLOAT);
		assertEquals(meta.getFrom().floatValue(), from);
		assertEquals(meta.getTo().floatValue(), to);
	}

	private ExtendedStats mockStats(double min, double max) {
		ExtendedStats stats = mock(ExtendedStats.class);
		when(stats.getMin()).thenReturn(min);
		when(stats.getMax()).thenReturn(max);
		return stats;
	}

	private FieldInfo mockInfo(String typeAsText) {
		JsonNode mockedFormat = mock(JsonNode.class);
		JsonNode mockedRoot = mock(JsonNode.class);
		when(mockedRoot.path(eq("type"))).thenReturn(mockedFormat);
		when(mockedFormat.asText()).thenReturn(typeAsText);
		FieldInfo info = new FieldInfo("numeric", false, mockedRoot);
		return info;
	}
}
