package org.esbench.generator.document.simple.builder;

import java.io.IOException;

import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.BooleanFieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Serializes Boolean fields to JSON.
 */
public class BooleanFieldBuilder extends AbstractFieldBuilder<Boolean> {

	public BooleanFieldBuilder(BooleanFieldMetadata meta, FieldFactory<Boolean> factory) {
		super(meta, factory);
	}

	@Override
	protected void writeValue(JsonGenerator gen, int instanceId) throws IOException {
		gen.writeBoolean(factory.newInstance(instanceId));
	}
}
