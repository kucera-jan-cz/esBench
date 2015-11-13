package org.esbench.generator.document.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.esbench.generator.document.DocumentFactory;
import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.generator.field.type.bool.BooleanFieldFactory;
import org.esbench.generator.field.type.date.DateFieldFactory;
import org.esbench.generator.field.type.ipv4.Ipv4FieldFactory;
import org.esbench.generator.field.type.text.StringFieldFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class SimpleDocumentFactory implements DocumentFactory<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDocumentFactory.class);
	private final JsonFactory factory = new JsonFactory();
	private List<JsonBuilder> builders = new ArrayList<>();

	public SimpleDocumentFactory(IndexTypeMetadata indexTypeMetadata) {
		this.factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
		builders.addAll(initBuilders(indexTypeMetadata.getFields()));
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
			throw new IllegalStateException(e);
		}

	}

	private List<JsonBuilder> initBuilders(List<? extends FieldMetadata> metadata) {
		List<JsonBuilder> builders = new ArrayList<>();
		for(FieldMetadata meta : metadata) {
			builders.add(createBuilder(meta));
		}
		return builders;
	}

	// @TODO - consider split functionality to two classes
	public JsonBuilder createBuilder(FieldMetadata metadata) {
		if(metadata instanceof StringFieldMetadata) {
			return newInstance((StringFieldMetadata) metadata);
		} else if(metadata instanceof BooleanFieldMetadata) {
			return newInstance((BooleanFieldMetadata) metadata);
		} else if(metadata instanceof DateFieldMetadata) {
			return newInstance((DateFieldMetadata) metadata);
		} else if(metadata instanceof ObjectTypeMetadata) {
			return newInstance((ObjectTypeMetadata) metadata);
		} else if(metadata instanceof IPv4FieldMetadata) {
			return newInstance((IPv4FieldMetadata) metadata);
		} else {
			throw new IllegalStateException("Unknown metadata: " + metadata);
		}
	}

	private JsonBuilder newInstance(IPv4FieldMetadata meta) {
		FieldFactory<String> factory = new Ipv4FieldFactory(meta.getCidrAddress());
		return new StringFieldBuilder(meta, factory);
	}

	private JsonBuilder newInstance(BooleanFieldMetadata metadata) {
		FieldFactory<Boolean> factory = BooleanFieldFactory.valueOf(metadata.getType().name());
		BooleanFieldBuilder builder = new BooleanFieldBuilder(metadata, factory);
		return builder;
	}

	private ObjectTypeBuilder newInstance(ObjectTypeMetadata meta) {
		List<JsonBuilder> builders = initBuilders(meta.getInnerMetadata());
		ObjectTypeBuilder builder = new ObjectTypeBuilder(meta, builders);
		return builder;
	}

	private StringFieldBuilder newInstance(StringFieldMetadata meta) {
		StringFieldFactory factory = new StringFieldFactory(meta.getTokensPerValue(), meta.getTokens());
		return new StringFieldBuilder(meta, factory);
	}

	private DateFieldBuilder newInstance(DateFieldMetadata meta) {
		long units = meta.getFrom().until(meta.getTo(), meta.getUnit());
		int modulo = (int) (units / meta.getStep());
		DateFieldFactory factory = new DateFieldFactory(meta.getFrom(), meta.getStep(), meta.getUnit(), modulo);
		return new DateFieldBuilder(meta, factory);
	}
}
