package org.esbench.generator.document.simple;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.NumericFieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class NumericFieldBuilder {

	public static JsonBuilder newInstance(NumericFieldMetadata meta, FieldFactory<? extends Number> factory) {
		NumericConsumer writer = newWriter(meta);
		return new NumericJsonBuilder(meta, (FieldFactory<Number>) factory, writer);
	}

	private static NumericConsumer newWriter(NumericFieldMetadata meta) {
		switch(meta.getMetaType()) {
		case INTEGER:
			return (g, n) -> g.writeNumber(n.intValue());
		case LONG:
			return (g, n) -> g.writeNumber(n.longValue());
		case BYTE:
			return (g, n) -> g.writeNumber(n.byteValue());
		case SHORT:
			return (g, n) -> g.writeNumber(n.shortValue());
		case DOUBLE:
			return (g, n) -> g.writeNumber(n.doubleValue());
		case FLOAT:
			return (g, n) -> g.writeNumber(n.floatValue());
		default:
			throw new IllegalArgumentException("Unknown type: " + meta.getMetaType());
		}
	}

	private static class NumericJsonBuilder extends AbstractFieldBuilder<Number> {
		private final FieldFactory<? extends Number> factory;
		private final NumericConsumer writer;

		public NumericJsonBuilder(FieldMetadata meta, FieldFactory<Number> factory, NumericConsumer writer) {
			super(meta, factory);
			this.meta = meta;
			this.writer = writer;
			this.factory = factory;
			Validate.inclusiveBetween(1, Integer.MAX_VALUE, meta.getValuesPerDocument().intValue());

		}

		@Override
		protected void writeValue(JsonGenerator gen, int instanceId) throws IOException {
			writer.accept(gen, factory.newInstance(instanceId));
		}
	}

	@FunctionalInterface
	private static interface NumericConsumer {
		void accept(JsonGenerator generator, Number value) throws IOException;
	}
}
