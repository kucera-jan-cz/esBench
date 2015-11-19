package org.esbench.config.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.esbench.config.Configuration;
import org.esbench.config.ConfigurationConstants;
import org.esbench.config.json.databind.DefaultFieldMetadataProvider;
import org.esbench.config.json.databind.IndexTypeDeserializer;
import org.esbench.config.json.databind.IndexTypeSerializer;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.FieldMetadataUtils;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

/**
 * Parses configuration file Java objects.
 */
public class ConfigurationParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationParser.class);

	/**
	 * Loads resource by location and parse inputs to Configuration
	 * @param location defining configuration location
	 * @return Configuration representing metadata
	 * @throws IOException when configuration can't be found or JSON parsing failed
	 */
	public Configuration parse(String location) throws IOException {
		ObjectMapper mapper = MapperFactory.initMapper();
		JsonNode root = mapper.readTree(Resources.getResource(location));
		return parseConfig(mapper, root);
	}

	/**
	 * Parse inputs from given reader to Configuration
	 * @param reader holding configuration to parse
	 * @return Configuration representing metadata
	 * @throws IOException when configuration can't be found or JSON parsing failed
	 */
	public Configuration parse(Reader reader) throws IOException {
		ObjectMapper mapper = MapperFactory.initMapper();
		JsonNode root = mapper.readTree(reader);
		return parseConfig(mapper, root);
	}

	private Configuration parseConfig(ObjectMapper mapper, JsonNode root) throws IOException {
		String version = root.get(ConfigurationConstants.VERSION_PROP).asText();
		Map<MetaType, FieldMetadata> defaults = getDefaults(mapper, root.path(ConfigurationConstants.DEFAULTS_PROP));
		SimpleModule updatedModule = initModule(defaults);

		ObjectMapper updatedMapper = MapperFactory.initMapper(updatedModule);
		JavaType list = updatedMapper.getTypeFactory().constructCollectionType(List.class, IndexTypeMetadata.class);
		JsonParser histogramParser = root.path(ConfigurationConstants.HISTOGRAM_PROP).traverse(updatedMapper);
		List<IndexTypeMetadata> indiceTypes = updatedMapper.readValue(histogramParser, list);
		// for(IndexTypeMetadata indexType : indiceTypes) {
		// List<FieldMetadata> structuredFields = createObjectStructure(indexType.getFields());
		// indexType.setFields(structuredFields);
		// }

		Configuration configuration = new Configuration(version, defaults, indiceTypes);
		return configuration;
	}

	/**
	 * Serialize given config to JSON representation and write it to writer.
	 * @param writer to which will be configuration written as JSON
	 * @param config that should be written to writer
	 * @throws IOException when JSON parsing failed for any reason
	 */
	public void parse(Writer writer, Configuration config) throws IOException {
		Validate.notNull(writer);
		Validate.notNull(config);
		ObjectMapper mapper = MapperFactory.initMapper();
		SimpleModule module = MapperFactory.initDefaultModule();
		DefaultFieldMetadataProvider defaultMetaProvider = new DefaultFieldMetadataProvider();
		IndexTypeSerializer serializer = new IndexTypeSerializer(defaultMetaProvider);
		module.addSerializer(IndexTypeMetadata.class, serializer);

		JsonFactory factory = mapper.getFactory();
		JsonGenerator gen = factory.createGenerator(writer);
		gen.setPrettyPrinter(new ConfigurationPrettyPrinter());
		gen.writeStartObject();

		gen.writeStringField(ConfigurationConstants.VERSION_PROP, ConfigurationConstants.CURRENT_VERSION);
		gen.writeObjectField(ConfigurationConstants.DEFAULTS_PROP, config.getDefaults());

		config.getDefaults().values().stream().forEach(m -> defaultMetaProvider.registerDefaultMetadata(m));
		gen.writeArrayFieldStart(ConfigurationConstants.HISTOGRAM_PROP);
		for(IndexTypeMetadata type : config.getIndiceTypes()) {
			gen.writeObject(type);
		}
		gen.writeEndArray();

		gen.writeEndObject();
		gen.close();
	}

	private SimpleModule initModule(Map<MetaType, FieldMetadata> defaults) throws IOException {
		SimpleModule module = MapperFactory.initDefaultModule();
		DefaultFieldMetadataProvider defaultMetaProvider = new DefaultFieldMetadataProvider();
		IndexTypeDeserializer deserializer = new IndexTypeDeserializer(defaultMetaProvider);
		defaults.values().stream().forEach(m -> defaultMetaProvider.registerDefaultMetadata(m));
		module.addDeserializer(IndexTypeMetadata.class, deserializer);
		return module;
	}

	private List<FieldMetadata> createObjectStructure(List<FieldMetadata> fields) {
		List<FieldMetadata> typesAsTree = new ArrayList<>();

		Multimap<String, FieldMetadata> fieldByFullName = ArrayListMultimap.create();
		fields.stream().forEach(m -> fieldByFullName.put(m.getFullPath(), m));

		Predicate<FieldMetadata> simpleTypesFilter = m -> !MetaType.OBJECT.equals(m.getMetaType()) && !m.getFullPath().contains(".");
		List<FieldMetadata> simpleRootFiels = fields.stream().filter(simpleTypesFilter).collect(Collectors.toList());
		typesAsTree.addAll(simpleRootFiels);

		LOGGER.debug("Simple: {}", simpleRootFiels.stream().map(m -> m.getFullPath()).collect(Collectors.joining(",")));

		Pattern objPattern = Pattern.compile("([^\\.]*)\\.([^\\.]+)");

		Predicate<FieldMetadata> objectTypeFilter = m -> objPattern.matcher(m.getFullPath()).matches();
		List<FieldMetadata> objectFields = fields.stream().filter(objectTypeFilter).collect(Collectors.toList());
		LOGGER.debug("Object fields: {}", objectFields.stream().map(m -> m.getFullPath()).collect(Collectors.joining(",")));

		Comparator<String> sortByLevel = (a, b) -> StringUtils.countMatches(a, ".") - StringUtils.countMatches(b, ".");
		Function<FieldMetadata, String> toFullName = m -> getParent(m.getFullPath());
		Set<String> objectNames = objectFields.stream().map(toFullName).sorted(sortByLevel).collect(Collectors.toSet());
		LOGGER.debug("Objects: {}", objectNames);

		return typesAsTree;
	}

	@Deprecated
	// @TODO - until further notice this method is prototype
	private List<FieldMetadata> organizeObjects(Set<String> objectNames, Multimap<String, FieldMetadata> fieldByFullName) {
		return null;
	}

	private String getParent(String value) {
		return value.substring(0, value.lastIndexOf('.'));
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
