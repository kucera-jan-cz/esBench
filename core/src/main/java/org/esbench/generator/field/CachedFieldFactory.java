package org.esbench.generator.field;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * Caching proxy class which calls from real factory maxItems and then acts like normal FieldFactory.
 * @param <T> defines what type of field value it produces
 */
public class CachedFieldFactory<T> extends AbstractFieldFactory<T> {
	private final T[] fields;

	public CachedFieldFactory(AbstractFieldFactory<T> factory, Class<T> classType, int maxItems) {
		Validate.inclusiveBetween(1, Integer.MAX_VALUE, maxItems);
		fields = generateFields(factory, classType, maxItems);
	}

	private T[] generateFields(AbstractFieldFactory<T> factory, Class<T> classType, int maxItems) {
		List<T> fieldsAsList = new ArrayList<>();

		for(int i = 0; i < maxItems; i++) {
			fieldsAsList.add(factory.newInstance(i));
		}

		T[] emptyArray = (T[]) Array.newInstance(classType, 0);
		return fieldsAsList.toArray(emptyArray);
	}

	public T[] getCachedFields() {
		return fields.clone();
	}

	@Override
	public T newInstance(int uniqueId) {
		return fields[uniqueId % fields.length];
	}
}