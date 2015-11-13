package org.esbench.generator.field.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.esbench.generator.field.FieldConstants;

import com.fasterxml.jackson.core.JsonGenerator;

public class ObjectTypeMetadata extends FieldMetadata {
	private List<FieldMetadata> innerMetadata;

	public ObjectTypeMetadata(String name, List<FieldMetadata> innerMetadata) {
		super(name, Object.class, FieldConstants.SINGLE_VALUE);
		this.innerMetadata = new ArrayList<>(innerMetadata);
		Collections.sort(this.innerMetadata, (a, b) -> a.getFullPath().compareTo(b.getFullPath()));
	}

	public List<FieldMetadata> getInnerMetadata() {
		return innerMetadata;
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
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

	@Override
	public void specificMetadataToJSON(JsonGenerator generator) throws IOException {
		write(generator, StringFieldMetadata.class);
		write(generator, DateFieldMetadata.class);
		write(generator, BooleanFieldMetadata.class);
		write(generator, ObjectTypeMetadata.class);
	}

	private void write(JsonGenerator generator, Class<? extends FieldMetadata> clazz) throws IOException {
		List<FieldMetadata> filtered = innerMetadata.stream().filter(m -> clazz.isInstance(m)).collect(Collectors.toList());
		for(FieldMetadata meta : filtered) {
			meta.toJSON(generator);
		}
	}
}
