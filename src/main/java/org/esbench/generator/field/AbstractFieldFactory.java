package org.esbench.generator.field;

import org.apache.commons.lang3.Validate;

public abstract class AbstractFieldFactory<T> implements FieldFactory<T> {
	private final int tokensPerField;

	public AbstractFieldFactory() {
		this.tokensPerField = 1;
	}

	public AbstractFieldFactory(int tokensPerField) {
		Validate.inclusiveBetween(1, Integer.MAX_VALUE, tokensPerField, "Provider for field %s has incorrect tokenPerField %d", this, tokensPerField);
		this.tokensPerField = tokensPerField;
	}

	@Override
	public int getTokensPerField() {
		return tokensPerField;
	}

	@Override
	public abstract T newInstance(int alertId);
}
