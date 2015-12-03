package org.esbench.elastic.spring;

import org.esbench.elastic.sender.DocumentSender;
import org.esbench.elastic.sender.cluster.MasterNodeInsertAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = InsertSpringConfiguration.class)
public class MasterNodeSpringConfiguration {

	@Bean
	public MasterNodeInsertAction masterNodeInsertAction(DocumentSender sender) {
		MasterNodeInsertAction action = new MasterNodeInsertAction(sender);
		return action;
	}
}
