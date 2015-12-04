package org.esbench.elastic.sender.cluster;

import static org.esbench.elastic.sender.cluster.ClusterConstants.CONF_MAP;
import static org.esbench.elastic.sender.cluster.ClusterConstants.DEFAULT_PROPS;
import static org.esbench.elastic.sender.cluster.ClusterConstants.DEFAULT_WAIT_UNIT;
import static org.esbench.elastic.sender.cluster.ClusterConstants.EXEC_LATCH;
import static org.esbench.elastic.sender.cluster.ClusterConstants.NODE_ID;
import static org.esbench.elastic.sender.cluster.ClusterConstants.PREP_LATCH;
import static org.esbench.elastic.sender.cluster.ClusterConstants.WORKLOAD;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.esbench.cmd.EsBenchAction;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.sender.AbstractInsertAction;
import org.esbench.elastic.sender.DocumentSender;
import org.esbench.elastic.sender.DocumentSenderFactory;
import org.esbench.elastic.sender.InsertProperties;
import org.esbench.generator.document.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IMap;

/**
 * Sl
 */
public class SlaveNodeInsertAction extends AbstractInsertAction implements EsBenchAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SlaveNodeInsertAction.class);
	private ICountDownLatch prepLatch;
	private ICountDownLatch execLatch;
	private IMap<String, Object> configMap;
	private long id;
	private final DocumentSenderFactory senderFactory;

	public SlaveNodeInsertAction(DocumentSenderFactory senderFactory) {
		this.senderFactory = senderFactory;
	}

	/**
	 * Wait till master node prepare configuration for sending, then create it's own sender and starts document insertion.
	 */
	@Override
	public void perform(DefaultProperties properties) throws IOException {
		ClientConfig config = new XmlClientConfigBuilder("hazelcast/hazelcast-slave.xml").build();
		String[] masterAddresses = properties.getProperty(ClusterConstants.CLUSTER_MASTER_PROP).split(",");
		config.getNetworkConfig().addAddress(masterAddresses);

		HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
		try {
			init(client, properties);
		} catch (InterruptedException ex) {
			LOGGER.error("Thread interrupted: ", ex);
		} finally {
			client.shutdown();
		}
	}

	private void init(HazelcastInstance hz, DefaultProperties slaveProperties) throws IOException, InterruptedException {
		IAtomicLong nodeId = hz.getAtomicLong(NODE_ID);
		id = nodeId.getAndIncrement();

		prepLatch = hz.getCountDownLatch(PREP_LATCH);
		if(id < 1) {
			prepLatch.trySetCount(1);
		}
		prepLatch.await(DEFAULT_WAIT_UNIT, TimeUnit.MINUTES);

		execLatch = hz.getCountDownLatch(EXEC_LATCH);

		configMap = hz.getMap(CONF_MAP);
		DefaultProperties masterProperties = (DefaultProperties) configMap.get(DEFAULT_PROPS);
		DefaultProperties merged = DefaultProperties.merge(slaveProperties, masterProperties);
		executeSend(merged);
		LOGGER.info("Leader finished");
	}

	private void executeSend(DefaultProperties defaults) throws IOException, InterruptedException {
		InsertProperties insProperties = new InsertProperties(defaults);
		String workloadAsText = (String) configMap.get(WORKLOAD);
		Reader reader = new StringReader(workloadAsText);
		DocumentFactory<String> factory = super.getFactory(insProperties, reader);
		DocumentSender sender = senderFactory.newInstance(defaults);

		int docsPerIteration = insProperties.getDocPerIteration();
		int startingFrom = docsPerIteration * (int) id;
		execLatch.countDown();
		execLatch.await(DEFAULT_WAIT_UNIT, TimeUnit.MINUTES);
		sender.send(factory, insProperties, startingFrom);
	}

}
