package org.esbench.generator.document.simple;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.NumericFieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class NumericArrayFieldBuilder {

	public static JsonBuilder newInstance(NumericFieldMetadata meta, FieldFactory<? extends Number> factory) {
		switch(meta.getMetaType()) {
		case INTEGER:
			return new NumericArrayJsonBuilder(meta) {

				@Override
				protected void writeValue(JsonGenerator gen, int id) throws IOException {
					gen.writeNumber(factory.newInstance(id).intValue());
				}

			};
		case LONG:
			return new NumericArrayJsonBuilder(meta) {

				@Override
				protected void writeValue(JsonGenerator gen, int id) throws IOException {
					gen.writeNumber(factory.newInstance(id).longValue());
				}

			};
		case BYTE:
			return new NumericArrayJsonBuilder(meta) {

				@Override
				protected void writeValue(JsonGenerator gen, int id) throws IOException {
					gen.writeNumber(factory.newInstance(id).byteValue());
				}

			};
		case SHORT:
			return new NumericArrayJsonBuilder(meta) {

				@Override
				protected void writeValue(JsonGenerator gen, int id) throws IOException {
					gen.writeNumber(factory.newInstance(id).shortValue());
				}

			};
		case DOUBLE:
			return new NumericArrayJsonBuilder(meta) {

				@Override
				protected void writeValue(JsonGenerator gen, int id) throws IOException {
					gen.writeNumber(factory.newInstance(id).doubleValue());
				}

			};
		case FLOAT:
			return new NumericArrayJsonBuilder(meta) {

				@Override
				protected void writeValue(JsonGenerator gen, int id) throws IOException {
					gen.writeNumber(factory.newInstance(id).floatValue());
				}

			};
		default:
			throw new IllegalArgumentException("Unknown type: " + meta.getMetaType());
		}
	}

	private abstract static class NumericArrayJsonBuilder implements JsonBuilder {
		private final FieldMetadata meta;

		public NumericArrayJsonBuilder(FieldMetadata meta) {
			this.meta = meta;
			Validate.inclusiveBetween(2, Integer.MAX_VALUE, meta.getValuesPerDocument().intValue());
		}

		@Override
		public void write(JsonGenerator gen, int instanceId) throws IOException {
			gen.writeArrayFieldStart(meta.getName());
			int id = instanceId * meta.getValuesPerDocument();
			for(int i = id; i < id + meta.getValuesPerDocument(); i++) {
				writeValue(gen, i);
			}
			gen.writeEndArray();
		}

		protected abstract void writeValue(JsonGenerator gen, int id) throws IOException;

	}
}
