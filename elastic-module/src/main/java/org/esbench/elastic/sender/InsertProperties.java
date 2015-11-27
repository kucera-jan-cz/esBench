package org.esbench.elastic.sender;

import org.apache.commons.lang3.Validate;
import org.esbench.cmd.CommandPropsConstants;
import org.esbench.core.DefaultProperties;

/**
 * Insertion related properties.
 */
public class InsertProperties {
	// General insert definition
	static final String DOCS = "insert.docs";
	static final String THREADS = "insert.threads";
	static final String ITERATIONS = "insert.iterations";

	// Loading workload
	static final String WORKLOAD_INDEX = "workload.index";
	static final String WORKLOAD_TYPE = "workload.type";

	// Bulk configuration
	static final String BULK_ACTIONS = "insert.bulk.actions";
	static final String BULK_THREADS = "insert.bulk.threads";

	private final int numOfThreads;
	private final int numOfIterations;
	private final int docPerIteration;
	private final String workloadLocation;
	private final String index;
	private final String type;
	private final String workloadIndex;
	private final String workloadType;
	private final int bulkThreads;
	private final int bulkActions;

	public InsertProperties(DefaultProperties props) {
		Validate.notNull(props);
		index = props.getProperty(CommandPropsConstants.INDEX_OPT);
		Validate.notEmpty(index, "Index property is not defined");
		this.type = props.getProperty(CommandPropsConstants.TYPE_OPT);
		Validate.notEmpty(type, "Type property is not defined");

		numOfThreads = props.getInt(THREADS);
		numOfIterations = props.getInt(ITERATIONS);
		docPerIteration = props.getInt(DOCS);

		workloadLocation = props.getProperty(CommandPropsConstants.WORKLOAD_OPT);
		Validate.notEmpty(workloadLocation, "Workload property is not defined");
		workloadIndex = props.get(WORKLOAD_INDEX, this.index);
		workloadType = props.get(WORKLOAD_TYPE, this.type);

		bulkActions = props.getInt(BULK_ACTIONS);
		bulkThreads = props.getInt(BULK_THREADS);
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

}
