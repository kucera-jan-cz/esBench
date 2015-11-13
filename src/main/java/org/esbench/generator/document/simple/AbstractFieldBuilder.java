package org.esbench.generator.document.simple;

import java.io.IOException;

import org.esbench.generator.field.FieldConstants;
import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.FieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public abstract class AbstractFieldBuilder<T> implements JsonBuilder {
	protected FieldMetadata meta;
	protected FieldFactory<T> factory;
	private boolean isArray;

	public AbstractFieldBuilder(FieldMetadata meta, FieldFactory<T> factory) {
		this.meta = meta;
		this.factory = factory;
		this.isArray = meta.getValuesPerDocument() > FieldConstants.SINGLE_VALUE;
	}

	@Override
	public void write(JsonGenerator gen, int instanceId) throws IOException {
		if(isArray) {
			gen.writeArrayFieldStart(meta.getName());
			int id = instanceId * meta.getValuesPerDocument();
			for(int i = id; i < id + meta.getValuesPerDocument(); i++) {
				writeValue(gen, i);
			}
			gen.writeEndArray();
		} else {
			writeValueWithName(gen, instanceId);
		}
	}

	protected abstract void writeValue(JsonGenerator gen, int instanceId) throws IOException;

	protected abstract void writeValueWithName(JsonGenerator gen, int instanceId) throws IOException;
}
