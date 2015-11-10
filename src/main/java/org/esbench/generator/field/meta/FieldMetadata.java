package org.esbench.generator.field.meta;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.core.JsonGenerator;

//@TODO - Look at PropertyUtilsBean in commons beanutils
public abstract class FieldMetadata {
	private final String name;
	private final String fullPath;
	private final Class<?> classType;
	private final int valuesPerDocument;

	public FieldMetadata(String fullPath, Class<?> classType, int valuesPerDoc) {
		Validate.notNull(fullPath);
		this.name = fullPath.substring(fullPath.lastIndexOf('.') + 1);
		this.fullPath = fullPath;
		this.classType = classType;
		this.valuesPerDocument = valuesPerDoc;
	}

	public String getName() {
		return name;
	}

	public String getFullPath() {
		return fullPath;
	}

	public Class<?> getClassType() {
		return classType;
	}

	public Class<?> getType() {
		return classType;
	}

	public int getValuesPerDocument() {
		return valuesPerDocument;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public abstract void specificMetadataToJSON(JsonGenerator generator) throws IOException;

	public void toJSON(JsonGenerator generator) throws IOException {
		generator.writeObjectFieldStart(fullPath);
		generator.writeStringField("type", classType.getSimpleName().toLowerCase());
		generator.writeNumberField("array", valuesPerDocument);
		specificMetadataToJSON(generator);
		generator.writeEndObject();
	}
}
