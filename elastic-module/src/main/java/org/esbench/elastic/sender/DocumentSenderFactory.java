package org.esbench.elastic.sender;

import org.elasticsearch.client.Client;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.utils.ElasticClientBuilder;

public class DocumentSenderFactory {
	public DocumentSender newInstance(DefaultProperties properties) {
		Client client = new ElasticClientBuilder().withProperties(properties).build();
		DocumentSender sender = new DocumentSenderImpl(client);
		return sender;
	}
}
