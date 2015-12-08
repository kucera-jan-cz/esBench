package org.esbench.elastic.spring;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.stats.CollectWorkloadAction;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.testng.annotations.Test;

public class CollectSpringConfigurationTest {
	@Test
	public void initializeContext() throws IOException {
		DefaultProperties defaultProps = new DefaultProperties("default.properties");
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerSingleton("defaults", defaultProps);
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(beanFactory);
		context.register(MockCollectSpringConfiguration.class);
		context.refresh();
		CollectWorkloadAction action = context.getBean(CollectWorkloadAction.class);
		assertNotNull(action);
		context.close();
	}

	@Configuration
	protected static class MockCollectSpringConfiguration extends CollectSpringConfiguration {
		@Override
		public Client esClient() {
			return mock(Client.class);
		}
	}
}
