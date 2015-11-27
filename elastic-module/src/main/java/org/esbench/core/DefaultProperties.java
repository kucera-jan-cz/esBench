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
	public static final DefaultProperties EMPTY = new DefaultProperties(new Properties(), new Properties());
	private final DefaultConversionService converter = new DefaultConversionService();
	private final Properties userDefined;
	private final Properties defaultProperties;
	private final Properties merged;

	public DefaultProperties(Properties props, Properties defaults) {
		this.userDefined = props;
		this.defaultProperties = defaults;
		this.merged = new Properties();
		this.merged.putAll(defaults);
		this.merged.putAll(props);
	}

	public String getProperty(String propertyName) {
		return merged.getProperty(propertyName);
	}

	public Integer getInt(String propertyName) {
		return get(propertyName, Integer.class);
	}

	public <T> T get(String propertyName, Class<T> type) {
		try {
			return get(propertyName, type, userDefined);
		} catch (IllegalArgumentException ex) {
			return get(propertyName, type, defaultProperties);
		}
	}

	public <T> T get(String propertyName, T defaultValue) {
		Class<T> type = (Class<T>) defaultValue.getClass();
		try {
			return get(propertyName, type, userDefined);
		} catch (IllegalArgumentException ex) {
			return defaultValue;
		}
	}

	private <T> T get(String propertyName, Class<T> type, Properties props) throws IllegalArgumentException {
		Validate.notEmpty(propertyName);
		Validate.notNull(type);
		String value = props.getProperty(propertyName);
		if(value == null) {
			throw new IllegalArgumentException();
		}
		try {
			return converter.convert(value, type);
		} catch (IllegalArgumentException | ConversionFailedException | ConverterNotFoundException ex) {
			LOGGER.warn("Invalid type for property: {}, value: {}, reason: {}", propertyName, value, ex.getMessage());
			throw new IllegalArgumentException();
		}
	}

	public boolean contains(String helpOpt) {
		return merged.contains(helpOpt);
	}
}
