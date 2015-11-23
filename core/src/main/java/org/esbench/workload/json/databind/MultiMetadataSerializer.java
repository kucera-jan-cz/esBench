package org.esbench.workload.json.databind;

import java.io.IOException;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.MultiFieldMetadata;
import org.esbench.workload.WorkloadConstants;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class MultiMetadataSerializer extends JsonSerializer<MultiFieldMetadata> {
	private final FieldsParser listSerializer;

	public MultiMetadataSerializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.listSerializer = new FieldsParser(defaultMetaProvider);
	}

	@Override
	public void serialize(MultiFieldMetadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeStringField(WorkloadConstants.TYPE_PROP, value.getMetaType().name());
		listSerializer.serialize(value.getFields(), gen, serializers);
		gen.writeEndObject();
	}

	@Override
	public void serializeWithType(MultiFieldMetadata value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		typeSer.writeTypePrefixForObject(value, gen);
		gen.writeStringField(WorkloadConstants.TYPE_PROP, value.getMetaType().name());
		if(value.getFields() != null) {
			gen.writeArrayFieldStart(WorkloadConstants.FIELDS_PROP);
			for(FieldMetadata field : value.getFields()) {
				serializers.defaultSerializeValue(field, gen);
			}
			gen.writeEndArray();
		}
		typeSer.writeTypeSuffixForObject(value, gen);
	}

}
