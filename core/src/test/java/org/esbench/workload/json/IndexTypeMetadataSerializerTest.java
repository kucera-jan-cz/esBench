package org.esbench.workload.json;

import java.util.Arrays;
import java.util.List;

import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.BooleanFieldMetadata.Type;
import org.esbench.workload.json.MapperFactory;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexTypeMetadataSerializerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexTypeMetadataSerializerTest.class);

	private ObjectMapper mapper = MapperFactory.initMapper();

	@Test
	public void serialize() throws JsonProcessingException {
		List<FieldMetadata> fields = Arrays.asList(new StringFieldMetadata("a", 1, 1, Arrays.asList("a", "b", "c")),
				new BooleanFieldMetadata("fBoolean", 1, Type.ALWAYS_TRUE));
		IndexTypeMetadata indexType = new IndexTypeMetadata("i", "t", fields);
		LOGGER.info("{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(indexType));
	}
}
