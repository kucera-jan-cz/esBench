package org.esbench.config.json;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.esbench.config.Configuration;
import org.esbench.config.ConfigurationConstants;
import org.esbench.generator.field.meta.BooleanFieldMetadata;
import org.esbench.generator.field.meta.BooleanFieldMetadata.Type;
import org.esbench.generator.field.meta.DateFieldMetadata;
import org.esbench.generator.field.meta.FieldMetadata;
import org.esbench.generator.field.meta.IndexTypeMetadata;
import org.esbench.generator.field.meta.MetaType;
import org.esbench.generator.field.meta.MetadataConstants;
import org.esbench.generator.field.meta.NumericFieldMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ConfigurationParserTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationParserTest.class);

	StringFieldMetadata titleA = new StringFieldMetadata("title", 3, 2, Arrays.asList("a", "b", "c"));
	// @TODO - implement this post-poned feature
	// StringFieldMetadata titleX = new StringFieldMetadata("title", 1, 3, Arrays.asList("x", "y", "z"));
	StringFieldMetadata type = new StringFieldMetadata("type", 1, 3, Arrays.asList("x", "y", "z"));
	BooleanFieldMetadata controversial = new BooleanFieldMetadata("controversial", 1, Type.ALWAYS_TRUE);
	BooleanFieldMetadata latest = new BooleanFieldMetadata("latest", 1, Type.TICK_TOCK);
	NumericFieldMetadata pageViews = new NumericFieldMetadata("page_views", 1, MetaType.INTEGER, 50, 100, 5);
	Instant from = Instant.parse("2015-01-01T00:00:00Z");
	Instant to = Instant.parse("2015-12-31T23:59:59Z");
	DateFieldMetadata lastUpdate = new DateFieldMetadata("lastUpdate", 1, from, to, 5, ChronoUnit.MINUTES, MetadataConstants.DEFAULT_DATE_PATTERN);

	// @TODO - implement this post-poned feature
	// NumericFieldMetadata authorsLikes = new NumericFieldMetadata("authors.likes", 1, MetaType.LONG, 50L, 1_000_000L, 5L);
	// StringFieldMetadata authorsReviewLinks = new StringFieldMetadata("authors.reviews", 2, 1, Arrays.asList("L1", "L2", "L3"));
	// ObjectTypeMetadata authors = new ObjectTypeMetadata("authors", Arrays.asList(authorsLikes, authorsReviewLinks));
	List<FieldMetadata> fields = Arrays.asList(titleA, type, controversial, latest, pageViews, lastUpdate);

	@Test
	public void parseConfiguration01() throws JsonProcessingException, IOException {
		ConfigurationParser parser = new ConfigurationParser();
		Configuration config = parser.parse("configuration/config01.json");
		assertNotNull(config);
		assertEquals(config.getIndiceTypes().size(), 1);
		IndexTypeMetadata meta = config.getIndiceTypes().get(0);
		assertEquals(meta.getIndexName(), "NAME");
		assertEquals(meta.getTypeName(), "TYPE");

		ReflectionAssert.assertReflectionEquals(fields, meta.getFields(), ReflectionComparatorMode.LENIENT_ORDER);
	}

	@Test
	public void parseConfiguration02() throws IOException {
		ConfigurationParser parser = new ConfigurationParser();
		Configuration config = parser.parse("configuration/config02.json");
		assertNotNull(config);
		assertEquals(config.getVersion(), ConfigurationConstants.CURRENT_VERSION);
	}

	@Test
	public void parseIndexType() throws IOException {
		ConfigurationParser parser = new ConfigurationParser();
		IndexTypeMetadata indexType = new IndexTypeMetadata("INDEX", "TYPE", fields);
		StringWriter writer = new StringWriter();
		Configuration config = new Configuration(ConfigurationConstants.CURRENT_VERSION, MetadataConstants.DEFAULT_META_BY_TYPE, Arrays.asList(indexType));
		parser.parse(writer, config);
		String json = writer.toString();
		LOGGER.info("\n{}", json);
		StringReader reader = new StringReader(json);
		Configuration parsedConfig = parser.parse(reader);
		ReflectionAssert.assertReflectionEquals(config, parsedConfig, ReflectionComparatorMode.LENIENT_ORDER);
	}
}
