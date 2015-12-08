package org.esbench.cmd;

import java.util.Arrays;
import java.util.List;

/**
 * Constant class for basic properties used in command-line command esbench.
 */
public final class CommandPropsConstants {

	// Available commands for esBench execution
	public static final String INSERT_CMD = "insert";
	public static final String INSERT_MASTER_CMD = "insert-master";
	public static final String INSERT_SLAVE_CMD = "insert-slave";
	public static final String COLLECT_CMD = "collect";
	public static final String LIST_PROPS_CMD = "list-props";
	static final List<String> ALLOWED_CMDS = Arrays.asList(COLLECT_CMD, INSERT_CMD, INSERT_MASTER_CMD, INSERT_SLAVE_CMD, LIST_PROPS_CMD);

	// General options
	public static final String HELP_OPT = "help";
	public static final String HOST_OPT = "host";
	public static final String PORT_OPT = "port";

	// Configuration and workload options
	public static final String CONF_OPT = "conf";
	public static final String WORKLOAD_OPT = "workload";

	// Connection options to Elasticsearch
	public static final String INDEX_OPT = "index";
	public static final String TYPE_OPT = "type";
	public static final String CLUSTER_OPT = "cluster.name";

	private CommandPropsConstants() {

	}
}
