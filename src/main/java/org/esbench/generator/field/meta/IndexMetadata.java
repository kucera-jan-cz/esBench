package org.esbench.generator.field.meta;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class IndexMetadata {
	private String name;
	private List<ObjectTypeMetadata> types;

	public IndexMetadata(String name, List<ObjectTypeMetadata> types) {
		this.name = name;
		this.types = types;
	}

	public String getName() {
		return name;
	}

	public List<ObjectTypeMetadata> getTypes() {
		return types;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
