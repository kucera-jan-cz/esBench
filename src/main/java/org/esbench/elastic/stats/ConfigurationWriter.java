package org.esbench.elastic.stats;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.esbench.config.Configuration;
import org.esbench.config.ConfigurationConstants;
import org.esbench.config.json.ConfigurationParser;
import org.esbench.generator.field.meta.IndexMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationWriter.class);

	private Client client;

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

	// @TODO - decide what to do with client initialization
	private Client intializeEsClient() throws UnknownHostException {
		Settings settings = Settings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name", "esBench").build();
		Client client = TransportClient.builder()
				.settings(settings)
				.build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		return client;
	}
}
