package org.esbench.workload.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.FieldMetadataUtils;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.generator.field.meta.TokenList;
import org.esbench.workload.Workload;
import org.esbench.workload.WorkloadConstants;
import org.esbench.workload.json.databind.DefaultFieldMetadataProvider;
import org.esbench.workload.json.databind.IndexTypeDeserializer;
import org.esbench.workload.json.databind.IndexTypeSerializer;
import org.esbench.workload.json.databind.TokensIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Parses workload file Java objects.
 */
public class WorkloadParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadParser.class);

	/**
	 * Parse inputs from given reader to Workload
	 * @param reader holding workload to parse
	 * @return Workload representing metadata
	 * @throws IOException when workload can't be found or JSON parsing failed
	 */
	public Workload parse(Reader reader) throws IOException {
		ObjectMapper mapper = MapperFactory.initMapper();
		JsonNode root = mapper.readTree(reader);
		return parseWorkload(mapper, root);
	}

	private Workload parseWorkload(ObjectMapper mapper, JsonNode root) throws IOException {
		String version = root.get(WorkloadConstants.VERSION_PROP).asText();
		// Load defaults without interering with
		Map<MetaType, FieldMetadata> defaults = getDefaults(mapper, root.path(WorkloadConstants.DEFAULTS_PROP));
		SimpleModule updatedModule = initModule(defaults);

		ObjectMapper updatedMapper = MapperFactory.initMapper(updatedModule);

		JavaType tokenListType = updatedMapper.getTypeFactory().constructCollectionType(List.class, TokenList.class);
		JsonParser refsParser = root.path(WorkloadConstants.REFS_PROP).traverse(updatedMapper);
		// Read token reference so it's loaded into TokensIdResolver instance
		updatedMapper.readValue(refsParser, tokenListType);

		JavaType list = updatedMapper.getTypeFactory().constructCollectionType(List.class, IndexTypeMetadata.class);
		JsonParser histogramParser = root.path(WorkloadConstants.HISTOGRAM_PROP).traverse(updatedMapper);
		List<IndexTypeMetadata> indiceTypes = updatedMapper.readValue(histogramParser, list);

		Workload workload = new Workload(version, defaults, indiceTypes);
		return workload;
	}

	/**
	 * Serialize given workload to JSON representation and write it to writer.
	 * @param writer to which workload will be written as JSON
	 * @param workload that should be written to writer
	 * @throws IOException when JSON parsing failed for any reason
	 */
	public void parse(Writer writer, Workload workload) throws IOException {
		Validate.notNull(writer);
		Validate.notNull(workload);
		SimpleModule module = MapperFactory.initDefaultModule();
		ObjectMapper mapper = MapperFactory.initMapper(module);
		DefaultFieldMetadataProvider defaultMetaProvider = new DefaultFieldMetadataProvider();
		IndexTypeSerializer serializer = new IndexTypeSerializer(defaultMetaProvider);
		module.addSerializer(IndexTypeMetadata.class, serializer);

		JsonFactory factory = mapper.getFactory();
		JsonGenerator gen = factory.createGenerator(writer);
		gen.setPrettyPrinter(new WorkloadPrettyPrinter());

		gen.writeStartObject();

		gen.writeStringField(WorkloadConstants.VERSION_PROP, WorkloadConstants.CURRENT_VERSION);
		gen.writeObjectField(WorkloadConstants.DEFAULTS_PROP, workload.getDefaults());

		workload.getDefaults().values().stream().forEach(m -> defaultMetaProvider.registerDefaultMetadata(m));

		gen.writeObjectField(WorkloadConstants.HISTOGRAM_PROP, workload.getIndiceTypes());

		writeReferences(MapperFactory.getTokenIdGenerator(mapper), gen);

		gen.writeEndObject();
		gen.close();
	}

	/**
	 * Write external refence tokens using internal TokensIdGenerator.
	 * @param idGenerator for retrieving collected references
	 * @param jsonGen to which actual values will be written
	 * @throws IOException when JSON serialization fails for any reason
	 */
	private void writeReferences(TokensIdGenerator idGenerator, JsonGenerator jsonGen) throws IOException {
		jsonGen.writeArrayFieldStart(WorkloadConstants.REFS_PROP);

		for(Entry<Integer, Object> entry : idGenerator.getObjectMap().entrySet()) {
			jsonGen.writeStartObject();
			Integer key = entry.getKey();

			jsonGen.writeNumberField(WorkloadConstants.REF_ID_PROP, key);
			TokenList tokenList = (TokenList) entry.getValue();
			jsonGen.writeObjectField(WorkloadConstants.TOKENS_PROP, tokenList.getTokens());
			jsonGen.writeEndObject();
		}
		jsonGen.writeEndArray();
	}

	private SimpleModule initModule(Map<MetaType, FieldMetadata> defaults) throws IOException {
		SimpleModule module = MapperFactory.initDefaultModule();
		DefaultFieldMetadataProvider defaultMetaProvider = new DefaultFieldMetadataProvider();
		IndexTypeDeserializer deserializer = new IndexTypeDeserializer(defaultMetaProvider);
		defaults.values().stream().forEach(m -> defaultMetaProvider.registerDefaultMetadata(m));
		module.addDeserializer(IndexTypeMetadata.class, deserializer);
		return module;
	}

	private Map<MetaType, FieldMetadata> getDefaults(ObjectMapper mapper, JsonNode defaultsNode) throws IOException {
		Map<MetaType, FieldMetadata> defaults = new HashMap<>();
		for(MetaType metaType : MetadataConstants.DEFAULT_META_BY_TYPE.keySet()) {
			JsonNode fieldNode = defaultsNode.path(metaType.name());
			if(fieldNode.isMissingNode()) {
				defaults.put(metaType, MetadataConstants.DEFAULT_META_BY_TYPE.get(metaType));
			} else {
				FieldMetadata meta = mapper.readValue(fieldNode.traverse(mapper), FieldMetadata.class);
				FieldMetadata defaultMeta = MetadataConstants.DEFAULT_META_BY_TYPE.get(metaType);
				FieldMetadata merged = FieldMetadataUtils.merge(meta, defaultMeta);
				LOGGER.debug("Original meta: {}", meta);
				LOGGER.debug("Merged   meta: {}", merged);
				defaults.put(metaType, merged);
			}
		}
		return defaults;
	}
}
