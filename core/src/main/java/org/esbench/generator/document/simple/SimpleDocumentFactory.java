package org.esbench.generator.document.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.document.DocumentFactory;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 *	Basic JSON document factory which creates JSON documents as String. 
 */
public class SimpleDocumentFactory implements DocumentFactory<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDocumentFactory.class);
	private final JsonFactory factory = new JsonFactory();
	private final List<JsonBuilder> builders = new ArrayList<>();

	/**
	 * Creates factory instances using given {@link IndexTypeMetadata}.
	 * @param indexTypeMetadata metadata based on which factory create JSON documents 
	 */
	public SimpleDocumentFactory(IndexTypeMetadata indexTypeMetadata) {
		this(indexTypeMetadata, JsonBuilderFactory.DEFAULT_CACHE_LIMIT);
	}

	/**
	 * Creates factory instances using given {@link IndexTypeMetadata} and specifying caching limit.
	 * @param indexTypeMetadata metadata based on which factory create JSON documents 
	 * @param cachingLimit defines field value caching limit
	 */
	public SimpleDocumentFactory(IndexTypeMetadata indexTypeMetadata, int cachingLimit) {
		Validate.notNull(indexTypeMetadata);
		this.factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
		builders.addAll(initBuilders(indexTypeMetadata.getFields(), cachingLimit));
	}

	@Override
	public String newInstance(int instanceId) {
		try(StringWriter writer = new StringWriter(); JsonGenerator generator = factory.createGenerator(writer);) {
			generator.writeStartObject();

			for(JsonBuilder builder : builders) {
				builder.write(generator, instanceId);
			}

			generator.writeEndObject();
			generator.flush();
			return writer.toString();
		} catch (IOException e) {
			// @TODO - consider better exception throw
			throw new IllegalStateException(e);
		}
	}

	private List<JsonBuilder> initBuilders(List<? extends FieldMetadata> metadata, int cachingLimit) {
		List<JsonBuilder> builders = new ArrayList<>();
		JsonBuilderFactory jsonBuilderFactory = new JsonBuilderFactory();
		jsonBuilderFactory.setFieldCacheLimit(cachingLimit);
		for(FieldMetadata meta : metadata) {
			JsonBuilder jsonBuilder = jsonBuilderFactory.newInstance(meta);
			LOGGER.debug("Building {} for {}", jsonBuilder, meta.getFullPath());
			builders.add(jsonBuilder);
		}
		return builders;
	}

}
