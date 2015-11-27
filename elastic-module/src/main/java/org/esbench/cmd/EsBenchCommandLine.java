package org.esbench.cmd;

import static org.esbench.cmd.CommandPropsConstants.ALLOWED_CMDS;
import static org.esbench.cmd.CommandPropsConstants.COLLECT_CMD;
import static org.esbench.cmd.CommandPropsConstants.CONF_OPT;
import static org.esbench.cmd.CommandPropsConstants.HELP_OPT;
import static org.esbench.cmd.CommandPropsConstants.INSERT_CMD;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.esbench.core.DefaultProperties;
import org.esbench.core.ResourceUtils;
import org.esbench.elastic.sender.InsertDocsAction;
import org.esbench.elastic.stats.CollectWorkloadAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.SimpleCommandLinePropertySource;

public class EsBenchCommandLine {
	private static final String CMD = "cmd";
	private static final Logger LOGGER = LoggerFactory.getLogger(EsBenchCommandLine.class);

	private void processArgs(String... args) throws IOException {
		if(args.length == 0) {
			displayHelp(0);
		}
		String command = args[0];
		if(!ALLOWED_CMDS.contains(command)) {
			LOGGER.error("Command is not defined or invalid, please read help");
			displayHelp(10);
		}
		DefaultProperties defaultProps = loadProperties(args);
		if(defaultProps.contains(HELP_OPT)) {
			displayHelp(0);
		}
		EsBenchAction action = executeCommand(command);
		action.perform(defaultProps);
	}

	private EsBenchAction executeCommand(String command) throws IOException {
		switch(command) {
		case INSERT_CMD:
			return new InsertDocsAction();
		case COLLECT_CMD:
			return new CollectWorkloadAction();
		default:
			throw new IllegalArgumentException("Unknown command");
		}
	}

	private DefaultProperties loadProperties(String... args) throws IOException {
		SimpleCommandLinePropertySource cmdSource = new SimpleCommandLinePropertySource(CMD, args);
		Properties defaultProps = ResourceUtils.asProperties("default.properties");
		Properties properties = new Properties(defaultProps);
		if(cmdSource.containsProperty(CONF_OPT)) {
			String configTextPath = cmdSource.getProperty(CONF_OPT);
			Path configText = Paths.get(configTextPath);

			Properties fileProperties = ResourceUtils.asProperties(configText.toUri().toURL().toString());
			properties.putAll(fileProperties);
		} else {
			LOGGER.debug("No property {} presented, using defaults", CONF_OPT);
		}
		for(String name : cmdSource.getPropertyNames()) {
			String value = cmdSource.getProperty(name);
			LOGGER.debug("Overriding {} to {}", name, value);
			properties.put(name, value);
		}

		return new DefaultProperties(properties, defaultProps);
	}

	private void displayHelp(int returnCode) throws IOException {
		String helpAsText = ResourceUtils.asString("man_page.txt", StandardCharsets.UTF_8);
		System.out.println(helpAsText);
		System.exit(returnCode);
	}

	/**
	 * Executes appropriate action based on given args.
	 * @param args arguments for main method
	 * @throws IOException when configuration manipulation or internal issue appears
	 */
	public static void main(String[] args) throws IOException {
		EsBenchCommandLine cmd = new EsBenchCommandLine();
		cmd.processArgs(args);
	}
}
