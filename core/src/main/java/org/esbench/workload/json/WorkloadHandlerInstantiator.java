package org.esbench.workload.json;

import org.esbench.generator.field.meta.TokenList;
import org.esbench.workload.json.databind.TokensIdGenerator;
import org.esbench.workload.json.databind.TokensIdResolver;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

/**
 * Jackson's handler Instantiator for handling token references.
 */
public class WorkloadHandlerInstantiator extends HandlerInstantiator {
	private final TokensIdResolver tokenResolver = new TokensIdResolver();
	private final TokensIdGenerator tokenIdGenerator = new TokensIdGenerator();

	@Override
	public JsonDeserializer<?> deserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> deserClass) {
		return null;
	}

	@Override
	public KeyDeserializer keyDeserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> keyDeserClass) {
		return null;
	}

	@Override
	public JsonSerializer<?> serializerInstance(SerializationConfig config, Annotated annotated, Class<?> serClass) {
		return null;
	}

	@Override
	public TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> config, Annotated annotated, Class<?> builderClass) {
		return null;
	}

	@Override
	public ObjectIdGenerator<?> objectIdGeneratorInstance(MapperConfig<?> config, Annotated annotated, Class<?> implClass) {
		if(annotated.getRawType().equals(TokenList.class)) {
			return tokenIdGenerator;
		} else {
			return null;
		}
	}

	@Override
	public ObjectIdResolver resolverIdGeneratorInstance(MapperConfig<?> config, Annotated annotated, Class<?> implClass) {
		if(annotated.getRawType().equals(TokenList.class)) {
			return tokenResolver;
		} else {
			return null;
		}
	}

	public TokensIdResolver getTokenIdResolver() {
		return tokenResolver;
	}

	public TokensIdGenerator getTokenIdGenerator() {
		return tokenIdGenerator;
	}

	@Override
	public TypeIdResolver typeIdResolverInstance(MapperConfig<?> config, Annotated annotated, Class<?> resolverClass) {
		return null;
	}

}
