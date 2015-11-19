package org.esbench.generator.document.simple;

import java.io.IOException;

import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.NumericFieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class NumericFieldBuilder {

	public static JsonBuilder newInstance(NumericFieldMetadata meta, FieldFactory<? extends Number> factory) {
		switch(meta.getMetaType()) {
		case INTEGER:
			return new JsonBuilder() {
				@Override
				public void write(JsonGenerator gen, int instanceId) throws IOException {
					gen.writeNumberField(meta.getName(), factory.newInstance(instanceId).intValue());

				}
			};
		case LONG:
			return new JsonBuilder() {
				@Override
				public void write(JsonGenerator gen, int instanceId) throws IOException {
					gen.writeNumberField(meta.getName(), factory.newInstance(instanceId).longValue());

				}
			};
		case BYTE:
			return new JsonBuilder() {
				@Override
				public void write(JsonGenerator gen, int instanceId) throws IOException {
					gen.writeNumberField(meta.getName(), factory.newInstance(instanceId).byteValue());

				}
			};
		case SHORT:
			return new JsonBuilder() {
				@Override
				public void write(JsonGenerator gen, int instanceId) throws IOException {
					gen.writeNumberField(meta.getName(), factory.newInstance(instanceId).shortValue());

				}
			};
		case DOUBLE:
			return new JsonBuilder() {
				@Override
				public void write(JsonGenerator gen, int instanceId) throws IOException {
					gen.writeNumberField(meta.getName(), factory.newInstance(instanceId).doubleValue());

				}
			};
		case FLOAT:
			return new JsonBuilder() {
				@Override
				public void write(JsonGenerator gen, int instanceId) throws IOException {
					gen.writeNumberField(meta.getName(), factory.newInstance(instanceId).floatValue());

				}
			};
		default:
			throw new IllegalArgumentException("Unknown type: " + meta.getMetaType());
		}
	}
}
