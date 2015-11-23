package org.esbench.workload.json.databind;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class FieldMetadataTypeIdResolver extends TypeIdResolverBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldMetadataTypeIdResolver.class);
	private JavaType mBaseType;

	@Override
	public void init(JavaType bt) {
		this.mBaseType = bt;
	}

	@Override
	public Id getMechanism() {
		return JsonTypeInfo.Id.CUSTOM;
	}

	@Override
	public String idFromValue(Object obj) {
		return idFromValueAndType(obj, obj.getClass());
	}

	@Override
	public String idFromBaseType() {
		return idFromValueAndType(null, mBaseType.getRawClass());
	}

	@Override
	public String idFromValueAndType(Object obj, Class<?> clazz) {
		FieldMetadata meta = (FieldMetadata) obj;
		return meta.getMetaType().name().toUpperCase();
	}

	@Override
	public JavaType typeFromId(String type) {
		LOGGER.debug("Detecting class type from {}", type);
		MetaType metaType = MetaType.valueOf(type.toUpperCase());
		Class<?> clazz = MetadataConstants.DEFAULT_META_BY_TYPE.get(metaType).getClass();
		return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, clazz);

	}
}
