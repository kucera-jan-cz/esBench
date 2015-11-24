package org.esbench.generator.field.meta;

import java.util.List;

import org.esbench.generator.field.FieldConstants;

public class MultiFieldMetadata extends FieldMetadata {
	private List<FieldMetadata> fields;

	/**
	 * Protected constructor for JSON serialization
	 */
	protected MultiFieldMetadata() {

	}

	public MultiFieldMetadata(String fieldName, List<FieldMetadata> fields) {
		super(fieldName, MetaType.MULTI, FieldConstants.SINGLE_VALUE);
		this.fields = fields;
	}

	public void setFields(List<FieldMetadata> fields) {
		this.fields = fields;
	}

	public List<FieldMetadata> getFields() {
		return fields;
	}

}
