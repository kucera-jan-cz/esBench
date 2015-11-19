package org.esbench.config.json.databind;

import static org.esbench.config.ConfigurationConstants.ARRAY_PROP;
import static org.esbench.config.ConfigurationConstants.NAME_PROP;
import static org.esbench.config.ConfigurationConstants.TYPE_PROP;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class FieldDeserializer<T extends FieldMetadata> extends JsonDeserializer<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FieldDeserializer.class);

	@Override
	public T deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

		JsonNode node = mapper.readTree(jsonParser);

		String fullFieldName = node.textValue();
		fullFieldName = (fullFieldName == null) ? node.path(NAME_PROP).asText(StringUtils.EMPTY) : fullFieldName;
		ObjectNode root = (ObjectNode) mapper.readTree(jsonParser);

		return deserializeSpecific(fullFieldName, jsonParser, root);
	}

	protected abstract T deserializeSpecific(String fullFieldName, JsonParser jsonParser, ObjectNode root)
			throws JsonParseException, JsonMappingException, IOException;

	protected int parseValuesPerDoc(JsonNode root, int defaultValue) {
		return root.path(ARRAY_PROP).asInt(defaultValue);
	}

	protected MetaType parseType(JsonNode root) {
		MetaType type = MetaType.valueOf(root.path(TYPE_PROP).asText().toUpperCase());
		return type;
	}

}
