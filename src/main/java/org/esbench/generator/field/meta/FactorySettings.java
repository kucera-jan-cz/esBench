package org.esbench.generator.field.meta;

import java.util.Collections;
import java.util.List;

@Deprecated
// Until further notice we will use ObjectTypeMetadata for index types and others
public class FactorySettings {
	private List<StringFieldMetadata> stringFields = Collections.emptyList();
	private List<BooleanFieldMetadata> booleanFields = Collections.emptyList();
	private List<DateFieldMetadata> dateFields = Collections.emptyList();
	private List<ObjectTypeMetadata> objectTypes = Collections.emptyList();

	public List<StringFieldMetadata> getStringFields() {
		return stringFields;
	}

	public List<BooleanFieldMetadata> getBooleanFields() {
		return booleanFields;
	}

	public List<DateFieldMetadata> getDateFields() {
		return dateFields;
	}

	public List<ObjectTypeMetadata> getObjectTypes() {
		return objectTypes;
	}

	public void setStringFields(List<StringFieldMetadata> stringFields) {
		this.stringFields = stringFields;
	}

	public void setBooleanFields(List<BooleanFieldMetadata> booleanFields) {
		this.booleanFields = booleanFields;
	}

	public void setDateFields(List<DateFieldMetadata> dateFields) {
		this.dateFields = dateFields;
	}

	public void setObjectTypes(List<ObjectTypeMetadata> objectTypes) {
		this.objectTypes = objectTypes;
	}

}
