package org.esbench.config.json.databind;

import java.io.IOException;
import java.util.Collections;

import org.esbench.config.ConfigurationConstants;
import org.esbench.generator.field.meta.ObjectTypeMetadata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectMetadataDeserializer extends JsonDeserializer<ObjectTypeMetadata> {

	private final DefaultFieldMetadataProvider defaultMetaProvider;

	public ObjectMetadataDeserializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.defaultMetaProvider = defaultMetaProvider;
	}

	@Override
	public ObjectTypeMetadata deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jsonParser);
		String name = root.path(ConfigurationConstants.NAME_PROP).asText();
		ObjectTypeMetadata objectType = new ObjectTypeMetadata(name, Collections.emptyList());
		return objectType;
	}

	@Override
	public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
		// TODO Auto-generated method stub
		return super.deserializeWithType(p, ctxt, typeDeserializer);
	}

}
