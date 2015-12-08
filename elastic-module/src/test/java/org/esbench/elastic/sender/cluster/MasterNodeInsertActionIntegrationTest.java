package org.esbench.elastic.sender.cluster;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.esbench.cmd.EsBenchAction;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.sender.DocumentSender;
import org.esbench.elastic.sender.DocumentSenderFactory;
import org.esbench.elastic.sender.InsertProperties;
import org.esbench.generator.document.DocumentFactory;
import org.esbench.generator.document.simple.SimpleDocumentFactory;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MasterNodeInsertActionIntegrationTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MasterNodeInsertActionIntegrationTest.class);
	Properties userDefined;
	Properties defaults;
	DefaultProperties defaultProperties;

	@BeforeClass
	public void loadProperties() throws IOException {
		defaultProperties = new DefaultProperties("default-test.properties", "default.properties");
	}

	@Test
	public void validateMaster() throws IOException, InterruptedException {
		DocumentSender masterSenderMock = mock(DocumentSender.class);
		startMaster(masterSenderMock);

		SendValidationAnswer masterAnswer = new SendValidationAnswer();
		doAnswer(masterAnswer).when(masterSenderMock).send(Matchers.<DocumentFactory<String>> any(), any(InsertProperties.class), anyInt());

		DocumentSender slaveSenderMock = mock(DocumentSender.class);
		SendValidationAnswer slaveAnswer = new SendValidationAnswer();
		doAnswer(slaveAnswer).when(slaveSenderMock).send(Matchers.<DocumentFactory<String>> any(), any(InsertProperties.class), anyInt());

		verifyZeroInteractions(masterSenderMock);

		startSlave(slaveSenderMock);
		slaveAnswer.executeLatch.await(1, TimeUnit.MINUTES);
		verify(masterAnswer, slaveAnswer);
	}

	private void verify(SendValidationAnswer... answers) {
		List<SendValidationAnswer> answerList = Arrays.asList(answers);
		assertEquals(answerList.stream().map(a -> a.factory.newInstance(0)).distinct().count(), 1, "Factories produces different values");
		assertEquals(answerList.stream().map(a -> a.props).distinct().count(), 1, "Insert properties are different on master and slaves");
		List<Integer> froms = answerList.stream().map(a -> a.from).distinct().sorted().collect(Collectors.toList());
		assertEquals(froms, expectedFroms());
	}

	private List<Integer> expectedFroms() {
		InsertProperties insertProps = new InsertProperties(defaultProperties);
		int iterations = insertProps.getNumOfIterations();
		int docsPerIteration = insertProps.getDocPerIteration();
		int nodes = insertProps.getClusterNodes();
		List<Integer> froms = IntStream.range(0, nodes).boxed().map(i -> i * docsPerIteration * iterations).collect(Collectors.toList());
		return froms;
	}

	private static class SendValidationAnswer implements Answer<Void> {
		private SimpleDocumentFactory factory;
		private InsertProperties props;
		private Integer from;
		private CountDownLatch executeLatch = new CountDownLatch(1);

		@Override
		public Void answer(InvocationOnMock invocation) throws Throwable {
			factory = invocation.getArgumentAt(0, SimpleDocumentFactory.class);
			props = invocation.getArgumentAt(1, InsertProperties.class);
			from = invocation.getArgumentAt(2, Integer.class);
			executeLatch.countDown();
			return null;
		}

	}

	private void startSlave(DocumentSender mockedSender) throws IOException {
		DocumentSenderFactory mockedFactory = mock(DocumentSenderFactory.class);
		when(mockedFactory.newInstance(any(DefaultProperties.class))).thenReturn(mockedSender);
		SlaveNodeInsertAction node = new SlaveNodeInsertAction(mockedFactory);
		DefaultProperties props = new DefaultProperties(new Properties(), defaults);
		execute(node, props);
	}

	private void startMaster(DocumentSender mockedSender) throws IOException {
		DocumentSenderFactory mockedFactory = mock(DocumentSenderFactory.class);
		when(mockedFactory.newInstance(any(DefaultProperties.class))).thenReturn(mockedSender);
		MasterNodeInsertAction node = new MasterNodeInsertAction(mockedFactory);
		execute(node, defaultProperties);
	}

	private void execute(EsBenchAction node, DefaultProperties props) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					node.perform(props);
				} catch (IOException e) {
					LOGGER.error("MasterNode failed: ", e);
				}
			}
		};
		thread.start();
	}
}
