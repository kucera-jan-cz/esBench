package org.esbench.generator.field.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.esbench.generator.field.FieldConstants;

//@TODO - enable support for multiple documents
public class ObjectTypeMetadata extends FieldMetadata {
	private List<FieldMetadata> innerMetadata;

	/**
	 * Protected constructor for JSON serialization
	 */
	protected ObjectTypeMetadata() {

	}

	public ObjectTypeMetadata(String name, int valuesPerDoc, List<FieldMetadata> innerMetadata) {
		super(name, MetaType.OBJECT, valuesPerDoc);
		this.innerMetadata = new ArrayList<>(innerMetadata);
		Collections.sort(this.innerMetadata, (a, b) -> a.getFullPath().compareTo(b.getFullPath()));
	}

	public ObjectTypeMetadata(String name, List<FieldMetadata> innerMetadata) {
		this(name, FieldConstants.SINGLE_VALUE, innerMetadata);
	}

	public List<FieldMetadata> getInnerMetadata() {
		return innerMetadata;
	}

	public void setInnerMetadata(List<FieldMetadata> innerMetadata) {
		this.innerMetadata = innerMetadata;
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
}
