package org.esbench.workload.json.databind;

import java.io.IOException;

import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.workload.WorkloadConstants;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class ObjectMetadataSerializer extends JsonSerializer<ObjectTypeMetadata> {
	private final FieldsParser listSerializer;

	public ObjectMetadataSerializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.listSerializer = new FieldsParser(defaultMetaProvider);
	}

	@Override
	public void serialize(ObjectTypeMetadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeStringField(WorkloadConstants.NAME_PROP, value.getFullPath());
		gen.writeStringField(WorkloadConstants.TYPE_PROP, value.getMetaType().name());
		if(value.getValuesPerDocument() != null) {
			gen.writeNumberField(WorkloadConstants.ARRAY_PROP, value.getValuesPerDocument());
		}
		listSerializer.serialize(value.getInnerMetadata(), gen, serializers);
		gen.writeEndObject();
	}

	@Override
	public void serializeWithType(ObjectTypeMetadata value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		typeSer.writeTypePrefixForObject(value, gen);
		gen.writeStringField(WorkloadConstants.NAME_PROP, value.getFullPath());
		gen.writeStringField(WorkloadConstants.TYPE_PROP, value.getMetaType().name());
		if(value.getValuesPerDocument() != null) {
			gen.writeNumberField(WorkloadConstants.ARRAY_PROP, value.getValuesPerDocument());
		}
		if(value.getInnerMetadata() != null) {
			listSerializer.serialize(value.getInnerMetadata(), gen, serializers);
		}
		typeSer.writeTypeSuffixForObject(value, gen);
	}

}
