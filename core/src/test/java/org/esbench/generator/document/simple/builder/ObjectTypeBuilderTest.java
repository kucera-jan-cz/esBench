package org.esbench.generator.document.simple.builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.document.simple.JsonBuilderFactory;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.esbench.generator.field.meta.ObjectTypeMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.testng.ResourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.javacrumbs.jsonunit.JsonAssert;

public class ObjectTypeBuilderTest extends AbstractFieldBuilderTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectTypeBuilderTest.class);

	private String expectedJson;
	JsonNode rootNode;

	@BeforeClass
	public void loadExpected() throws IOException {
		expectedJson = ResourcesUtils.loadAsString("documents/doc01.json");
		ObjectMapper m = new ObjectMapper();
		rootNode = m.readTree(expectedJson);
	}

	@Test
	public void writeSingleValue() throws IOException {
		int tokenPerValue = 2;
		List<String> tokens = Arrays.asList("A", "B", "C", "D");
		StringFieldMetadata smeta = new StringFieldMetadata("text", 1, tokenPerValue, tokens);
		NumericFieldMetadata imeta = new NumericFieldMetadata("num", 1, MetaType.INTEGER, 0, 3, 1);
		ObjectTypeMetadata meta = new ObjectTypeMetadata("obj", Arrays.asList(smeta, imeta));
		JsonBuilderFactory factory = new JsonBuilderFactory();
		JsonBuilder builder = factory.newInstance(meta);

		LOGGER.info("JSON: {}", createJson(builder, 0));
		JsonAssert.assertJsonEquals(rootNode.get("0"), createJson(builder, 0));
		JsonAssert.assertJsonEquals(rootNode.get("1"), createJson(builder, 1));
	}

	@Test
	public void writeArrayValue() throws IOException {
		int tokenPerValue = 2;
		List<String> tokens = Arrays.asList("A", "B", "C", "D");
		StringFieldMetadata smeta = new StringFieldMetadata("text", 1, tokenPerValue, tokens);
		NumericFieldMetadata imeta = new NumericFieldMetadata("num", 1, MetaType.INTEGER, 0, 3, 1);
		ObjectTypeMetadata meta = new ObjectTypeMetadata("obj", 2, Arrays.asList(smeta, imeta));
		JsonBuilderFactory factory = new JsonBuilderFactory();
		JsonBuilder builder = factory.newInstance(meta);

		LOGGER.info("JSON: {}", createJson(builder, 0));
		JsonAssert.assertJsonEquals(rootNode.get("a0"), createJson(builder, 0));
		JsonAssert.assertJsonEquals(rootNode.get("a1"), createJson(builder, 1));
	}
}
