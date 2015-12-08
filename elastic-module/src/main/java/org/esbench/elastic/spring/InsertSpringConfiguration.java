package org.esbench.elastic.spring;

import org.esbench.elastic.sender.DocumentSenderFactory;
import org.esbench.elastic.sender.SimpleInsertAction;
import org.esbench.elastic.sender.cluster.MasterNodeInsertAction;
import org.esbench.elastic.sender.cluster.SlaveNodeInsertAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class InsertSpringConfiguration {
	@Bean
	public DocumentSenderFactory documentSenderFactory() {
		DocumentSenderFactory factory = new DocumentSenderFactory();
		return factory;
	}

	@Bean
	public SimpleInsertAction simpleInsertAction(DocumentSenderFactory senderFactory) {
		SimpleInsertAction action = new SimpleInsertAction(senderFactory);
		return action;
	}

	@Bean
	public SlaveNodeInsertAction slaveNodeInsertAction(DocumentSenderFactory senderFactory) {
		SlaveNodeInsertAction action = new SlaveNodeInsertAction(senderFactory);
		return action;
	}

	@Bean
	public MasterNodeInsertAction masterNodeInsertAction(DocumentSenderFactory senderFactory) {
		MasterNodeInsertAction action = new MasterNodeInsertAction(senderFactory);
		return action;
	}
}
