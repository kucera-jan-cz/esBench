package org.esbench.elastic.sender.cluster;

/**
 * Holds constants for cluster-related properties
 */
public final class ClusterConstants {
	static final String CLUSTER_MASTER_PROP = "insert.cluster.master";

	public static final String PREP_LATCH = "preparationLatch";
	public static final String EXEC_LATCH = "executionLatch";
	public static final String CONF_MAP = "configurationMap";
	public static final String DEFAULT_PROPS = "properties";
	public static final String WORKLOAD = "workload";
	public static final String NODE_ID = "nodeId";

	private ClusterConstants() {
	}
}
