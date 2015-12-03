package org.esbench.elastic.sender;

import java.io.IOException;

import org.esbench.generator.document.DocumentFactory;

public interface DocumentSender {

	void send(DocumentFactory<String> factory, InsertProperties properties) throws IOException;

	void send(DocumentFactory<String> factory, InsertProperties properties, int from) throws IOException;

}