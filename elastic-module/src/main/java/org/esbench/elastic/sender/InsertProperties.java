package org.esbench.elastic.sender;

import org.esbench.cmd.CommandPropsConstants;
import org.esbench.core.DefaultProperties;

public class InsertProperties {
	private static final String THREADS = "insert.threads";
	private static final String ITERATIONS = "insert.iterations";
	private static final String DOCS = "insert.docs";
	private static final String WORKLOAD_INDEX = "workload.index";
	private static final String WORKLOAD_TYPE = "workload.type";

	private final int numOfThreads;
	private final int numOfIterations;
	private final int docPerIteration;
	private final String workloadLocation;
	private final String index;
	private final String type;
	private final String workloadIndex;
	private final String workloadType;

	public InsertProperties(DefaultProperties props) {
		numOfThreads = props.get(THREADS, 1);
		numOfIterations = props.get(ITERATIONS, 1);
		docPerIteration = props.get(DOCS, 100);
		workloadLocation = props.getProperty(CommandPropsConstants.WORKLOAD_OPT);
		index = props.getProperty(CommandPropsConstants.INDEX_OPT);
		type = props.getProperty(CommandPropsConstants.TYPE_OPT);
		workloadIndex = props.get(WORKLOAD_INDEX, this.index);
		workloadType = props.getProperty(WORKLOAD_TYPE);
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

}
