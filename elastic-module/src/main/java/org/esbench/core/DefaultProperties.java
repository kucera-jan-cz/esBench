package org.esbench.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Extended Property-like able to provide default values when user mistyped option value and with ability to class casting.
 */
public class DefaultProperties implements Serializable {
	private static final long serialVersionUID = 4598130622574689594L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProperties.class);
	private static final Properties EMPTY = new Properties();
	private transient DefaultConversionService converter;
	private final Properties userDefined;
	private final Properties defaultProperties;
	private final Properties merged;

	public DefaultProperties(String defaults) {
		this(EMPTY, loadOrEmpty(defaults));
	}

	public DefaultProperties(String props, String defaults) {
		this(loadOrEmpty(props), loadOrEmpty(defaults));
	}

	public DefaultProperties(Properties props, Properties defaults) {
		this.converter = new DefaultConversionService();
		this.userDefined = props;
		this.defaultProperties = defaults;
		this.merged = new Properties();
		this.merged.putAll(defaults);
		this.merged.putAll(props);
	}

	/**
	 * @param propertyName
	 * @return String value for given propertyName
	 */
	public String getProperty(String propertyName) {
		return merged.getProperty(propertyName);
	}

	/**
	 * @param propertyName
	 * @return Integer value for given propertyName
	 */
	public Integer getInt(String propertyName) {
		return get(propertyName, Integer.class);
	}

	/**
	 * @param propertyName
	 * @param type Class type which should be returned
	 * @return appropriate value for given propertyName class-casted to required type
	 */
	public <T> T get(String propertyName, Class<T> type) {
		try {
			return get(propertyName, type, userDefined);
		} catch (IllegalArgumentException ex) {
			LOGGER.trace("Failed to load {} from user defined properties: {}", propertyName, ex.getMessage());
			return get(propertyName, type, defaultProperties);
		}
	}

	/**
	 * @param propertyName
	 * @param defaultValue which should be returned if conversion failed or property is not presented
	 * @return value for given propertyName or defaultValue
	 */
	public <T> T get(String propertyName, T defaultValue) {
		Class<T> type = (Class<T>) defaultValue.getClass();
		try {
			return get(propertyName, type, userDefined);
		} catch (IllegalArgumentException ex) {
			LOGGER.trace("Failed to load {} from user defined properties: {}", propertyName, ex.getMessage());
			return defaultValue;
		}
	}

	/**
	 * @param propertyName
	 * @return true when propertyName is presented, false otherwise
	 */
	public boolean contains(String propertyName) {
		return merged.contains(propertyName);
	}

	/**
	 * Print list of properties to given writer.
	 * @param writer to which properties are written
	 */
	public void list(PrintWriter writer) {
		List<String> sortedKeys = merged.keySet().stream().map(k -> String.valueOf(k)).sorted().collect(Collectors.toList());
		for(String key : sortedKeys) {
			writer.print(key);
			writer.print('=');
			writer.println(merged.get(key));
		}
	}

	/**
	 * Merge given properties into new one where first DefaultProperties has overloading precedence.
	 * @param fst DefaultProperties which overrules snd
	 * @param snd DefaultProperties which serves as baseline
	 * @return merged DefaultProperties
	 */
	public static DefaultProperties merge(DefaultProperties fst, DefaultProperties snd) {
		Properties newDefaults = new Properties();
		newDefaults.putAll(snd.defaultProperties);
		newDefaults.putAll(fst.defaultProperties);
		Properties newUserDefaults = new Properties();
		newUserDefaults.putAll(snd.userDefined);
		newUserDefaults.putAll(fst.userDefined);
		return new DefaultProperties(newUserDefaults, newDefaults);
	}

	private static Properties loadOrEmpty(String resourcePath) {
		try {
			return ResourceUtils.asProperties(resourcePath);
		} catch (IOException ex) {
			LOGGER.warn("Can't load properties from {}, using empty one", resourcePath, ex);
			return EMPTY;
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

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		converter = new DefaultConversionService();
	}

}
