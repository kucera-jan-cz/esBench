package org.esbench.config.json.databind;

import static org.esbench.config.ConfigurationConstants.FIELDS_PROP;
import static org.esbench.config.ConfigurationConstants.NAME_PROP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.FieldMetadataUtils;
import org.esbench.generator.field.meta.MetaType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;

public class FieldsParser {
	private final DefaultFieldMetadataProvider defaultMetaProvider;

	public FieldsParser(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.defaultMetaProvider = defaultMetaProvider;
	}

	public void serialize(List<FieldMetadata> fields, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeObjectFieldStart(FIELDS_PROP);
		for(FieldMetadata field : fields) {
			String fieldName = field.getFullPath();
			MetaType metaType = field.getMetaType();
			Validate.notNull(metaType, "No type for field %s", fieldName);
			FieldMetadata diff = defaultMetaProvider.getDiff(field);
			serializers.defaultSerializeField(fieldName, diff, gen);
		}
		gen.writeEndObject();
	}

	public List<FieldMetadata> getFields(ObjectMapper mapper, ObjectNode root) throws JsonParseException, IOException {
		JsonNode fieldsJson = root.path(FIELDS_PROP);
		if(fieldsJson.isArray()) {
			return getFieldsFromArray(mapper, fieldsJson);
		} else {
			return getFieldsFromObject(mapper, fieldsJson);
		}
	}

	private List<FieldMetadata> getFieldsFromObject(ObjectMapper mapper, JsonNode fieldsJson) throws JsonParseException, IOException {
		List<FieldMetadata> fields = new ArrayList<>();
		Iterator<Entry<String, JsonNode>> it = fieldsJson.fields();
		while(it.hasNext()) {
			Entry<String, JsonNode> entry = it.next();
			String fieldName = entry.getKey();
			JsonNode fieldJson = entry.getValue();
			Validate.isTrue(!fieldJson.isArray(), "Field %s in %s should not be an array", fieldName, fieldJson);
			JsonParser enriched = enrichJsonObject(mapper, fieldName, fieldJson);
			FieldMetadata metadata = deserializeMetadata(mapper, enriched);
			fields.add(metadata);
		}
		return fields;
	}

	private List<FieldMetadata> getFieldsFromArray(ObjectMapper mapper, JsonNode fieldsJson) throws JsonParseException, IOException {
		List<FieldMetadata> fields = new ArrayList<>();
		Iterator<JsonNode> it = fieldsJson.elements();
		while(it.hasNext()) {
			FieldMetadata metadata = deserializeMetadata(mapper, it.next().traverse(mapper));
			fields.add(metadata);
		}
		return fields;
	}

	private FieldMetadata deserializeMetadata(ObjectMapper mapper, JsonParser json) throws JsonParseException, IOException {
		FieldMetadata diff = mapper.readValue(json, FieldMetadata.class);
		MetaType metaType = diff.getMetaType();
		FieldMetadata defaultMeta = defaultMetaProvider.getDefaultMetadata(metaType);
		return FieldMetadataUtils.merge(diff, defaultMeta);
	}

	private JsonParser enrichJsonObject(ObjectMapper mapper, String fieldName, JsonNode fieldJson) throws IOException {
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
		return jp;
	}
}
