package org.esbench.generator.field.meta;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esbench.generator.field.FieldConstants;

import com.fasterxml.jackson.core.JsonGenerator;

//@TODO - implement rest of the configuration
public class BooleanFieldMetadata extends FieldMetadata {

	public BooleanFieldMetadata(String name) {
		super(name, Boolean.class, FieldConstants.SINGLE_VALUE);
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
