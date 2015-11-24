package org.esbench.generator.document.simple;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.FieldFactory;
import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.meta.MultiFieldMetadata;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.generator.field.type.bool.BooleanFieldFactory;
import org.esbench.generator.field.type.date.DateFieldFactory;
import org.esbench.generator.field.type.ipv4.Ipv4FieldFactory;
import org.esbench.generator.field.type.numeric.ByteFieldFactory;
import org.esbench.generator.field.type.numeric.DoubleFieldFactory;
import org.esbench.generator.field.type.numeric.FloatFieldFactory;
import org.esbench.generator.field.type.numeric.IntegerFieldFactory;
import org.esbench.generator.field.type.numeric.LongFieldFactory;
import org.esbench.generator.field.type.numeric.ShortFieldFactory;
import org.esbench.generator.field.type.text.StringFieldFactory;

/**
 * Creates implementation of JSONBuilders based on given FieldMetadata.
 */
public class JsonBuilderFactory {
	/**
	 * Based on ginve metadata creates appropriate JsonBuilder.
	 * @param metadata for concrete type of document's field
	 * @return JsonBuilder which corresponds to given metadata
	 */
	public JsonBuilder newInstance(FieldMetadata metadata) {
		Validate.notNull(metadata);
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
		} else if(metadata instanceof MultiFieldMetadata) {
			return build((MultiFieldMetadata) metadata);
		} else {
			throw new IllegalStateException("Unknown metadata: " + metadata);
		}
	}

	private JsonBuilder build(MultiFieldMetadata metadata) {
		List<JsonBuilder> builders = new ArrayList<>();
		for(FieldMetadata fieldMeta : metadata.getFields()) {
			builders.add(newInstance(fieldMeta));
		}
		MultiFieldBuilder multiBuilder = new MultiFieldBuilder(builders);
		return multiBuilder;
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
		return NumericFieldBuilder.newInstance(meta, factory);
	}

	private FieldFactory<? extends Number> buildNumericFactory(NumericFieldMetadata meta) {
		switch(meta.getMetaType()) {
		case INTEGER:
			return new IntegerFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
		case LONG:
			return new LongFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
		case DOUBLE:
			return new DoubleFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
		case FLOAT:
			return new FloatFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
		case SHORT:
			return new ShortFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
		case BYTE:
			return new ByteFieldFactory(meta.getFrom(), meta.getTo(), meta.getStep());
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
		List<JsonBuilder> builders = new ArrayList<>();
		for(FieldMetadata meta : metadata) {
			builders.add(newInstance(meta));
		}
		return builders;
	}
}
