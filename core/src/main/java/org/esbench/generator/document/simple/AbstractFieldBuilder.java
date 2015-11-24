package org.esbench.generator.document.simple;

import java.io.IOException;

import org.esbench.generator.field.FieldConstants;
import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.FieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Base abstract class for JSONBuilders which handles single value/array fields. 
 * @param <T> defines type which given FieldFactory must produce. 
 */
public abstract class AbstractFieldBuilder<T> implements JsonBuilder {
	protected FieldMetadata meta;
	protected FieldFactory<T> factory;
	private final boolean isArray;

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
			gen.writeFieldName(meta.getName());
			writeValue(gen, instanceId);
		}
	}

	/**
	 * Writes only value (not field name or array) to Jackson's JSONGenerator generator.
	 * @param generator for writing value to JSON
	 * @param instanceId for creating unique field value
	 * @throws IOException when serialization fails for any reason
	 */
	protected abstract void writeValue(JsonGenerator generator, int instanceId) throws IOException;
}
