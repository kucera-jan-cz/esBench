package org.esbench.elastic.spring;

import org.elasticsearch.client.Client;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.utils.ElasticClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchSpringConfiguration {
	@Autowired
	private DefaultProperties properties;

	@Bean
	public Client elasticSearchClient() {
		Client client = new ElasticClientBuilder().withProperties(properties).build();
		return client;
	}
}
