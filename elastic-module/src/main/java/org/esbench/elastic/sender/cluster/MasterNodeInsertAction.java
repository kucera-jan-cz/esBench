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
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.esbench.cmd.EsBenchAction;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.sender.AbstractInsertAction;
import org.esbench.elastic.sender.DocumentSender;
import org.esbench.elastic.sender.DocumentSenderFactory;
import org.esbench.elastic.sender.InsertProperties;
import org.esbench.generator.document.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IMap;

/**
 *  Responsible for coordinating clustered document insertion using multiple nodes (see {@link SlaveNodeInsertAction}.
 */
public class MasterNodeInsertAction extends AbstractInsertAction implements EsBenchAction {
	private static final int SINGLE_PREP_LATCH_VALUE = 1;
	private static final Logger LOGGER = LoggerFactory.getLogger(MasterNodeInsertAction.class);
	private ICountDownLatch prepLatch;
	private ICountDownLatch execLatch;
	private long id;
	private final DocumentSenderFactory senderFactory;

	public MasterNodeInsertAction(DocumentSenderFactory senderFactory) {
		this.senderFactory = senderFactory;
	}

	/**
	 * Establishes Hazelcast cluster and once required number of clients connect, execute multiple document insertion.
	 */
	@Override
	public void perform(DefaultProperties properties) throws IOException {
		Config config = new ClasspathXmlConfig("hazelcast/hazelcast-master.xml");
		String address = properties.getProperty(ClusterConstants.CLUSTER_MASTER_PROP);
		config.getNetworkConfig().setPublicAddress(address);

		HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
		try {
			InsertProperties insProperties = new InsertProperties(properties);
			init(hz, properties, insProperties);
			InetSocketAddress establishedAddress = hz.getCluster().getLocalMember().getSocketAddress();
			LOGGER.info("Cluster established at {}", establishedAddress);
			executeSend(properties, insProperties);
			LOGGER.info("Sending on master node finished");
			waitForAllClientShutdown(hz.getClientService());
		} catch (InterruptedException ex) {
			LOGGER.error("Thread interrupted: ", ex);
		} finally {
			hz.shutdown();
		}
	}

	private void init(HazelcastInstance hz, DefaultProperties properties, InsertProperties insProperties) throws IOException, InterruptedException {
		IAtomicLong nodeId = hz.getAtomicLong(NODE_ID);
		id = nodeId.getAndIncrement();
		prepLatch = hz.getCountDownLatch(PREP_LATCH);
		if(id < 1) {
			prepLatch.trySetCount(SINGLE_PREP_LATCH_VALUE);
		}

		execLatch = hz.getCountDownLatch(EXEC_LATCH);

		int numOfNodes = insProperties.getClusterNodes();
		LOGGER.info("Establishing master node... Waiting for {} nodes (including master node)", numOfNodes);
		Validate.isTrue(execLatch.trySetCount(numOfNodes), "Execution latch could not be set");

		IMap<String, Object> configMap = hz.getMap(CONF_MAP);
		configMap.put(DEFAULT_PROPS, properties);
		Reader reader = guessReader(insProperties.getWorkloadLocation());
		String workloadAsText = IOUtils.toString(reader);
		configMap.put(WORKLOAD, workloadAsText);

		prepLatch.countDown();
	}

	private void executeSend(DefaultProperties properties, InsertProperties insProperties) throws IOException, InterruptedException {
		int docsPerIteration = insProperties.getDocPerIteration();

		int startingFrom = insProperties.getNumOfIterations() * docsPerIteration * (int) id;

		DocumentFactory<String> factory = super.getFactory(insProperties);
		DocumentSender sender = senderFactory.newInstance(properties);
		LOGGER.info("Waiting for slave nodes...");
		execLatch.countDown();
		Validate.isTrue(execLatch.await(DEFAULT_WAIT_UNIT, TimeUnit.MINUTES), "Execution failed: waiting for nodes exceeded limit");
		LOGGER.info("All nodes ready, startign inserting...");
		sender.send(factory, insProperties, startingFrom);
	}

	private void waitForAllClientShutdown(ClientService clientService) throws InterruptedException {
		Lock lock = new ReentrantLock();
		Condition notEmpty = lock.newCondition();
		clientService.addClientListener(new ClientShutdownListener(lock, notEmpty));
		lock.lock();
		try {
			while(clientService.getConnectedClients().size() > 0) {
				notEmpty.await(DEFAULT_WAIT_UNIT, TimeUnit.MINUTES);
			}
		} finally {
			lock.unlock();
		}
		LOGGER.info("All slave nodes finished, shutting down");
	}

}
