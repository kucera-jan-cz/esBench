package org.esbench.workload.json;

import java.time.Instant;

import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MultiFieldMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.workload.json.databind.DefaultFieldMetadataProvider;
import org.esbench.workload.json.databind.IndexTypeDeserializer;
import org.esbench.workload.json.databind.IndexTypeSerializer;
import org.esbench.workload.json.databind.InstantDeserializer;
import org.esbench.workload.json.databind.InstantSerializer;
import org.esbench.workload.json.databind.MultiMetadataDeserializer;
import org.esbench.workload.json.databind.MultiMetadataSerializer;
import org.esbench.workload.json.databind.ObjectMetadataDeserializer;
import org.esbench.workload.json.databind.ObjectMetadataSerializer;
import org.esbench.workload.json.databind.TokensIdGenerator;
import org.esbench.workload.json.databind.TokensIdResolver;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

class MapperFactory {
	private static final String MODULE_NAME = "SimpleModule";

	public static SimpleModule initDefaultModule() {
		SimpleModule module = new SimpleModule(MODULE_NAME, Version.unknownVersion());
		DefaultFieldMetadataProvider defaultMetaProvider = new DefaultFieldMetadataProvider();
		module.addSerializer(Instant.class, new InstantSerializer());
		module.addSerializer(ObjectTypeMetadata.class, new ObjectMetadataSerializer(defaultMetaProvider));
		module.addSerializer(IndexTypeMetadata.class, new IndexTypeSerializer(defaultMetaProvider));
		module.addSerializer(MultiFieldMetadata.class, new MultiMetadataSerializer(defaultMetaProvider));

		module.addDeserializer(Instant.class, new InstantDeserializer());
		module.addDeserializer(ObjectTypeMetadata.class, new ObjectMetadataDeserializer(defaultMetaProvider));
		module.addDeserializer(IndexTypeMetadata.class, new IndexTypeDeserializer(defaultMetaProvider));
		module.addDeserializer(MultiFieldMetadata.class, new MultiMetadataDeserializer(defaultMetaProvider));
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
		mapper.setHandlerInstantiator(new WorkloadHandlerInstantiator());
		return mapper;
	}

	public static TokensIdGenerator getTokenIdGenerator(ObjectMapper mapper) {
		WorkloadHandlerInstantiator handlerInstantiator = (WorkloadHandlerInstantiator) mapper.getSerializationConfig().getHandlerInstantiator();
		TokensIdGenerator generator = handlerInstantiator.getTokenIdGenerator();
		return generator;
	}

	public static TokensIdResolver getTokenIdResolver(ObjectMapper mapper) {
		WorkloadHandlerInstantiator handlerInstantiator = (WorkloadHandlerInstantiator) mapper.getSerializationConfig().getHandlerInstantiator();
		TokensIdResolver resolver = handlerInstantiator.getTokenIdResolver();
		return resolver;
	}
}
