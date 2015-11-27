package org.esbench.generator.document.simple.builder;

import java.io.IOException;

import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.document.simple.JsonBuilderFactory;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.testng.annotations.Test;

import net.javacrumbs.jsonunit.JsonAssert;

public class IntegerFieldBuilderTest extends AbstractFieldBuilderTest {
	@Test
	public void writeSingleValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		NumericFieldMetadata metadata = new NumericFieldMetadata("int", 1, MetaType.INTEGER, 0, 10, 2);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"int\":0}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"int\":2}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"int\":8}", createJson(builder, 4));
		JsonAssert.assertJsonEquals("{\"int\":10}", createJson(builder, 5));
		JsonAssert.assertJsonEquals("{\"int\":0}", createJson(builder, 6));
	}

	@Test
	public void writeArrayValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		NumericFieldMetadata metadata = new NumericFieldMetadata("int", 2, MetaType.INTEGER, 0, 10, 3);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"int\": [0, 3]}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"int\": [6, 9]}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"int\": [0, 3]}", createJson(builder, 2));
	}

	@Test
	public void negativeToZero() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		NumericFieldMetadata metadata = new NumericFieldMetadata("int", 1, MetaType.INTEGER, -3, 0, 1);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"int\": -3}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"int\": -2}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"int\": -1}", createJson(builder, 2));
		JsonAssert.assertJsonEquals("{\"int\": 0}", createJson(builder, 3));
		JsonAssert.assertJsonEquals("{\"int\": -3}", createJson(builder, 4));
	}

	@Test
	public void negativeToPositive() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		NumericFieldMetadata metadata = new NumericFieldMetadata("int", 1, MetaType.INTEGER, -2, 2, 1);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"int\": -2}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"int\": -1}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"int\": 0}", createJson(builder, 2));
		JsonAssert.assertJsonEquals("{\"int\": 1}", createJson(builder, 3));
		JsonAssert.assertJsonEquals("{\"int\": 2}", createJson(builder, 4));
		JsonAssert.assertJsonEquals("{\"int\": -2}", createJson(builder, 5));
	}
}
