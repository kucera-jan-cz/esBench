package org.esbench.config.json.databind;

import static org.esbench.config.ConfigurationConstants.FIELDS_PROP;
import static org.esbench.config.ConfigurationConstants.INDEX_PROP;
import static org.esbench.config.ConfigurationConstants.NAME_PROP;
import static org.esbench.config.ConfigurationConstants.TYPE_PROP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.FieldMetadataUtils;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;

public class IndexTypeDeserializer extends JsonDeserializer<IndexTypeMetadata> {
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexTypeDeserializer.class);

	private final DefaultFieldMetadataProvider defaultMetaProvider;

	public IndexTypeDeserializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.defaultMetaProvider = defaultMetaProvider;
	}

	@Override
	public IndexTypeMetadata deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jsonParser);
		String name = root.path(INDEX_PROP).asText();
		String type = root.path(TYPE_PROP).asText();
		LOGGER.debug("Parsing index: {} type: {}", name, type);
		List<FieldMetadata> fields = getFields(mapper, root);
		IndexTypeMetadata meta = new IndexTypeMetadata(name, type, fields);
		return meta;
	}

	private List<FieldMetadata> getFields(ObjectMapper mapper, ObjectNode root) throws JsonParseException, IOException {
		JsonNode fieldsJson = root.path(FIELDS_PROP);
		Iterator<Entry<String, JsonNode>> it = fieldsJson.fields();
		List<FieldMetadata> fields = new ArrayList<>();
		while(it.hasNext()) {
			Entry<String, JsonNode> entry = it.next();
			String fieldName = entry.getKey();
			JsonNode fieldJson = entry.getValue();
			if(fieldJson.isArray()) {
				Iterator<JsonNode> innerIt = fieldJson.elements();
				while(innerIt.hasNext()) {
					FieldMetadata metadata = deserializeMetadata(mapper, fieldName, innerIt.next());
					fields.add(metadata);
				}
			} else {
				FieldMetadata metadata = deserializeMetadata(mapper, fieldName, fieldJson);
				fields.add(metadata);
			}
		}
		return fields;
	}

	FieldMetadata deserializeMetadata(ObjectMapper mapper, String fieldName, JsonNode fieldJson) throws JsonParseException, IOException {
		TokenBuffer tb = new TokenBuffer(null, false);
		JsonParser jp = fieldJson.traverse(mapper);

		jp.nextToken(); // Get to Start TOKEN
		JsonToken token = jp.getCurrentToken();
		tb.writeStringField(NAME_PROP, fieldName);
		do {
			jp.nextToken();
			token = jp.getCurrentToken();
			tb.copyCurrentStructure(jp);
		} while(token != JsonToken.END_OBJECT);
		jp = tb.asParser(jp);
		jp.nextToken();
		tb.close();
		FieldMetadata diff = mapper.readValue(jp, FieldMetadata.class);
		MetaType metaType = diff.getMetaType();
		FieldMetadata defaultMeta = defaultMetaProvider.getDefaultMetadata(metaType);
		return FieldMetadataUtils.merge(diff, defaultMeta);
	}
}
