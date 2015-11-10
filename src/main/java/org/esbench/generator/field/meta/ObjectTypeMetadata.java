package org.esbench.generator.field.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esbench.generator.field.FieldConstants;

import com.fasterxml.jackson.core.JsonGenerator;

public class ObjectTypeMetadata extends FieldMetadata {
	private List<FieldMetadata> innerMetadata;
	@Deprecated
	private final boolean isRoot;

	public ObjectTypeMetadata(String name, boolean isRoot, List<FieldMetadata> innerMetadata) {
		super(name, Object.class, FieldConstants.SINGLE_VALUE);
		this.isRoot = isRoot;
		this.innerMetadata = new ArrayList<>(innerMetadata);
		Collections.sort(this.innerMetadata, (a, b) -> a.getFullPath().compareTo(b.getFullPath()));
	}

	@Deprecated
	public boolean isRoot() {
		return isRoot;
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
		StringBuilder buff = new StringBuilder();
		buff.append("Object [name=").append(getName()).append('\n');
		for(FieldMetadata meta : innerMetadata) {
			buff.append('\t').append(meta).append('\n');
		}
		buff.append("]\n");
		return buff.toString();
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
