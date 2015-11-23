package org.esbench.workload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;

public class Workload {
	private String version;
	private Map<MetaType, FieldMetadata> defaults = new HashMap<>();
	private List<IndexTypeMetadata> indiceTypes;

	public Workload(String version, Map<MetaType, FieldMetadata> defaults, List<IndexTypeMetadata> indiceTypes) {
		this.version = version;
		this.defaults = defaults;
		this.indiceTypes = indiceTypes;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<MetaType, FieldMetadata> getDefaults() {
		return defaults;
	}

	public void setDefaults(Map<MetaType, FieldMetadata> defaults) {
		this.defaults = defaults;
	}

	public List<IndexTypeMetadata> getIndiceTypes() {
		return indiceTypes;
	}

	public void setIndiceTypes(List<IndexTypeMetadata> indiceTypes) {
		this.indiceTypes = indiceTypes;
	}

}
