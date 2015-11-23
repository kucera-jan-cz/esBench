package org.esbench.core;

import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.support.DefaultConversionService;

public class DefaultProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProperties.class);
	public static final DefaultProperties EMPTY = new DefaultProperties(new Properties());
	private final DefaultConversionService converter = new DefaultConversionService();
	private final Properties properties;

	public DefaultProperties(Properties props) {
		this.properties = props;
	}

	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

	public <T> T get(String propertyName, Class<T> type) {
		Validate.notEmpty(propertyName);
		Validate.notNull(type);

		String value = properties.getProperty(propertyName);
		if(value == null) {
			throw new IllegalArgumentException("Property " + propertyName + "not defined");
		}
		try {
			return converter.convert(value, type);
		} catch (ConversionFailedException | ConverterNotFoundException ex) {
			LOGGER.warn("Invalid type for property: {}, value: {}, reason: {}", propertyName, value, ex.getMessage());
			throw new IllegalArgumentException("Invalid type for property: " + propertyName);
		}
	}

	public <T> T get(String propertyName, T defaultValue) {
		Validate.notEmpty(propertyName);
		Validate.notNull(defaultValue);

		String value = properties.getProperty(propertyName);
		if(value == null) {
			return defaultValue;
		}
		try {
			Class<T> type = (Class<T>) defaultValue.getClass();
			return converter.convert(value, type);
		} catch (IllegalArgumentException | ConversionFailedException | ConverterNotFoundException ex) {
			LOGGER.warn("Invalid type for property: {}, value: {}, reason: {}", propertyName, value, ex.getMessage());
			return defaultValue;
		}
	}
}
