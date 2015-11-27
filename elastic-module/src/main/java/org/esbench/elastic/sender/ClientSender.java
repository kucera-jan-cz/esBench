package org.esbench.elastic.sender;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.esbench.elastic.sender.exceptions.InsertionFailure;
import org.esbench.generator.document.simple.SimpleDocumentFactory;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;

public class ClientSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSender.class);
	static final MetricRegistry metrics = new MetricRegistry();
	final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
			.outputTo(LOGGER)
			.convertRatesTo(TimeUnit.SECONDS)
			.convertDurationsTo(TimeUnit.MILLISECONDS)
			.build();
	private final Client client;

	public ClientSender(Client client) {
		this.client = client;
	}

	public void send(IndexTypeMetadata indexType, InsertProperties properties) throws IOException {
		SimpleDocumentFactory factory = new SimpleDocumentFactory(indexType);
		String index = properties.getIndex();
		String type = properties.getType();
		for(int i = 0; i < properties.getNumOfIterations(); i++) {
			LOGGER.info("Iteration {}: Sending {} documents to /{}/{}", i, properties.getDocPerIteration(), index, type);
			try {
				execute(indexType, factory, properties);
			} catch (InterruptedException ex) {
				throw new InsertionFailure("Failed to send documents", ex);
			}
		}
	}

	private void execute(IndexTypeMetadata indexType, SimpleDocumentFactory factory, InsertProperties properties) throws InterruptedException {
		final int threads = properties.getNumOfThreads();
		ExecutorService service = Executors.newFixedThreadPool(threads);
		int from = 0;
		int perThread = properties.getDocPerIteration() / threads;
		int to = 0;
		Timer.Context insert = metrics.timer("insert-total").time();
		for(int i = 0; i < threads; i++) {
			BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkListener(metrics))
					.setBulkActions(properties.getBulkActions())
					.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
					.setConcurrentRequests(properties.getBulkThreads())
					.build();
			from = to;
			to = from + perThread;
			SenderAction action = new SenderAction(metrics, properties, factory, bulkProcessor, from, to);
			service.execute(action);
		}
		service.shutdown();
		service.awaitTermination(60, TimeUnit.MINUTES);
		insert.stop();
		reporter.report();
	}
}
