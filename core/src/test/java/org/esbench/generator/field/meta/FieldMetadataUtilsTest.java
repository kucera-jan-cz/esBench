package org.esbench.generator.field.meta;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class FieldMetadataUtilsTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldMetadataUtilsTest.class);

	@Test
	public void updateObject() throws InvocationTargetException, IllegalAccessException {
		StringFieldMetadata toMerge = new StringFieldMetadata();
		FieldMetadataUtils.updateObject(MetadataConstants.DEFAULT_STRING_META, toMerge);
		LOGGER.info("{}", toMerge);
	}

	@Test
	public void merge() throws InvocationTargetException, IllegalAccessException {
		StringFieldMetadata custom = new StringFieldMetadata();
		List<String> tokens = Arrays.asList("a", "b");
		String fullPath = "fString.new";
		custom.setFullPath(fullPath);
		custom.setTokens(tokens);
		StringFieldMetadata updated = FieldMetadataUtils.merge(custom, MetadataConstants.DEFAULT_STRING_META);
		LOGGER.info("{}", updated);
		assertEquals(updated.getFullPath(), fullPath);
		assertEquals(updated.getTokens(), tokens);
		// Deep copy is not required
		assertTrue(updated.getTokens() == tokens);
	}

	@Test
	public void diff() throws InvocationTargetException, IllegalAccessException {
		StringFieldMetadata custom = new StringFieldMetadata();
		List<String> tokens = Arrays.asList("a", "b");
		String fullPath = "fString.new";
		custom.setFullPath(fullPath);
		custom.setTokens(tokens);
		StringFieldMetadata updated = FieldMetadataUtils.diff(custom, MetadataConstants.DEFAULT_STRING_META);
		LOGGER.info("{}", updated);
		assertEquals(updated.getFullPath(), fullPath);
		assertEquals(updated.getTokens(), tokens);
		assertNull(updated.getTokensPerValue());
		assertNull(updated.getValuesPerDocument());
	}

	@Test
	public void diffObject() throws InvocationTargetException, IllegalAccessException {
		ObjectTypeMetadata defaultMeta = MetadataConstants.DEFAULT_OBJECT_META;
		ObjectTypeMetadata field = new ObjectTypeMetadata("obj", Arrays.asList(MetadataConstants.DEFAULT_STRING_META));
		ObjectTypeMetadata diff = FieldMetadataUtils.diff(field, defaultMeta);
		assertEquals(diff.getInnerMetadata().size(), 1);
	}
}
