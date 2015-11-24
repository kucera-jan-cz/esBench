package org.esbench.workload.json;

import java.io.IOException;

import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.testng.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexTypeMetadataDeserializerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexTypeMetadataDeserializerTest.class);
	private ObjectMapper mapper;

	@BeforeClass
	public void initMapper() {
		mapper = MapperFactory.initMapper();
	}

	@Test
	public void deserialize() throws IOException {
		String json = ResourcesUtils.loadAsString("configuration/test02.json");
		IndexTypeMetadata meta = mapper.readValue(json, IndexTypeMetadata.class);
		LOGGER.info("{}", meta);
	}
}
