package org.esbench.generator.document.simple;

import java.io.IOException;

import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.BooleanFieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class BooleanFieldBuilder extends AbstractFieldBuilder<Boolean> {

	public BooleanFieldBuilder(BooleanFieldMetadata meta, FieldFactory<Boolean> factory) {
		super(meta, factory);
	}

	@Override
	protected void writeValue(JsonGenerator gen, int instanceId) throws IOException {
		gen.writeBoolean(factory.newInstance(instanceId));
	}
}
