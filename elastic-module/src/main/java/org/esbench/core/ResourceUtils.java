package org.esbench.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Utility class for loading Spring Resource ({@link org.springframework.core.io.Resource} and it's derivates from classpath and filesystem.
 */
public final class ResourceUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

	private ResourceUtils() {

	}

	/**
	 * Load Resource based on given location and return it as stream.
	 * @param location representing URI pointing to resource.
	 * @return InputStream from resource
	 * @throws IOException when resource can't be retrieved
	 */
	public static InputStream asInputStream(String location) throws IOException {
		LOGGER.debug("Loading resource {}", location);
		Resource resource = asResource(location);
		return resource.getInputStream();
	}

	/**
	 * Load Resource based on given location and return it as Properties.
	 * @param location representing URI pointing to resource.
	 * @return Properties created from resource
	 * @throws IOException when resource can't be retrieved or property loading failed
	 */
	public static Properties asProperties(String location) throws IOException {
		Resource resource = asResource(location);
		return PropertiesLoaderUtils.loadProperties(resource);
	}

	/**
	 * Load Resource based on given location and transform it to String using defined encoding.
	 * @param location representing URI pointing to resource.
	 * @param encoding to use for building String
	 * @return String representation of resource
	 * @throws IOException when resource can't be retrieved
	 */
	public static String asString(String location, Charset encoding) throws IOException {
		Validate.notNull(encoding);
		StringWriter writer = new StringWriter();
		try(InputStream is = asInputStream(location)) {
			IOUtils.copy(is, writer, encoding);
		}
		String text = writer.toString();
		return text;
	}

	/**
	 * Load Resource based on given location and transform it to String using UTF-8 encoding.
	 * @param location representing URI pointing to resource.
	 * @return String representation of resource
	 * @throws IOException when resource can't be retrieved
	 */
	public static String asString(String location) throws IOException {
		return asString(location, StandardCharsets.UTF_8);
	}

	/**
	 * Load Resource for given location.
	 * @param location representing URI pointing to resource.
	 * @return Resource entity based on given location 
	 * @throws IOException when resource can't be loaded
	 */
	public static Resource asResource(String location) throws IOException {
		Validate.notEmpty(location);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource(location);
		if(!resource.isReadable()) {
			throw new IOException("Can't find resource at " + location);
		}
		return resource;
	}
}
