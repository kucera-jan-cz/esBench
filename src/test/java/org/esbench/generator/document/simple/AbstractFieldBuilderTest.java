package org.esbench.generator.document.simple;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public abstract class AbstractFieldBuilderTest {
	private JsonFactory jsonFactory = new JsonFactory();

	protected String createJson(JsonBuilder builder, int instanceId) throws IOException {
		StringWriter writer = new StringWriter();
		JsonGenerator generator = jsonFactory.createGenerator(writer);
		generator.writeStartObject();
		builder.write(generator, instanceId);
		generator.writeEndObject();
		generator.flush();
		return writer.toString();
	}
}
