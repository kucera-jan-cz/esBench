package org.esbench.elastic.stats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class StatsCollector {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatsCollector.class);
	// Type constants
	private static final String NESTED_TYPE = "nested";
	private static final String FIELD_SEPARATOR = ".";
	// Mapping constants
	private static final String TYPE_PROP = "type";
	private static final String PROPERTIES_PROP = "properties";

	private final Client client;
	private final CollectorProperties props;

	public StatsCollector(Client client, CollectorProperties props) {
		this.client = client;
		this.props = props;
	}

	public List<IndexTypeMetadata> collectIndex(String indexName) throws IOException {
		GetMappingsResponse response = client.admin().indices().prepareGetMappings(indexName).get();
		ImmutableOpenMap<String, MappingMetaData> mapping = response.getMappings().get(indexName);
		String[] indexTypes = mapping.keys().toArray(String.class);
		ObjectMapper mapper = new ObjectMapper();
		List<IndexTypeMetadata> typesMetadata = new ArrayList<>();
		FieldAnalyzer analyzer = new FieldAnalyzer(client, indexName, props);
		for(String indexType : indexTypes) {
			MappingMetaData meta = mapping.get(indexType);
			LOGGER.info("Index: {} Type: {}", indexName, indexType);
			String mappingsAsJson = meta.source().string();
			LOGGER.debug("JSON:\n{}", mappingsAsJson);

			JsonNode root = mapper.readValue(mappingsAsJson, JsonNode.class);
			JsonNode typeProp = root.path(indexType).path(PROPERTIES_PROP);
			ObjectTypeMetadata typeMeta = parseConfiguration(analyzer, typeProp, StringUtils.EMPTY, false);
			typesMetadata.add(new IndexTypeMetadata(indexName, indexType, typeMeta.getInnerMetadata()));
		}
		return typesMetadata;
	}

	private ObjectTypeMetadata parseConfiguration(FieldAnalyzer analyzer, JsonNode typeProp, String parentFullPath, boolean nested) {
		Validate.isTrue(!typeProp.isMissingNode(), "Parsing of mapping failed to look 'properties'");
		List<FieldMetadata> innerMetadata = new ArrayList<>();
		Multimap<String, FieldInfo> fieldsByteType = ArrayListMultimap.create();
		Iterator<String> it = typeProp.fieldNames();
		while(it.hasNext()) {
			String name = it.next();
			String fullFieldName = parentFullPath + FIELD_SEPARATOR + name;
			fullFieldName = StringUtils.removeStart(fullFieldName, FIELD_SEPARATOR);
			JsonNode fieldJson = typeProp.path(name);
			JsonNode fieldTypeJson = fieldJson.path(TYPE_PROP);
			String fieldType = fieldTypeJson.textValue();
			FieldInfo info = new FieldInfo(fullFieldName, nested, fieldJson);

			if(fieldTypeJson.isMissingNode() || NESTED_TYPE.equals(fieldType)) {
				boolean fieldNested = nested || NESTED_TYPE.equals(fieldType);
				ObjectTypeMetadata objectFields = parseConfiguration(analyzer, fieldJson.path(PROPERTIES_PROP), fullFieldName, fieldNested);
				innerMetadata.add(objectFields);
			} else {
				fieldsByteType.put(fieldType, info);
			}

		}
		innerMetadata.addAll(analyzer.collectMetadata(fieldsByteType));
		return new ObjectTypeMetadata(parentFullPath, innerMetadata);
	}

}
