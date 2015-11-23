package org.esbench.workload.json.databind;

import java.io.IOException;
import java.util.List;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.generator.field.meta.MultiFieldMetadata;
import org.esbench.workload.WorkloadConstants;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MultiMetadataDeserializer extends JsonDeserializer<MultiFieldMetadata> {
	private final FieldsParser listSerializer;

	public MultiMetadataDeserializer(DefaultFieldMetadataProvider defaultMetaProvider) {
		this.listSerializer = new FieldsParser(defaultMetaProvider);
	}

	@Override
	public MultiFieldMetadata deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jsonParser);
		String name = root.path(WorkloadConstants.NAME_PROP).asText(MetadataConstants.DEFAULT_MULTI_META.getFullPath());
		List<FieldMetadata> fields = listSerializer.getFields(mapper, root);
		for(FieldMetadata field : fields) {
			field.setFullPath(name);
		}
		MultiFieldMetadata multiField = new MultiFieldMetadata(name, fields);
		return multiField;
	}

	@Override
	public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
		// TODO Auto-generated method stub
		return super.deserializeWithType(p, ctxt, typeDeserializer);
	}

}
