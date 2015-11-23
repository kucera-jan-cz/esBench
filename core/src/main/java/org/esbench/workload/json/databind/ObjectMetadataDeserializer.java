package org.esbench.workload.json.databind;

import java.io.IOException;
import java.util.List;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.workload.WorkloadConstants;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectMetadataDeserializer extends JsonDeserializer<ObjectTypeMetadata> {
	private final FieldsParser listSerializer;

	public ObjectMetadataDeserializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.listSerializer = new FieldsParser(defaultMetaProvider);
	}

	@Override
	public ObjectTypeMetadata deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jsonParser);
		String name = root.path(WorkloadConstants.NAME_PROP).asText(MetadataConstants.DEFAULT_OBJECT_META.getFullPath());
		List<FieldMetadata> fields = listSerializer.getFields(mapper, root);
		ObjectTypeMetadata objectType = new ObjectTypeMetadata(name, fields);
		return objectType;
	}
}
