package org.esbench.cmd;

import static org.esbench.cmd.CommandPropsConstants.CLUSTER_OPT;
import static org.esbench.cmd.CommandPropsConstants.COLLECT_CMD;
import static org.esbench.cmd.CommandPropsConstants.CONF_OPT;
import static org.esbench.cmd.CommandPropsConstants.INSERT_CMD;
import static org.esbench.cmd.CommandPropsConstants.LIST_PROPS_CMD;
import static org.esbench.cmd.CommandPropsConstants.WORKLOAD_OPT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.client.Client;
import org.esbench.testng.EmbeddedElasticSearchServer;
import org.esbench.workload.json.WorkloadParser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EsBenchCommandLineIntegrationTest {
	private static final String INDEX = "esbench-test";
	private static final int EXPECTED_DOCS = 100;
	private EmbeddedElasticSearchServer server;
	private Path tempWorkloadDir;
	private Path workloadPath;

	@BeforeClass
	public void initializeElastic() throws IOException {
		server = new EmbeddedElasticSearchServer("target/es-data", "esbench-test", "node.local", "false");
		Path target = Paths.get("target");
		tempWorkloadDir = Files.createTempDirectory(target, "workload-data");
		workloadPath = tempWorkloadDir.resolve("workload.json");
	}

	@AfterClass
	public void shutdownElastic() {
		server.shutdown();
	}

	@Test
	public void printProperties() throws IOException, InterruptedException, ExecutionException {
		String[] args = parametrize(LIST_PROPS_CMD, CONF_OPT, "classpath:configurations/insert.properties");
		EsBenchCommandLine cmd = new EsBenchCommandLine();
		cmd.processArgs(args);

		validateDocCount(0);
	}

	@Test(dependsOnMethods = { "printProperties" })
	public void insertCommand() throws IOException, InterruptedException, ExecutionException {
		String clusterName = server.getClusterName();
		String[] args = parametrize(INSERT_CMD, CLUSTER_OPT, clusterName, CONF_OPT, "classpath:configurations/insert.properties");
		EsBenchCommandLine cmd = new EsBenchCommandLine();
		cmd.processArgs(args);

		validateDocCount(EXPECTED_DOCS);
	}

	@Test(dependsOnMethods = { "insertCommand" })
	public void collectCommand() throws IOException, InterruptedException, ExecutionException {
		String clusterName = server.getClusterName();
		String[] args = parametrize(COLLECT_CMD, CLUSTER_OPT, clusterName, CONF_OPT, "classpath:configurations/collect.properties", WORKLOAD_OPT,
				workloadPath.toString());
		EsBenchCommandLine cmd = new EsBenchCommandLine();
		cmd.processArgs(args);
		assertTrue(Files.exists(workloadPath));
		WorkloadParser parser = new WorkloadParser();
		assertNotNull(parser.parse(new FileReader(workloadPath.toFile())));
	}

	private void validateDocCount(int expectedDocs) throws InterruptedException, ExecutionException {
		Client client = server.getClient();
		client.admin().indices().flush(new FlushRequest()).get();
		long docs = client.count(new CountRequest()).get().getCount();
		assertEquals(docs, expectedDocs);
	}

	private String[] parametrize(String command, Object... propsAndValues) {
		List<String> params = new ArrayList<>();
		params.add(command);
		for(int i = 0; i < propsAndValues.length; i += 2) {
			String property = "--" + propsAndValues[i] + "=" + propsAndValues[i + 1];
			params.add(property);
		}
		return params.toArray(new String[0]);
	}
}
