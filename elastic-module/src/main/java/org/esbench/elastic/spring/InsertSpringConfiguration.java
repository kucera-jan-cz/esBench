package org.esbench.elastic.spring;

import org.elasticsearch.client.Client;
import org.esbench.elastic.sender.DocumentSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = ElasticSearchSpringConfiguration.class)
public class InsertSpringConfiguration {

	@Bean
	public DocumentSender documentSender(Client client) {
		DocumentSender sender = new DocumentSender(client);
		return sender;
	}
}
