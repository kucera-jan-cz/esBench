package org.esbench.generator.document.simple.builder;

import java.io.IOException;
import java.util.List;

import org.esbench.generator.document.simple.InstanceIdTransformer;
import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.field.FieldConstants;
import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.FieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Base abstract class for JSONBuilders which handles single value/array fields.
 * @param <T> defines type which given FieldFactory must produce.
 */
abstract class AbstractFieldBuilder<T> implements JsonBuilder {
	protected FieldMetadata meta;
	protected FieldFactory<T> factory;
	protected List<JsonBuilder> factories;
	private final boolean isArray;
	private final InstanceIdTransformer idTransformation;

	public AbstractFieldBuilder(FieldMetadata meta, List<JsonBuilder> factories) {
		this.meta = meta;
		this.factories = factories;
		this.isArray = meta.getValuesPerDocument() > FieldConstants.SINGLE_VALUE;
		this.idTransformation = InstanceIdTransformer.valueOf(meta.getStrategy());
	}

	public AbstractFieldBuilder(FieldMetadata meta, FieldFactory<T> factory) {
		this.meta = meta;
		this.factory = factory;
		this.isArray = meta.getValuesPerDocument() > FieldConstants.SINGLE_VALUE;
		this.idTransformation = InstanceIdTransformer.valueOf(meta.getStrategy());
	}

	@Override
	public final void write(JsonGenerator gen, int unique) throws IOException {
		int instanceId = idTransformation.compute(unique);
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
