package org.esbench.workload.json.databind;

import java.util.HashMap;
import java.util.Map;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.FieldMetadataUtils;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;

public class DefaultFieldMetadataProvider {
	private final Map<MetaType, FieldMetadata> defaultMetadataByType = new HashMap<>();

	public FieldMetadata getDefaultMetadata(MetaType metaType) {
		FieldMetadata defaultMeta = defaultMetadataByType.get(metaType);
		if(defaultMeta == null) {
			defaultMeta = MetadataConstants.DEFAULT_META_BY_TYPE.get(metaType);
		}
		return defaultMeta;
	}

	// @TODO - implement also "merging mechanism" and to remove DRY
	public FieldMetadata getDiff(FieldMetadata field) {
		MetaType metaType = field.getMetaType();
		FieldMetadata defaultMeta = getDefaultMetadata(metaType);
		if(defaultMeta != null) {
			FieldMetadata diff = FieldMetadataUtils.diff(field, defaultMeta);
			diff.setMetaType(metaType);
			return diff;
		} else {
			return field;
		}
	}

	public void registerDefaultMetadata(FieldMetadata meta) {
		defaultMetadataByType.put(meta.getMetaType(), meta);
	}
}
