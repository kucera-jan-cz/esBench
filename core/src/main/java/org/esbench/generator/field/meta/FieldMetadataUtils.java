package org.esbench.generator.field.meta;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FieldMetadataUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldMetadataUtils.class);
	private static List<String> FILTERED_PROPERTIES = Arrays.asList("name", "class", "uniqueValueCount", "finite");
	private static Map<Class<?>, List<PropertyDescriptor>> descriptorsMap = new HashMap<>();

	private FieldMetadataUtils() {
		MetadataConstants.DEFAULT_META_BY_TYPE.values().forEach(c -> getDescriptors(c.getClass()));
	}

	@SuppressWarnings("unchecked")
	public static <T extends FieldMetadata> T diff(T from, T to) throws IllegalStateException {
		try {
			T empty = (T) invokeConstructor(from.getClass());
			List<PropertyDescriptor> descriptors = getDescriptors(from.getClass());

			for(PropertyDescriptor descriptor : descriptors) {
				diffProperty(empty, to, from, descriptor);
			}
			return empty;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
			throw new IllegalStateException("Failed to diff bean", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends FieldMetadata> T merge(T from, T to) throws IllegalStateException {
		try {
			T clone;
			clone = (T) invokeConstructor(from.getClass());
			updateObject(to, clone);
			updateObject(from, clone);
			return clone;
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
			throw new IllegalStateException("Failed to clone bean", ex);
		}
	}

	public static <T extends FieldMetadata> void updateObject(T from, T to) throws IllegalStateException {
		Validate.notNull(from);
		Validate.notNull(to);

		// Only go through the process if the objects are not the same reference
		if(to == from) {
			return;
		}
		Class<?> originalClass = to.getClass();
		Class<?> updateClass = from.getClass();
		// you may want to work this check if you need to handle polymorphic relations
		if(!originalClass.equals(updateClass)) {
			throw new IllegalArgumentException("Received objects are not the same type of class");
		}

		List<PropertyDescriptor> descriptors = getDescriptors(originalClass);

		for(PropertyDescriptor descriptor : descriptors) {
			updateProperty(to, from, descriptor);
		}
	}

	private static List<PropertyDescriptor> getDescriptors(Class<?> clazz) {
		List<PropertyDescriptor> descriptors = descriptorsMap.get(clazz);
		if(descriptors == null) {
			PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(clazz);
			Predicate<PropertyDescriptor> filter = d -> !FILTERED_PROPERTIES.contains(d.getName());
			descriptors = Arrays.asList(desc).stream().filter(filter).collect(Collectors.toList());
			descriptorsMap.put(clazz, descriptors);
		}
		return descriptors;
	}

	private static void diffProperty(Object empty, Object to, Object from, PropertyDescriptor descriptor) throws IllegalStateException {
		if(!PropertyUtils.isReadable(to, descriptor.getName()) || !PropertyUtils.isWriteable(to, descriptor.getName())) {
			LOGGER.warn("Can't access getter/setter of property {} for bean {}", descriptor.getName(), to);
			return;
		}
		try {
			Method readMethod = descriptor.getReadMethod();
			Object fromValue = readMethod.invoke(from);
			Object toValue = readMethod.invoke(to);
			if(fromValue != null && !fromValue.equals(toValue)) {
				Method writeMethod = descriptor.getWriteMethod();
				writeMethod.invoke(empty, fromValue);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new IllegalStateException("Failed to diff property", ex);
		}
	}

	private static void updateProperty(Object origin, Object update, PropertyDescriptor descriptor) throws IllegalStateException {
		if(!PropertyUtils.isReadable(origin, descriptor.getName()) || !PropertyUtils.isWriteable(origin, descriptor.getName())) {
			LOGGER.warn("Can't access getter/setter of propert {} for bean {}", descriptor.getName(), origin);
			return;
		}
		try {
			Method readMethod = descriptor.getReadMethod();
			Object updateValue = readMethod.invoke(update);
			Object originalValue = readMethod.invoke(origin);
			if(updateValue != null && !updateValue.equals(originalValue)) {
				Method writeMethod = descriptor.getWriteMethod();
				writeMethod.invoke(origin, updateValue);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new IllegalStateException("Failed to update property", ex);
		}
	}

	private static <T> T invokeConstructor(final Class<T> clazz) throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?> constructor = clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		return (T) constructor.newInstance();
	}
}
