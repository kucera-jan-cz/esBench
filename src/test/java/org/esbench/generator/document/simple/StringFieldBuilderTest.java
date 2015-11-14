package org.esbench.generator.document.simple;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.esbench.generator.field.meta.StringFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import net.javacrumbs.jsonunit.JsonAssert;

public class StringFieldBuilderTest extends AbstractFieldBuilderTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(StringFieldBuilderTest.class);

	@Test
	public void writeSingleValue() throws IOException {
		int tokenPerValue = 2;
		List<String> tokens = Arrays.asList("A", "B", "C", "D");
		StringFieldMetadata meta = new StringFieldMetadata("text", 1, tokenPerValue, tokens);
		JsonBuilderFactory factory = new JsonBuilderFactory();
		JsonBuilder builder = factory.newInstance(meta);

		LOGGER.info("JSON: {}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"text\":\"A B\"}", createJson(builder, 0));
	}

	@Test
	public void writeArrayValue() throws IOException {
		int tokenPerValue = 2;
		List<String> tokens = Arrays.asList("A", "B", "C", "D");
		StringFieldMetadata meta = new StringFieldMetadata("text", 2, tokenPerValue, tokens);
		JsonBuilderFactory factory = new JsonBuilderFactory();
		JsonBuilder builder = factory.newInstance(meta);

		LOGGER.info("JSON: {}", createJson(builder, 0));

		JsonAssert.assertJsonEquals("{\"text\":[\"A B\",\"C D\"]}", createJson(builder, 0));
	}
}
