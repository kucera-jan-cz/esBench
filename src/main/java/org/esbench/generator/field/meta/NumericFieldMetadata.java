package org.esbench.generator.field.meta;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.core.JsonGenerator;

public class NumericFieldMetadata extends FieldMetadata {
	public enum Type {
		BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE
	}

	private Type type;
	private final Number from;
	private final Number to;
	private final Number step;

	public NumericFieldMetadata(String fullPath, int valuesPerDoc, Type type, Number from, Number to, Number step) {
		super(fullPath, Number.class, valuesPerDoc);
		this.type = type;
		this.from = from;
		this.to = to;
		this.step = step;
	}

	public Type getType() {
		return type;
	}

	public Number getFrom() {
		return from;
	}

	public Number getTo() {
		return to;
	}

	public Number getStep() {
		return step;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

	@Override
	public void specificMetadataToJSON(JsonGenerator generator) throws IOException {
		// @TODO - decide whether to use numeric or string
		// generator.writeNumberField("from", from);
		// generator.writeStringField("to", to);
		// generator.writeStringField("step", step);

	}
}
