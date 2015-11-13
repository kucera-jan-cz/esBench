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

	public LongFieldFactory(long from, long step, long modulo) {
		this.from = from;
		this.step = step;
		this.modulo = modulo;
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
