package org.esbench.config.json.databind;

import static org.esbench.config.ConfigurationConstants.ARRAY_PROP;
import static org.esbench.config.ConfigurationConstants.TYPE_PROP;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.TemporalAccessor;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.Objects;

public abstract class FieldSerializer<T extends FieldMetadata> extends JsonSerializer<T> {
	private static final String VALUES_PER_DOCUMENT_FIELD = "valuesPerDocument";
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldSerializer.class);
	private final FieldMetadata defaultMetadata;
	// @TODO check about thread-safety
	private PropertyUtilsBean beanUtil = new PropertyUtilsBean();

	public FieldSerializer(FieldMetadata defaultMetadata) {
		this.defaultMetadata = defaultMetadata;
	}

	@Override
	public void serialize(FieldMetadata meta, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		String fillFieldName = meta.getFullPath();

		gen.writeObjectFieldStart(fillFieldName);
		MetaType type = meta.getMetaType();
		gen.writeStringField(TYPE_PROP, type.toString().toLowerCase());

		writeValue(gen, serializers, ARRAY_PROP, VALUES_PER_DOCUMENT_FIELD, defaultMetadata, meta);
		serializeSpecific((T) meta, gen, serializers);

		gen.writeEndObject();
	}

	protected abstract void serializeSpecific(T meta, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException;

	protected boolean differ(String property, FieldMetadata defaultMeta, FieldMetadata meta) {
		try {
			Object defaultValue = beanUtil.getNestedProperty(defaultMeta, property);
			Object value = beanUtil.getNestedProperty(meta, property);
			return !Objects.equal(defaultValue, value);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LOGGER.warn("Failed to get bean property {}", property, e);
			return true;
		}
	}

	protected void writeValue(JsonGenerator gen, SerializerProvider serializers, String fieldName, String property, FieldMetadata defaultMeta,
			FieldMetadata meta) throws IOException {
		Object defaultValue = getValue(defaultMeta, property);
		Object value = getValue(meta, property);
		if(!Objects.equal(defaultValue, value)) {
			serializers.defaultSerializeField(fieldName, value, gen);
		}
	}

	protected void writeNumericValue(JsonGenerator gen, String fieldName, String property, FieldMetadata defaultMeta, FieldMetadata meta) throws IOException {
		if(differ(property, defaultMeta, meta)) {
			Number value = getValue(meta, property);
			writeNumber(meta, gen, fieldName, value);
		}
	}

	protected void writeTemporalAccessor(JsonGenerator gen, String fieldName, String property, FieldMetadata defaultMeta, FieldMetadata meta)
			throws IOException {
		if(differ(property, defaultMeta, meta)) {
			TemporalAccessor temporal = getValue(meta, property);
			String value = MetadataConstants.DEFAULT_DATE_FORMATTER.format(temporal);
			gen.writeStringField(fieldName, value);
		}
	}

	@SuppressWarnings({ "hiding", "unchecked" })
	protected <T> T getValue(FieldMetadata meta, String property) {
		try {
			return (T) beanUtil.getNestedProperty(meta, property);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LOGGER.warn("Failed to get property {} of bean {}", property, meta, e);
			throw new IllegalStateException("Can't access property " + property);
		}
	}

	protected void writeNumber(FieldMetadata meta, JsonGenerator gen, String fieldName, Number value) throws IOException {
		switch(meta.getMetaType()) {
		case INTEGER:
			gen.writeNumberField(fieldName, value.intValue());
			break;
		case LONG:
			gen.writeNumberField(fieldName, value.longValue());
			break;
		case SHORT:
			gen.writeNumberField(fieldName, value.shortValue());
			break;
		case FLOAT:
			gen.writeNumberField(fieldName, value.floatValue());
			break;
		case DOUBLE:
			gen.writeNumberField(fieldName, value.doubleValue());
			break;
		case BYTE:
			gen.writeNumberField(fieldName, value.byteValue());
			break;
		default:
			throw new IllegalArgumentException("Unsupported type: " + meta.getMetaType());
		}
	}

}
