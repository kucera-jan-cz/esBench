package org.esbench.generator.document.simple;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.DateFieldMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

public class DateFieldBuilder extends AbstractFieldBuilder<Instant> {
	private DateTimeFormatter formatter;

	public DateFieldBuilder(DateFieldMetadata meta, FieldFactory<Instant> factory) {
		super(meta, factory);
		this.formatter = meta.getFormatter();
	}

	@Override
	protected void writeValue(JsonGenerator gen, int instanceId) throws IOException {
		Instant date = factory.newInstance(instanceId);
		gen.writeString(formatter.format(date));
	}

	@Override
	protected void writeValueWithName(JsonGenerator gen, int instanceId) throws IOException {
		Instant date = factory.newInstance(instanceId);
		gen.writeStringField(meta.getName(), formatter.format(date));
	}
}
