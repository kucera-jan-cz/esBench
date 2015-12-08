package org.esbench.elastic.spring;

import org.elasticsearch.client.Client;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.stats.CollectWorkloadAction;
import org.esbench.elastic.stats.CollectorProperties;
import org.esbench.elastic.stats.StatsCollector;
import org.esbench.elastic.utils.ElasticClientBuilder;
import org.esbench.workload.json.WorkloadParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectSpringConfiguration {
	@Autowired
	private DefaultProperties defaults;

	@Bean
	public CollectorProperties collectorProperties() {
		return new CollectorProperties(defaults);
	}

	@Bean
	public Client esClient() {
		return new ElasticClientBuilder().withProperties(defaults).build();
	}

	@Bean
	public WorkloadParser workloadParser() {
		return new WorkloadParser();
	}

	@Bean
	public StatsCollector collector(Client client, CollectorProperties properties) {
		return new StatsCollector(client, properties);
	}

	@Bean
	public CollectWorkloadAction collectAction(StatsCollector collector, WorkloadParser parser) {
		return new CollectWorkloadAction(collector, parser);
	}
}
