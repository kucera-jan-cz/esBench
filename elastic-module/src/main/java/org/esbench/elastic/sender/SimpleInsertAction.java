package org.esbench.elastic.sender;

import java.io.IOException;

import org.esbench.cmd.EsBenchAction;
import org.esbench.core.DefaultProperties;
import org.esbench.generator.document.DocumentFactory;

/**
 * Action for inserting docs to Elasticsearch.
 */
public class SimpleInsertAction extends AbstractInsertAction implements EsBenchAction {
	private final DocumentSenderFactory senderFactory;

	public SimpleInsertAction(DocumentSenderFactory senderFactory) {
		this.senderFactory = senderFactory;
	}

	/**
	 * Based on given props perform docs inserting.
	 * @param props holding information properties for establish ClientSender and other components necessary for inserting.
	 * @throws IOException when sending or configuration loading fails for any reason
	 */
	@Override
	public void perform(DefaultProperties props) throws IOException {
		InsertProperties insProperties = new InsertProperties(props);
		DocumentFactory<String> factory = super.getFactory(insProperties);
		DocumentSender sender = senderFactory.newInstance(props);

		sender.send(factory, insProperties);
	}
}
