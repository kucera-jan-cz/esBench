package org.esbench.generator.document.simple.builder;

import java.io.IOException;
import java.util.List;

import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.field.meta.ObjectTypeMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class ObjectTypeBuilder extends AbstractFieldBuilder<Object> implements JsonBuilder {

	public ObjectTypeBuilder(ObjectTypeMetadata meta, List<JsonBuilder> factories) {
		super(meta, factories);
	}

	@Override
	protected void writeValue(JsonGenerator gen, int instanceId) throws IOException {
		gen.writeStartObject();
		for(JsonBuilder buider : factories) {
			buider.write(gen, instanceId);
		}
		gen.writeEndObject();
	}
}
