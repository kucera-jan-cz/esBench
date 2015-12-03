package org.esbench.generator.document.simple.builder;

import java.io.IOException;

import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.document.simple.JsonBuilderFactory;
import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.testng.annotations.Test;

import net.javacrumbs.jsonunit.JsonAssert;

public class BooleanFieldBuilderTest extends AbstractFieldBuilderTest {

	@Test
	public void writeSingleValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		BooleanFieldMetadata metadata = new BooleanFieldMetadata("boolean");
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"boolean\":true}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"boolean\":false}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"boolean\":true}", createJson(builder, 2));
	}

	@Test
	public void writeArrayValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		BooleanFieldMetadata metadata = new BooleanFieldMetadata("boolean", 2, BooleanFieldMetadata.Type.ALWAYS_TRUE);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"boolean\": [true, true]}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"boolean\": [true, true]}", createJson(builder, 1));
	}
}
