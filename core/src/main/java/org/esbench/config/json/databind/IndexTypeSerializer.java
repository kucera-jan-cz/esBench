package org.esbench.config.json.databind;

import static org.esbench.config.ConfigurationConstants.FIELDS_PROP;
import static org.esbench.config.ConfigurationConstants.INDEX_PROP;
import static org.esbench.config.ConfigurationConstants.TYPE_PROP;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class IndexTypeSerializer extends JsonSerializer<IndexTypeMetadata> {
	private final DefaultFieldMetadataProvider defaultMetaProvider;

	public IndexTypeSerializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.defaultMetaProvider = defaultMetaProvider;
	}

	@Override
	public void serialize(IndexTypeMetadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeStringField(INDEX_PROP, value.getIndexName());
		gen.writeStringField(TYPE_PROP, value.getTypeName());
		gen.writeObjectFieldStart(FIELDS_PROP);
		for(FieldMetadata field : value.getFields()) {
			String fieldName = field.getFullPath();
			MetaType metaType = field.getMetaType();
			Validate.notNull(metaType, "No type for field %s", fieldName);
			FieldMetadata diff = defaultMetaProvider.getDiff(field);
			serializers.defaultSerializeField(fieldName, diff, gen);
		}
		gen.writeEndObject();
		gen.writeEndObject();
	}
}
