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
import org.esbench.elastic.utils.BulkListener;
import org.esbench.generator.document.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;

public class DocumentSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentSender.class);
	static final MetricRegistry metrics = new MetricRegistry();
	final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
			.outputTo(LOGGER)
			.convertRatesTo(TimeUnit.SECONDS)
			.convertDurationsTo(TimeUnit.MILLISECONDS)
			.build();
	private final Client client;

	public DocumentSender(Client client) {
		this.client = client;
	}

	public void send(DocumentFactory<String> factory, InsertProperties properties) throws IOException {
		send(factory, properties, 0);
	}

	public void send(DocumentFactory<String> factory, InsertProperties properties, int from) throws IOException {
		String index = properties.getIndex();
		String type = properties.getType();
		for(int i = 0; i < properties.getNumOfIterations(); i++) {
			LOGGER.info("Iteration {}: Sending {} documents to /{}/{}", i, properties.getDocPerIteration(), index, type);
			try {
				execute(factory, properties, from);
			} catch (InterruptedException ex) {
				throw new InsertionFailure("Failed to send documents", ex);
			}
		}
	}

	private void execute(DocumentFactory<String> factory, InsertProperties properties, int from) throws InterruptedException {
		final int threads = properties.getNumOfThreads();
		ExecutorService service = Executors.newFixedThreadPool(threads);
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
