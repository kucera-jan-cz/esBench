package org.esbench.cmd;

import java.util.Arrays;
import java.util.List;

/**
 * Constant class for basic properties used in command-line command esbench.
 */
public final class CommandPropsConstants {

	// Commands
	public static final String INSERT_CMD = "insert";
	public static final String COLLECT_CMD = "collect";
	static final List<String> ALLOWED_CMDS = Arrays.asList(COLLECT_CMD, INSERT_CMD);

	// General options
	public static final String HELP_OPT = "help";
	public static final String HOST_OPT = "host";
	public static final String PORT_OPT = "port";
	public static final String CONF_OPT = "conf";
	public static final String WORKLOAD_OPT = "workload";
	public static final String INDEX_OPT = "index";
	public static final String TYPE_OPT = "type";

	public static final String CLUSTER_OPT = "cluster.name";

	private CommandPropsConstants() {

	}
}
