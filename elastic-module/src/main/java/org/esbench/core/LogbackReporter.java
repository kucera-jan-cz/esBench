package org.esbench.core;

import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

/**
 * Formats results from MetricRegistry.
 * Class is based on {@link Slf4jReporter} and currently reports only Timer metrics with simplified output.
 */
public class LogbackReporter extends ScheduledReporter {
	private final Logger logger;
	private int paddingSize = 24;
	private final DecimalFormat formatter = new DecimalFormat("##0.0000");

	/**
	 * Register reported with given registry using default logger.
	 * @param registry which will be used for generating report.
	 */
	public LogbackReporter(MetricRegistry registry) {
		this(registry, LoggerFactory.getLogger(LogbackReporter.class));
	}

	/**
	 * Register reported with given registry using given logger.
	 * @param registry which will be used for generating report.
	 */
	public LogbackReporter(MetricRegistry registry, Logger logger) {
		super(registry, "logger-reporter", MetricFilter.ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS);
		this.logger = Validate.notNull(logger);
	}

	@Override
	public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms,
			SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {

		for(Entry<String, Timer> entry : timers.entrySet()) {
			logTimer(entry.getKey(), entry.getValue());
		}
	}

	private void logTimer(String name, Timer timer) {
		final Snapshot snap = timer.getSnapshot();
		String paddedName = StringUtils.rightPad(name, paddingSize);
		logger.info("name={}: count={}, min={}, max={}, mean={}", paddedName, timer.getCount(), toLog(snap.getMin()), toLog(snap.getMax()),
				toLog(snap.getMean()));
	}

	private String toLog(double value) {
		double convertered = convertDuration(value);
		return formatter.format(convertered);
	}

	public int getPaddingSize() {
		return paddingSize;
	}

	/**
	 * Change padding size for padding name of calculated timer.
	 * @param paddingSize how much padding will be applied
	 */
	public void setPaddingSize(int paddingSize) {
		Validate.inclusiveBetween(1, Integer.MAX_VALUE, paddingSize);
		this.paddingSize = paddingSize;
	}
}
