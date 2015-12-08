package org.esbench.elastic.sender;

import java.io.IOException;

import org.esbench.generator.document.DocumentFactory;

/**
 * Based on given {@link InsertProperties} sends documents using {@link DocumentFactory}.
 */
public interface DocumentSender {
	/**
	 * Sends documents using factory to destination defined in InsertProperties.
	 * @param factory for creating documents
	 * @param properties for configuring connection to DB.
	 * @throws IOException when sending documents or any issue occurs
	 */
	void send(DocumentFactory<String> factory, InsertProperties properties) throws IOException;

	/**
	 * Sends documents using factory to destination defined in InsertProperties.
	 * @param factory for creating documents
	 * @param properties for configuring connection to DB.
	 * @param from defining starting value for creating documents, @see {@link DocumentFactory}
	 * @throws IOException when sending documents or any issue occurs
	 */
	void send(DocumentFactory<String> factory, InsertProperties properties, int from) throws IOException;

}