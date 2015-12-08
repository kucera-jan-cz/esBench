package org.esbench.elastic.stats.analyzer;

import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.esbench.elastic.stats.FieldInfo;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.utils.AddressUtils;

public class Ipv4StatsAnalyzer implements ExtendedStatsAnalyzer<IPv4FieldMetadata> {

	@Override
	public IPv4FieldMetadata parse(FieldInfo info, ExtendedStats stats, int valuesPerDocument) {
		String cidr = AddressUtils.toCIDR(Math.round(stats.getMin()), Math.round(stats.getMax()));
		return new IPv4FieldMetadata(info.getFullPath(), valuesPerDocument, cidr);
	}
}
