package org.esbench.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public final class ResourceUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

	private ResourceUtils() {

	}

	public static InputStream asInputStream(String location) throws IOException {
		LOGGER.debug("Loading resource {}", location);
		Resource resource = asResource(location);
		return resource.getInputStream();
	}

	public static Properties asProperties(String location) throws IOException {
		Resource resource = asResource(location);
		return PropertiesLoaderUtils.loadProperties(resource);
	}

	public static String asString(String location, Charset encoding) throws IOException {
		Validate.notNull(encoding);
		StringWriter writer = new StringWriter();
		try(InputStream is = asInputStream(location)) {
			IOUtils.copy(is, writer, encoding);
		}
		String text = writer.toString();
		return text;
	}

	public static Resource asResource(String location) throws FileNotFoundException {
		Validate.notEmpty(location);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource(location);
		if(!resource.isReadable()) {
			throw new FileNotFoundException("Can't find resource at " + location);
		}
		return resource;
	}
}
