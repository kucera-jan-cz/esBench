package org.esbench.generator.field.type.numeric;

import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs integer by adding _step_ to _from_.
 * Variable _modulo_ represents number of unique values.
 */
public class FloatFieldFactory extends AbstractFieldFactory<Float> {
	private final float from;
	private final float step;
	private final float modulo;

	public FloatFieldFactory(Number from, Number to, Number step) {
		this.from = from.floatValue();
		this.step = step.floatValue();
		this.modulo = ((to.floatValue() - this.from) / this.step) + 1;
	}

	@Override
	public Float newInstance(int uniqueId) {
		float amountToAdd = (uniqueId % modulo) * step;
		return from + amountToAdd;
	}

	public float getFrom() {
		return from;
	}

	public float getStep() {
		return step;
	}

	public float getModulo() {
		return modulo;
	}

}
