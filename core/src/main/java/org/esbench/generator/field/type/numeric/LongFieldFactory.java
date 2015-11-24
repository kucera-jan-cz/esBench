package org.esbench.generator.field.type.numeric;

import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs integer by adding _step_ to _from_.
 * Variable _modulo_ represents number of unique dates.
 */
public class LongFieldFactory extends AbstractFieldFactory<Long> {
	private final long from;
	private final long step;
	private final long modulo;

	public LongFieldFactory(Number from, Number to, Number step) {
		this.from = from.longValue();
		this.step = step.longValue();
		this.modulo = ((to.longValue() - this.from) / this.step) + 1L;
	}

	@Override
	public Long newInstance(int uniqueId) {
		long amountToAdd = (uniqueId % modulo) * step;
		return from + amountToAdd;
	}

	public long getFrom() {
		return from;
	}

	public long getStep() {
		return step;
	}

	public long getModulo() {
		return modulo;
	}
}
