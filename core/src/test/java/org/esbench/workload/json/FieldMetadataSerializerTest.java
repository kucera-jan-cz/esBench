package org.esbench.workload.json;

import java.io.IOException;
import java.util.Arrays;

import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.BooleanFieldMetadata.Type;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.testng.ResourcesUtils;
import org.esbench.workload.json.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FieldMetadataSerializerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldMetadataSerializerTest.class);

	private ObjectMapper mapper;

	@BeforeClass
	public void initMapper() {
		mapper = MapperFactory.initMapper();
	}

	@Test
	public void serializeString() throws JsonProcessingException {
		StringFieldMetadata meta = new StringFieldMetadata("fString", 2, 2, Arrays.asList("a", "b", "c"));
		LOGGER.info("{}", mapper.writeValueAsString(meta));
	}

	@Test
	public void serializeBoolean() throws JsonProcessingException {
		BooleanFieldMetadata meta = new BooleanFieldMetadata("fBoolean", 1, Type.ALWAYS_TRUE);
		LOGGER.info("{}", mapper.writeValueAsString(meta));
	}

	@Test
	public void deserialize() throws JsonParseException, JsonMappingException, IOException {
		String json = ResourcesUtils.loadAsString("configuration/test01.json");
		// mapper.enableDefaultTyping();
		// IndexTypeMetadata metadata = mapper.readValue(json, IndexTypeMetadata.class);
		IndexTypeMetadata metadata = mapper.readValue(json, IndexTypeMetadata.class);
		// LOGGER.info("{}", Arrays.toString(metadata));
		LOGGER.info("{}", metadata);
	}

	@Test
	public void deserializeString() throws JsonParseException, JsonMappingException, IOException {
		FieldMetadata booleanMeta = mapper.readValue("{\"type\":\"BOOLEAN\",\"tokens\":\"ALWAYS_TRUE\"}", FieldMetadata.class);
		LOGGER.info("{}", booleanMeta);
		FieldMetadata stringMeta = mapper.readValue("{\"type\":\"STRING\",\"array\":2,\"words\":2,\"tokens\":[\"x\",\"y\",\"z\"]}", FieldMetadata.class);
		LOGGER.info("{}", stringMeta);
	}
}
