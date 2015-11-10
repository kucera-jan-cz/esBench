package org.esbench.elastic.stats;

import com.fasterxml.jackson.databind.JsonNode;

class FieldInfo {
	private String fullPath;
	private JsonNode json;

	public FieldInfo(String fullFieldName, JsonNode json) {
		this.fullPath = fullFieldName;
		this.json = json;
	}

	public String getFullPath() {
		return fullPath;
	}

	public JsonNode getJson() {
		return json;
	}

}