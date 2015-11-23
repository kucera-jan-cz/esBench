package org.esbench.elastic.stats;

import java.io.IOException;
import java.io.StringWriter;

import org.elasticsearch.client.Client;
import org.esbench.generator.field.meta.IndexMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.workload.Workload;
import org.esbench.workload.WorkloadConstants;
import org.esbench.workload.json.WorkloadParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationWriter.class);

	private final Client client;

	public ConfigurationWriter(Client client) {
		this.client = client;
	}

	public void saveConfiguration() throws IOException {
		// @TODO - proper initialization
		String indexName = "types";
		StatsCollector collector = new StatsCollector(client, indexName);
		IndexMetadata indexMetadata = collector.collectMapping();

		StringWriter writer = new StringWriter();
		WorkloadParser parser = new WorkloadParser();
		Workload config = new Workload(WorkloadConstants.CURRENT_VERSION, MetadataConstants.DEFAULT_META_BY_TYPE, indexMetadata.getTypes());
		parser.parse(writer, config);

		LOGGER.info("Configuration:\n{}", writer.toString());
	}
}
