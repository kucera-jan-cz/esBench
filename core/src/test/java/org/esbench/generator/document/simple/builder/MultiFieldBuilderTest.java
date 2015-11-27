package org.esbench.generator.document.simple.builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.document.simple.JsonBuilderFactory;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MultiFieldMetadata;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import net.javacrumbs.jsonunit.JsonAssert;

public class MultiFieldBuilderTest extends AbstractFieldBuilderTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiFieldBuilderTest.class);
	String fieldName = "text";
	List<String> tokensA = Arrays.asList("A", "B", "C", "D");
	List<String> tokensX = Arrays.asList("x", "y", "z");

	@Test
	public void writeSingleValue() throws IOException {
		StringFieldMetadata stringFieldA = new StringFieldMetadata(fieldName, 1, 1, tokensA);
		StringFieldMetadata stringFieldB = new StringFieldMetadata(fieldName, 2, 1, tokensX);

		MultiFieldMetadata multi = new MultiFieldMetadata(fieldName, Arrays.asList(stringFieldA, stringFieldB));

		JsonBuilderFactory factory = new JsonBuilderFactory();
		JsonBuilder builder = factory.newInstance(multi);

		LOGGER.info("JSON: {}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"text\":\"A\"}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"text\":[\"x\", \"y\"]}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"text\":\"B\"}", createJson(builder, 2));
		JsonAssert.assertJsonEquals("{\"text\":[\"z\", \"x\"]}", createJson(builder, 3));
	}

	@Test
	public void writeArrayValue() throws IOException {
		StringFieldMetadata stringFieldA = new StringFieldMetadata(fieldName, 1, 2, tokensA);
		StringFieldMetadata stringFieldB = new StringFieldMetadata(fieldName, 2, 2, tokensX);

		MultiFieldMetadata multi = new MultiFieldMetadata(fieldName, Arrays.asList(stringFieldA, stringFieldB));

		JsonBuilderFactory factory = new JsonBuilderFactory();
		JsonBuilder builder = factory.newInstance(multi);

		LOGGER.info("JSON: {}", createJson(builder, 0));

		JsonAssert.assertJsonEquals("{\"text\": \"A B\"}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"text\":[\"x y\",\"z x\"]}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"text\": \"C D\"}", createJson(builder, 2));
		JsonAssert.assertJsonEquals("{\"text\":[\"y z\",\"x y\"]}", createJson(builder, 3));
	}

	@Test
	public void writeMultiTypeValue() throws IOException {

		StringFieldMetadata stringFieldB = new StringFieldMetadata(fieldName, 2, 2, tokensX);
		NumericFieldMetadata intField = new NumericFieldMetadata(fieldName, 2, MetaType.INTEGER, 0, 10, 5);

		MultiFieldMetadata multi = new MultiFieldMetadata(fieldName, Arrays.asList(intField, stringFieldB));

		JsonBuilderFactory factory = new JsonBuilderFactory();
		JsonBuilder builder = factory.newInstance(multi);

		LOGGER.info("JSON: {}", createJson(builder, 0));

		JsonAssert.assertJsonEquals("{\"text\": [0, 5]}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"text\":[\"x y\",\"z x\"]}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"text\": [10, 0]}", createJson(builder, 2));
		JsonAssert.assertJsonEquals("{\"text\":[\"y z\",\"x y\"]}", createJson(builder, 3));
	}
}
