package org.esbench.generator.field;

import static org.esbench.generator.field.FieldConstants.SINGLE_TOKEN;

import org.apache.commons.lang3.Validate;

/**
 * Abstract field factory class implemented common methods.
 */
public abstract class AbstractFieldFactory<T> implements FieldFactory<T> {
	@Deprecated
	private final int tokensPerField;

	public AbstractFieldFactory() {
		this.tokensPerField = SINGLE_TOKEN;
	}

	@Deprecated
	public AbstractFieldFactory(int tokensPerField) {
		Validate.inclusiveBetween(SINGLE_TOKEN, Integer.MAX_VALUE, tokensPerField, "Factory %s has incorrect tokenPerField %d", this, tokensPerField);
		this.tokensPerField = tokensPerField;
	}

	@Override
	public abstract T newInstance(int uniqueId);
}
