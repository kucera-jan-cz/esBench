package org.esbench.elastic.sender;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.esbench.generator.document.simple.SimpleDocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class SenderAction implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SenderAction.class);
	private final MetricRegistry metrics;
	private final InsertProperties properties;
	private final SimpleDocumentFactory factory;
	private final BulkProcessor bulkProcessor;
	private final int from;
	private final int to;

	public SenderAction(MetricRegistry metrics, InsertProperties properties, SimpleDocumentFactory factory, BulkProcessor bulkProcessor, int from, int to) {
		this.metrics = metrics;
		this.properties = properties;
		this.factory = factory;
		this.bulkProcessor = bulkProcessor;
		this.from = from;
		this.to = to;
	}

	@Override
	public void run() {
		IndexRequest indexRequest = new IndexRequest(properties.getIndex(), properties.getType());
		Timer.Context insert = metrics.timer("insert-per-thread").time();
		Timer.Context docCreation = metrics.timer("doc-creation").time();
		try {
			for(int i = from; i < to; i++) {
				String json = factory.newInstance(i);
				bulkProcessor.add(indexRequest.source(json));
			}
			docCreation.stop();
			bulkProcessor.awaitClose(60, TimeUnit.SECONDS);
			bulkProcessor.close();
		} catch (InterruptedException e) {
			LOGGER.error("Waiting for bulk processing failed, reason: ", e);
		} catch (RuntimeException ex) {
			LOGGER.error("Bulk send ailed, reason: ", ex);
		} finally {
			insert.stop();
		}
	}

}
