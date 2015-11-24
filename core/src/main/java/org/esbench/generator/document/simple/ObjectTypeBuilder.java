package org.esbench.generator.document.simple;

import java.io.IOException;
import java.util.List;

import org.esbench.generator.field.FieldConstants;
import org.esbench.generator.field.meta.ObjectTypeMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class ObjectTypeBuilder implements JsonBuilder {

	private final ObjectTypeMetadata meta;
	private final List<JsonBuilder> factories;
	private final boolean isArray;

	public ObjectTypeBuilder(ObjectTypeMetadata meta, List<JsonBuilder> factories) {
		this.meta = meta;
		this.factories = factories;
		this.isArray = meta.getValuesPerDocument() > FieldConstants.SINGLE_VALUE;
	}

	@Override
	public void write(JsonGenerator gen, int instanceId) throws IOException {
		if(isArray) {
			writeObjectArray(gen, instanceId);
		} else {
			writeObject(gen, instanceId);
		}
	}

	private void writeObject(JsonGenerator gen, int instanceId) throws IOException {
		gen.writeObjectFieldStart(meta.getName());
		for(JsonBuilder buider : factories) {
			buider.write(gen, instanceId);
		}
		gen.writeEndObject();
	}

	private void writeObjectArray(JsonGenerator gen, int instanceId) throws IOException {
		gen.writeArrayFieldStart(meta.getName());
		int id = instanceId * meta.getValuesPerDocument();
		for(int i = id; i < id + meta.getValuesPerDocument(); i++) {
			for(JsonBuilder buider : factories) {
				buider.write(gen, instanceId);
			}
		}
		gen.writeEndArray();
	}
}
