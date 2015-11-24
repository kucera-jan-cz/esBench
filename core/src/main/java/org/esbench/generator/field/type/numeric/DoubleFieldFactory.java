package org.esbench.generator.field.type.numeric;

import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs integer by adding _step_ to _from_.
 * Variable _modulo_ represents number of unique values.
 */
public class DoubleFieldFactory extends AbstractFieldFactory<Double> {
	private final double from;
	private final double step;
	private final double modulo;

	public DoubleFieldFactory(Number from, Number to, Number step) {
		this.from = from.doubleValue();
		this.step = step.doubleValue();
		this.modulo = ((to.doubleValue() - this.from) / this.step) + 1;
	}

	@Override
	public Double newInstance(int uniqueId) {
		double amountToAdd = (uniqueId % modulo) * step;
		return from + amountToAdd;
	}

	public double getFrom() {
		return from;
	}

	public double getStep() {
		return step;
	}

	public double getModulo() {
		return modulo;
	}

}
