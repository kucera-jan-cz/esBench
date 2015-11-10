package org.esbench.generator.field.meta;

import java.util.List;

public class IndexTypeMetadata {
	private String indexName;
	private String typeName;
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

}
