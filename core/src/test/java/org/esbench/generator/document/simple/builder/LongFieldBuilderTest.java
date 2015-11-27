package org.esbench.generator.document.simple.builder;

import java.io.IOException;

import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.document.simple.JsonBuilderFactory;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.testng.annotations.Test;

import net.javacrumbs.jsonunit.JsonAssert;

public class LongFieldBuilderTest extends AbstractFieldBuilderTest {
	@Test
	public void writeSingleValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		NumericFieldMetadata metadata = new NumericFieldMetadata("long", 1, MetaType.LONG, Integer.MAX_VALUE, Integer.MAX_VALUE + 4L, 2L);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"long\":2147483647}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"long\":2147483649}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"long\":2147483651}", createJson(builder, 2));
		JsonAssert.assertJsonEquals("{\"long\":2147483647}", createJson(builder, 3));
	}

	@Test
	public void writeArrayValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		NumericFieldMetadata metadata = new NumericFieldMetadata("long", 2, MetaType.LONG, Integer.MAX_VALUE, Integer.MAX_VALUE + 4L, 2L);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"long\": [2147483647, 2147483649]}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"long\": [2147483651, 2147483647]}", createJson(builder, 1));
	}
}
