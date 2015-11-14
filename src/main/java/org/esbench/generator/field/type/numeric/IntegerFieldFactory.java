package org.esbench.generator.field.type.numeric;

import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs integer by adding _step_ to _from_.
 * Variable _modulo_ represents number of unique dates. 
 */
public class IntegerFieldFactory extends AbstractFieldFactory<Integer> {
	private final int from;
	private final int step;
	private final int modulo;

	public IntegerFieldFactory(Number from, Number to, Number step) {
		this.from = from.intValue();
		this.step = step.intValue();
		this.modulo = ((to.intValue() - this.from) / this.step) + 1;
	}

	@Override
	public Integer newInstance(int uniqueId) {
		int amountToAdd = (uniqueId % modulo) * step;
		return from + amountToAdd;
	}

	public int getFrom() {
		return from;
	}

	public int getStep() {
		return step;
	}

	public int getModulo() {
		return modulo;
	}

}
