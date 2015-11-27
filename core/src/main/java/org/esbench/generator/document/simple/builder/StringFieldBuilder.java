package org.esbench.generator.document.simple.builder;

import java.io.IOException;

import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.FieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class StringFieldBuilder extends AbstractFieldBuilder<String> {

	public StringFieldBuilder(FieldMetadata meta, FieldFactory<String> factory) {
		super(meta, factory);
	}

	@Override
	protected void writeValue(JsonGenerator gen, int instanceId) throws IOException {
		gen.writeString(factory.newInstance(instanceId));
	}
}
