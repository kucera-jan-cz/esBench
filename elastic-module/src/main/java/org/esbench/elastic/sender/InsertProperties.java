package org.esbench.elastic.sender;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esbench.cmd.CommandPropsConstants;
import org.esbench.core.DefaultProperties;

/**
 * Insertion related properties.
 */
public class InsertProperties {
	// General insert definition
	public static final String DOCS = "insert.docs";
	public static final String THREADS = "insert.threads";
	public static final String ITERATIONS = "insert.iterations";
	public static final String CACHE_LIMIT = "insert.cache.value_limit";

	// Loading workload
	public static final String WORKLOAD_INDEX = "workload.index";
	public static final String WORKLOAD_TYPE = "workload.type";

	// Bulk configuration
	public static final String BULK_ACTIONS = "insert.bulk.actions";
	public static final String BULK_THREADS = "insert.bulk.threads";

	public static final String CLUSTER_NODES = "insert.cluster.nodes";

	private final int numOfThreads;
	private final int numOfIterations;
	private final int docPerIteration;
	private final int fieldCacheLimit;
	private final String workloadLocation;
	private final String index;
	private final String type;
	private final String workloadIndex;
	private final String workloadType;
	private final int bulkThreads;
	private final int bulkActions;
	private final int clusterNodes;

	public InsertProperties(DefaultProperties props) {
		Validate.notNull(props);
		index = props.getProperty(CommandPropsConstants.INDEX_OPT);
		Validate.notEmpty(index, "Index property is not defined");
		this.type = props.getProperty(CommandPropsConstants.TYPE_OPT);
		Validate.notEmpty(type, "Type property is not defined");

		numOfThreads = props.getInt(THREADS);
		numOfIterations = props.getInt(ITERATIONS);
		docPerIteration = props.getInt(DOCS);
		fieldCacheLimit = props.getInt(CACHE_LIMIT);

		workloadLocation = props.getProperty(CommandPropsConstants.WORKLOAD_OPT);
		Validate.notEmpty(workloadLocation, "Workload property is not defined");
		workloadIndex = props.get(WORKLOAD_INDEX, this.index);
		workloadType = props.get(WORKLOAD_TYPE, this.type);

		bulkActions = props.getInt(BULK_ACTIONS);
		bulkThreads = props.getInt(BULK_THREADS);

		clusterNodes = props.getInt(CLUSTER_NODES);
	}

	public int getNumOfThreads() {
		return numOfThreads;
	}

	public int getNumOfIterations() {
		return numOfIterations;
	}

	public int getDocPerIteration() {
		return docPerIteration;
	}

	public String getWorkloadLocation() {
		return workloadLocation;
	}

	public int getFieldCacheLimit() {
		return fieldCacheLimit;
	}

	public String getWorkloadIndex() {
		return workloadIndex;
	}

	public String getWorkloadType() {
		return workloadType;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public int getBulkThreads() {
		return bulkThreads;
	}

	public int getBulkActions() {
		return bulkActions;
	}

	public int getClusterNodes() {
		return clusterNodes;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
