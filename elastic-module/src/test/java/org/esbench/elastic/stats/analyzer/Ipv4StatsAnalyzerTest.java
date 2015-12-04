package org.esbench.elastic.stats.analyzer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.elastic.stats.FieldInfo;
import org.esbench.elastic.stats.analyzer.Ipv4StatsAnalyzer;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.utils.AddressUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Ipv4StatsAnalyzerTest {
	@DataProvider
	public Object[][] parseDataProvider() {
		Object[][] values = { { "127.0.0.0", "127.0.63.255", "127.0.0.0/18" }, { "127.0.0.255", "127.0.63.255", "127.0.0.0/18" },
				{ "127.0.0.0", "127.0.0.63", "127.0.0.0/26" }, { "127.0.0.0", "127.0.0.55", "127.0.0.0/26" } };
		return values;
	}

	@Test(dataProvider = "parseDataProvider")
	public void parse(String from, String to, String expected) {
		Ipv4StatsAnalyzer parser = new Ipv4StatsAnalyzer();
		FieldInfo info = new FieldInfo("ip", false, null);
		ExtendedStats stats = mockStats(AddressUtils.ipv4ToLong(from), AddressUtils.ipv4ToLong(to));
		IPv4FieldMetadata meta = parser.parse(info, stats, 1);
		assertEquals(meta.getCidrAddress(), expected);
	}

	private ExtendedStats mockStats(long min, long max) {
		ExtendedStats stats = mock(ExtendedStats.class);
		when(stats.getMin()).thenReturn(min * 1.0);
		when(stats.getMax()).thenReturn(max * 1.0);
		return stats;
	}
}
