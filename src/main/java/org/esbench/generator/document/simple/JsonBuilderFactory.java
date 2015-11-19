package org.esbench.generator.document.simple;

import java.util.ArrayList;
import java.util.List;

import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.generator.field.type.bool.BooleanFieldFactory;
import org.esbench.generator.field.type.date.DateFieldFactory;
import org.esbench.generator.field.type.ipv4.Ipv4FieldFactory;
import org.esbench.generator.field.type.numeric.IntegerFieldFactory;
import org.esbench.generator.field.type.numeric.LongFieldFactory;
import org.esbench.generator.field.type.text.StringFieldFactory;

public class JsonBuilderFactory {

	public JsonBuilder newInstance(FieldMetadata metadata) {
		if(metadata instanceof StringFieldMetadata) {
			return build((StringFieldMetadata) metadata);
		} else if(metadata instanceof BooleanFieldMetadata) {
			return build((BooleanFieldMetadata) metadata);
		} else if(metadata instanceof DateFieldMetadata) {
			return build((DateFieldMetadata) metadata);
		} else if(metadata instanceof ObjectTypeMetadata) {
			return build((ObjectTypeMetadata) metadata);
		} else if(metadata instanceof IPv4FieldMetadata) {
			return build((IPv4FieldMetadata) metadata);
		} else if(metadata instanceof NumericFieldMetadata) {
			return build((NumericFieldMetadata) metadata);
		} else {
			throw new IllegalStateException("Unknown metadata: " + metadata);
		}
	}

	private JsonBuilder build(IPv4FieldMetadata meta) {
		FieldFactory<String> factory = new Ipv4FieldFactory(meta.getCidrAddress());
		return new StringFieldBuilder(meta, factory);
	}

	private JsonBuilder build(BooleanFieldMetadata metadata) {
		FieldFactory<Boolean> factory = BooleanFieldFactory.valueOf(metadata.getBooleanType().name());
		BooleanFieldBuilder builder = new BooleanFieldBuilder(metadata, factory);
		return builder;
	}

	private ObjectTypeBuilder build(ObjectTypeMetadata meta) {
		List<JsonBuilder> builders = build(meta.getInnerMetadata());
		ObjectTypeBuilder builder = new ObjectTypeBuilder(meta, builders);
		return builder;
	}

	private StringFieldBuilder build(StringFieldMetadata meta) {
		StringFieldFactory factory = new StringFieldFactory(meta.getTokensPerValue(), meta.getTokens());
		return new StringFieldBuilder(meta, factory);
	}

	private JsonBuilder build(NumericFieldMetadata meta) {
		FieldFactory<? extends Number> factory = buildNumericFactory(meta);
		if(meta.getValuesPerDocument() < 2) {
			return NumericFieldBuilder.newInstance(meta, factory);
		} else {
			return NumericArrayFieldBuilder.newInstance(meta, factory);
		}
	}

	private FieldFactory<? extends Number> buildNumericFactory(NumericFieldMetadata meta) {
		switch(meta.getMetaType()) {
		case INTEGER:
			return new IntegerFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
		case LONG:
			return new LongFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
		default:
			throw new IllegalArgumentException("Unsupported type: " + meta.getMetaType());
		}
	}

	private DateFieldBuilder build(DateFieldMetadata meta) {
		long units = meta.getFrom().until(meta.getTo(), meta.getUnit());
		int modulo = (int) (units / meta.getStep());
		DateFieldFactory factory = new DateFieldFactory(meta.getFrom(), meta.getStep(), meta.getUnit(), modulo);
		return new DateFieldBuilder(meta, factory);
	}

	private List<JsonBuilder> build(List<? extends FieldMetadata> metadata) {
		// @TODO - implement dedup here
		List<JsonBuilder> builders = new ArrayList<>();
		for(FieldMetadata meta : metadata) {
			builders.add(newInstance(meta));
		}
		return builders;
	}
}
