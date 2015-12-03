package org.esbench.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.esbench.testng.UtilityClassValidator;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

public class ResourceUtilsTest {
	private static final String PROP_FILE_PATH = "resources-tests/prop_file.properties";
	private static final String TEXT_FILE_PATH = "resources-tests/text_file.txt";
	private static final String NON_EXISTING_PATH = "resources-tests/NOT_EXISTS.txt";

	@Test
	public void validate() {
		UtilityClassValidator.validate(ResourceUtils.class);
	}

	@Test
	public void asInputStream() throws IOException {
		InputStream ins = ResourceUtils.asInputStream(TEXT_FILE_PATH);
		assertNotNull(ins);
		String text = IOUtils.toString(ins, StandardCharsets.UTF_8);
		assertEquals(text, "esBench");

	}

	@Test
	public void asProperties() throws IOException {
		Properties props = ResourceUtils.asProperties(PROP_FILE_PATH);
		assertNotNull(props);
		assertEquals(props.size(), 1);
		assertEquals(props.get("name"), "esbench");
	}

	@Test
	public void asResource() throws IOException {
		Resource resource = ResourceUtils.asResource(TEXT_FILE_PATH);
		assertNotNull(resource);
		assertTrue(resource.isReadable());
	}

	@Test
	public void asString() throws IOException {
		String text = ResourceUtils.asString(TEXT_FILE_PATH);
		assertEquals(text, "esBench");
	}

	@Test(expectedExceptions = { IOException.class })
	public void notExists() throws IOException {
		ResourceUtils.asInputStream(NON_EXISTING_PATH);
	}
}
