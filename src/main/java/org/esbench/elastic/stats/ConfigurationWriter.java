package org.esbench.elastic.stats;

import java.io.IOException;
import java.io.StringWriter;

import org.elasticsearch.client.Client;
import org.esbench.config.Configuration;
import org.esbench.config.ConfigurationConstants;
import org.esbench.config.json.ConfigurationParser;
import org.esbench.generator.field.meta.IndexMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
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
		ConfigurationParser parser = new ConfigurationParser();
		Configuration config = new Configuration(ConfigurationConstants.CURRENT_VERSION, MetadataConstants.DEFAULT_META_BY_TYPE, indexMetadata.getTypes());
		parser.parse(writer, config);

		LOGGER.info("Configuration:\n{}", writer.toString());
	}
}
