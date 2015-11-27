package org.esbench.elastic.sender;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.esbench.cmd.EsBenchAction;
import org.esbench.core.DefaultProperties;
import org.esbench.elastic.utils.ElasticClientBuilder;
import org.esbench.generator.document.DocumentFactory;

/**
 * Action for inserting docs to Elasticsearch.
 */
public class SimpleInsertAction extends AbstractInsertAction implements EsBenchAction {

	/**
	 * Based on given props perform docs inserting. 
	 * @param props holding information properties for establish ClientSender and other components necessary for inserting.
	 * @throws IOException when sending or configuration loading fails for any reason
	 */
	@Override
	public void perform(DefaultProperties props) throws IOException {
		InsertProperties insProperties = new InsertProperties(props);
		DocumentFactory<String> factory = super.getFactory(insProperties);
		Client client = new ElasticClientBuilder().withProperties(props).build();
		DocumentSender sender = new DocumentSender(client);

		sender.send(factory, insProperties);
	}
}
