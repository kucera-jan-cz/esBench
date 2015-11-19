package org.esbench.config.json;

import java.time.Instant;

import org.esbench.config.json.databind.DefaultFieldMetadataProvider;
import org.esbench.config.json.databind.IndexTypeDeserializer;
import org.esbench.config.json.databind.IndexTypeSerializer;
import org.esbench.config.json.databind.InstantDeserializer;
import org.esbench.config.json.databind.InstantSerializer;
import org.esbench.generator.field.meta.IndexTypeMetadata;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MapperFactory {
	private static final String MODULE_NAME = "SimpleModule";

	public static SimpleModule initDefaultModule() {
		SimpleModule module = new SimpleModule(MODULE_NAME, Version.unknownVersion());
		DefaultFieldMetadataProvider defaultMetaProvider = new DefaultFieldMetadataProvider();
		module.addSerializer(Instant.class, new InstantSerializer());
		// module.addSerializer(ObjectTypeMetadata.class, new ObjectMetadataSerializer(defaultMetaProvider));
		module.addSerializer(IndexTypeMetadata.class, new IndexTypeSerializer(defaultMetaProvider));

		module.addDeserializer(Instant.class, new InstantDeserializer());
		// module.addDeserializer(ObjectTypeMetadata.class, new ObjectMetadataDeserializer(defaultMetaProvider));
		module.addDeserializer(IndexTypeMetadata.class, new IndexTypeDeserializer(defaultMetaProvider));
		return module;
	}

	public static ObjectMapper initMapper() {
		return initMapper(initDefaultModule());
	}

	public static ObjectMapper initMapper(SimpleModule module) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		return mapper;
	}
}
