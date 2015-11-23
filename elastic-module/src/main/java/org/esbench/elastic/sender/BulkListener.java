package org.esbench.elastic.sender;

import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class BulkListener implements Listener {
	private static final Logger LOGGER = LoggerFactory.getLogger(BulkListener.class);
	private Timer timer;
	private Timer.Context insert;

	public BulkListener(MetricRegistry metrics) {
		this.timer = metrics.timer("insert");
	}

	@Override
	public void beforeBulk(long executionId, BulkRequest request) {
		insert = timer.time();
		LOGGER.debug("Bulk initialized");
	}

	@Override
	public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
		insert.stop();
		if(response.hasFailures()) {
			LOGGER.warn("Bulk finished. Failures: {}", response.hasFailures());
			LOGGER.warn("MSG: {}", response.buildFailureMessage());
		} else {
			LOGGER.debug("Bulk finished. Took: {} ms", response.getTookInMillis());
		}
	}

	@Override
	public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
		insert.stop();
		LOGGER.warn("Bulk failed with exception: ", failure);
	}

}
