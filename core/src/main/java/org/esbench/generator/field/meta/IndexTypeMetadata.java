package org.esbench.generator.field.meta;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IndexTypeMetadata {
	private final String indexName;
	private final String typeName;
	private List<FieldMetadata> fields;

	public IndexTypeMetadata(String indexName, String typeName, List<FieldMetadata> fields) {
		this.indexName = indexName;
		this.typeName = typeName;
		this.fields = fields;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public List<FieldMetadata> getFields() {
		return fields;
	}

	public void setFields(List<FieldMetadata> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}
}
