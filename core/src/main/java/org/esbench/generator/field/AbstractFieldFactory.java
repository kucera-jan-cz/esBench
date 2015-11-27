package org.esbench.generator.field;

/**
 * Abstract field factory class implemented common methods.
 */
public abstract class AbstractFieldFactory<T> implements FieldFactory<T> {

	public AbstractFieldFactory() {
	}

	@Override
	public abstract T newInstance(int uniqueId);
}
