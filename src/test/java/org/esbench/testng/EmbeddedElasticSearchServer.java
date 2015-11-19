package org.esbench.testng;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedElasticSearchServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedElasticSearchServer.class);
	private static final String NODE_NAME_PROP = "node.name";
	private static final String CLUSTER_NAME_PROP = "cluster.name";
	private static final String INDEX_GATEWAY_TYPE_PROP = "index.gateway.type";
	private static final String PATH_HOME_PROP = "path.home";
	private static final String PATH_DATA_PROP = "path.data";
	private static final String HTTP_ENABLED_PROP = "http.enabled";

	private static final String DEFAULT_DATA_DIR = "target/es-data";
	private static final String DEFAULT_CLUSTER = "cluster";

	private final Node node;

	public EmbeddedElasticSearchServer() {
		this(DEFAULT_DATA_DIR, DEFAULT_CLUSTER);
	}

	public EmbeddedElasticSearchServer(String dataDirectory, String cluster) {
		Path parentDataDir = Paths.get(dataDirectory);
		try {
			Files.createDirectories(parentDataDir);
			Path dataDir = Files.createTempDirectory(parentDataDir, cluster);
			Settings settings = Settings.settingsBuilder()
					.put(PATH_HOME_PROP, "target")
					.put(HTTP_ENABLED_PROP, "false")
					.put(INDEX_GATEWAY_TYPE_PROP, "none")
					.put(PATH_DATA_PROP, dataDir.toString())
					.put(CLUSTER_NAME_PROP, cluster)
					.build();
			node = nodeBuilder().local(true).settings(settings).node();
		} catch (IOException e) {
			LOGGER.error("Failed to create temporary directory", e);
			throw new IllegalStateException("Can't initialize cluster", e);
		}

	}

	public Client getClient() {
		// node.client().admin().cluster().prepareHealth().setWaitForGreenStatus().get();
		return node.client();
	}

	public void shutdown() {
		node.close();
	}
}
