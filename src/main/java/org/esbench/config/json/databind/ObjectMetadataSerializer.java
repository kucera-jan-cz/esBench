package org.esbench.config.json.databind;

import java.io.IOException;

import org.esbench.config.ConfigurationConstants;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class ObjectMetadataSerializer extends JsonSerializer<ObjectTypeMetadata> {
	private final DefaultFieldMetadataProvider defaultMetaProvider;

	public ObjectMetadataSerializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.defaultMetaProvider = defaultMetaProvider;
	}

	@Override
	public void serialize(ObjectTypeMetadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeStringField(ConfigurationConstants.NAME_PROP, value.getFullPath());
		gen.writeStringField(ConfigurationConstants.TYPE_PROP, value.getMetaType().name());
		if(value.getValuesPerDocument() != null) {
			gen.writeNumberField(ConfigurationConstants.ARRAY_PROP, value.getValuesPerDocument());
		}
		gen.writeEndObject();

		for(FieldMetadata field : value.getInnerMetadata()) {
			FieldMetadata diff = defaultMetaProvider.getDiff(field);
			serializers.defaultSerializeField(field.getFullPath(), diff, gen);
		}
	}

	@Override
	public void serializeWithType(ObjectTypeMetadata value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		typeSer.writeTypePrefixForObject(value, gen);
		gen.writeStringField(ConfigurationConstants.NAME_PROP, value.getFullPath());
		gen.writeStringField(ConfigurationConstants.TYPE_PROP, value.getMetaType().name());
		if(value.getValuesPerDocument() != null) {
			gen.writeNumberField(ConfigurationConstants.ARRAY_PROP, value.getValuesPerDocument());
		}
		typeSer.writeTypeSuffixForObject(value, gen);
		if(value.getInnerMetadata() != null) {
			for(FieldMetadata field : value.getInnerMetadata()) {
				FieldMetadata diff = defaultMetaProvider.getDiff(field);
				serializers.defaultSerializeField(field.getFullPath(), diff, gen);
			}
		}
	}

}
