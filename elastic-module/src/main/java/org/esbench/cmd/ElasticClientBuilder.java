package org.esbench.cmd;

import static org.esbench.cmd.CommandPropsConstants.CLUSTER_OPT;
import static org.esbench.cmd.CommandPropsConstants.HOST_OPT;
import static org.esbench.cmd.CommandPropsConstants.PORT_OPT;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.esbench.core.DefaultProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticClientBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientBuilder.class);
	private String clusterName = "elasticsearch";
	private String host = "localhost";
	private int port = 9300;

	public Client build() {
		TransportAddress address;
		try {
			address = new InetSocketTransportAddress(InetAddress.getByName(host), port);
		} catch (UnknownHostException e) {
			LOGGER.error("Can't resolve address {} and port {}", host, port);
			throw new IllegalArgumentException("Can't resolve address", e);
		}
		Settings settings = Settings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name", clusterName).build();

		Client client = TransportClient.builder().settings(settings).build().addTransportAddress(address);
		return client;
	}

	public Client buildNode() {
		Settings settings = Settings.settingsBuilder().put("http.enabled", false).build();
		Node node = new NodeBuilder().clusterName(clusterName).settings(settings).client(true).node();

		Client client = node.client();
		return client;
	}

	public ElasticClientBuilder withProperties(DefaultProperties props) {
		host = props.get(HOST_OPT, "localhost");
		port = props.get(PORT_OPT, 9300);
		clusterName = props.get(CLUSTER_OPT, "elasticsearch");
		return this;
	}

}
