package org.esbench.generator.document.simple.builder;

import java.io.IOException;
import java.io.StringWriter;

import org.esbench.generator.document.simple.JsonBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public abstract class AbstractFieldBuilderTest {
	private final JsonFactory jsonFactory = new JsonFactory();

	protected String createJson(JsonBuilder builder, int instanceId) throws IOException {
		StringWriter writer = new StringWriter();
		JsonGenerator generator = jsonFactory.createGenerator(writer);
		generator.writeStartObject();
		builder.write(generator, instanceId);
		generator.writeEndObject();
		generator.flush();
		String json = writer.toString();
		return json;
	}
}
