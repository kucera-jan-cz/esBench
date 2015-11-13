package org.esbench.generator.field.meta;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esbench.generator.field.FieldConstants;

import com.fasterxml.jackson.core.JsonGenerator;

//@TODO - implement rest of the configuration
public class BooleanFieldMetadata extends FieldMetadata {
	public enum Type {
		ALWAYS_TRUE, ALWAYS_FALSE, TICK_TOCK
	};

	private final Type type;

	public BooleanFieldMetadata(String name) {
		this(name, FieldConstants.SINGLE_VALUE, Type.TICK_TOCK);
	}

	public BooleanFieldMetadata(String name, int valuesPerDoc, Type type) {
		super(name, Boolean.class, valuesPerDoc);
		Validate.notNull(type);
		this.type = type;
	}

	public Type getType() {
		return type;
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
	public void specificMetadataToJSON(JsonGenerator generator) throws IOException {
		generator.writeStringField("TODO", "");
	}

}
