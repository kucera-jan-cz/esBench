package org.esbench.workload.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.generator.field.meta.MultiFieldMetadata;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotationJsonTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationJsonTest.class);

	@Test
	public void serialize() throws IOException {
		ObjectMapper mapper = MapperFactory.initMapper();

		StringFieldMetadata stringField = new StringFieldMetadata("fString", 2, 2, Arrays.asList("A", "B", "C"));
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(stringField);
		LOGGER.info("\n{}", json);

		TokenListTestUtil.registerTokens(mapper, 0, Arrays.asList("A", "B", "C"));
		LOGGER.info("\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(MetadataConstants.DEFAULT_BOOLEAN_META));
		LOGGER.info("\n{}", mapper.readValue(json, FieldMetadata.class));
	}

	@Test
	public void serializeIndexType() throws IOException {
		ObjectMapper mapper = MapperFactory.initMapper();
		TokenListTestUtil.registerTokens(mapper, Collections.emptyList());
		TokenListTestUtil.registerTokens(mapper, Arrays.asList("A", "B", "C"));

		StringFieldMetadata stringField = new StringFieldMetadata("fString", 2, 2, MetadataConstants.DEFAULT_STRING_META.getTokens());
		NumericFieldMetadata integerField = new NumericFieldMetadata("fInteger", 1, MetaType.INTEGER, 0, 10, 1);
		NumericFieldMetadata longField = new NumericFieldMetadata("fLong", 1, MetaType.LONG, 0L, 1024L, 1L);
		IPv4FieldMetadata ipMeta = new IPv4FieldMetadata("fIp", 1, "192.168.0.0/21");

		StringFieldMetadata stringFieldA = new StringFieldMetadata("multiString", 1, 1, Arrays.asList("a", "b", "c"));
		StringFieldMetadata stringFieldB = new StringFieldMetadata("multiString", 2, 2, Arrays.asList("x", "y", "z"));

		MultiFieldMetadata multi = new MultiFieldMetadata("multiString", Arrays.asList(stringFieldA, stringFieldB));
		IndexTypeMetadata meta = new IndexTypeMetadata("NAME", "TYPE", Arrays.asList(stringField, MetadataConstants.DEFAULT_BOOLEAN_META, integerField,
				longField, ipMeta, MetadataConstants.DEFAULT_DATE_META, multi));
		String json = mapper.writer(new WorkloadPrettyPrinter()).writeValueAsString(meta);
		LOGGER.info("\n{}", json);
		IndexTypeMetadata metaCopy = mapper.readValue(json, IndexTypeMetadata.class);
		LOGGER.info("\n{}", metaCopy);

	}
}
