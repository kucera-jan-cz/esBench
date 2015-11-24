package org.esbench.cmd;

import static org.esbench.cmd.CommandPropsConstants.ALLOWED_CMDS;
import static org.esbench.cmd.CommandPropsConstants.COLLECT_CMD;
import static org.esbench.cmd.CommandPropsConstants.CONF_OPT;
import static org.esbench.cmd.CommandPropsConstants.HELP_OPT;
import static org.esbench.cmd.CommandPropsConstants.INDEX_OPT;
import static org.esbench.cmd.CommandPropsConstants.INSERT_CMD;
import static org.esbench.cmd.CommandPropsConstants.WORKLOAD_OPT;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.client.Client;
import org.esbench.core.DefaultProperties;
import org.esbench.core.ResourceUtils;
import org.esbench.elastic.sender.ClientSender;
import org.esbench.elastic.sender.InsertProperties;
import org.esbench.elastic.stats.CollectorProperties;
import org.esbench.elastic.stats.StatsCollector;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.workload.Workload;
import org.esbench.workload.WorkloadConstants;
import org.esbench.workload.json.WorkloadParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.SimpleCommandLinePropertySource;

public class EsBenchCommandLine {
	private static final String SEPARATOR = ",";
	private static final String CMD = "cmd";
	private static final Logger LOGGER = LoggerFactory.getLogger(EsBenchCommandLine.class);

	private void processArgs(String... args) throws IOException, InterruptedException {
		if(args.length == 0) {
			displayHelp(0);
		}
		String command = args[0];
		if(!ALLOWED_CMDS.contains(command)) {
			LOGGER.error("Command is not defined or invalid, please read help");
			displayHelp(10);
		}
		Properties properties = loadProperties(args);
		if(properties.contains(HELP_OPT)) {
			displayHelp(0);
		}
		executeCommand(command, properties);

	}

	private void executeCommand(String command, Properties properties) throws IOException, InterruptedException {
		switch(command) {
		case INSERT_CMD:
			insert(properties);
			break;
		case COLLECT_CMD:
			collection(properties);
			break;
		default:
			throw new IllegalArgumentException("Unknown command");
		}
	}

	private void insert(Properties properties) throws IOException, InterruptedException {
		DefaultProperties props = new DefaultProperties(properties);
		Client client = new ElasticClientBuilder().withProperties(props).build();
		ClientSender sender = new ClientSender(client);
		InsertProperties insProperties = new InsertProperties(props);
		sender.send(insProperties);
	}

	private void collection(Properties properties) throws IOException {
		DefaultProperties props = new DefaultProperties(properties);
		Client client = new ElasticClientBuilder().withProperties(props).build();
		String[] indices = props.getProperty(INDEX_OPT).split(SEPARATOR);

		List<IndexTypeMetadata> types = new ArrayList<>();
		for(String indexName : indices) {
			CollectorProperties collectionProperties = new CollectorProperties(props);
			StatsCollector collector = new StatsCollector(client, collectionProperties, indexName);
			types.addAll(collector.collectIndex());
		}

		Path workloadFilePath = Paths.get(props.getProperty(WORKLOAD_OPT));
		Files.createDirectories(workloadFilePath.getParent());
		Writer writer = new FileWriter(workloadFilePath.toFile(), false);
		WorkloadParser parser = new WorkloadParser();
		Workload config = new Workload(WorkloadConstants.CURRENT_VERSION, MetadataConstants.DEFAULT_META_BY_TYPE, types);
		parser.parse(writer, config);
		writer.close();
		LOGGER.info("Workload sucessfully write to {}", workloadFilePath);
	}

	private Properties loadProperties(String... args) throws IOException {
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
		return properties;
	}

	private void displayHelp(int returnCode) throws IOException {
		String helpAsText = ResourceUtils.asString("man_page.txt", StandardCharsets.UTF_8);
		System.out.println(helpAsText);
		System.exit(returnCode);
	}

	/**
	 *
	 * @param args arguments for main method
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		EsBenchCommandLine cmd = new EsBenchCommandLine();
		cmd.processArgs(args);
	}
}
