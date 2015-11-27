package org.esbench.generator.document.simple.builder;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.esbench.generator.document.simple.JsonBuilder;
import org.esbench.generator.document.simple.JsonBuilderFactory;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.MetadataConstants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.javacrumbs.jsonunit.JsonAssert;

public class DateFieldBuilderTest extends AbstractFieldBuilderTest {

	@Test
	public void writeSingleValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		Instant from = Instant.parse("2015-10-01T10:00:00.00Z");
		Instant to = from.plus(10, ChronoUnit.MINUTES);
		DateFieldMetadata metadata = new DateFieldMetadata("date", 1, from, to, 5, ChronoUnit.MINUTES, MetadataConstants.DEFAULT_DATE_PATTERN);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"date\":\"2015-10-01T10:00:00\"}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"date\":\"2015-10-01T10:05:00\"}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"date\":\"2015-10-01T10:00:00\"}", createJson(builder, 10));
	}

	@Test
	public void writeArrayValue() throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		Instant from = Instant.parse("2015-10-01T10:00:00.00Z");
		Instant to = from.plus(20, ChronoUnit.MINUTES);
		DateFieldMetadata metadata = new DateFieldMetadata("date", 2, from, to, 5, ChronoUnit.MINUTES, MetadataConstants.DEFAULT_DATE_PATTERN);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals("{\"date\": [\"2015-10-01T10:00:00\", \"2015-10-01T10:05:00\"]}", createJson(builder, 0));
		JsonAssert.assertJsonEquals("{\"date\": [\"2015-10-01T10:10:00\", \"2015-10-01T10:15:00\"]}", createJson(builder, 1));
		JsonAssert.assertJsonEquals("{\"date\": [\"2015-10-01T10:00:00\", \"2015-10-01T10:05:00\"]}", createJson(builder, 2));
	}

	@DataProvider
	public Object[][] timezoneDataProvider() {
		Object[][] values = { { "yyyy-MM-dd'T'HH:mm:ss+01:00", "{\"date\": \"2015-10-01T10:00:00+01:00\"}" },
				{ "yyyy-MM-dd'T'HH:mm:ssZ", "{\"date\": \"2015-10-01T10:00:00+0000\"}" },
				{ "yyyy-MM-dd'T'HH:mm:ssO", "{\"date\": \"2015-10-01T10:00:00GMT\"}" } };
		return values;
	}

	@Test(dataProvider = "timezoneDataProvider")
	public void timezone(String format, String expected) throws IOException {
		JsonBuilderFactory factory = new JsonBuilderFactory();
		Instant from = Instant.parse("2015-10-01T10:00:00.00Z");
		Instant to = from.plus(20, ChronoUnit.MINUTES);
		DateFieldMetadata metadata = new DateFieldMetadata("date", 1, from, to, 5, ChronoUnit.MINUTES, format);
		JsonBuilder builder = factory.newInstance(metadata);

		JsonAssert.assertJsonEquals(expected, createJson(builder, 0));
	}
}
