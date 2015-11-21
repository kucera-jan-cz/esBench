package org.esbench.elastic.stats;

import com.fasterxml.jackson.databind.JsonNode;

class FieldInfo {
	private final String fullPath;
	private final boolean nested;
	private final JsonNode json;

	public FieldInfo(String fullFieldName, boolean nested, JsonNode json) {
		this.fullPath = fullFieldName;
		this.json = json;
		this.nested = nested;
	}

	public boolean isNested() {
		return nested;
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getParent() {
		int index = fullPath.lastIndexOf('.');
		if(index < 0) {
			return fullPath;
		} else {
			return fullPath.substring(0, index);
		}
	}

	public JsonNode getJson() {
		return json;
	}

}