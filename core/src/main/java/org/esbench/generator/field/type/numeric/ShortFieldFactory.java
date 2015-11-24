package org.esbench.generator.field.type.numeric;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs integer by adding _step_ to _from_.
 * Variable _modulo_ represents number of unique values.
 */
public class ShortFieldFactory extends AbstractFieldFactory<Short> {
	private final int from;
	private final int step;
	private final int modulo;

	public ShortFieldFactory(Number from, Number to, Number step) {
		this.from = from.intValue();
		this.step = step.intValue();
		Validate.inclusiveBetween(Short.MIN_VALUE, Short.MAX_VALUE, this.from);
		Validate.inclusiveBetween(Short.MIN_VALUE, Short.MAX_VALUE, this.step);
		Validate.isTrue(this.from < to.intValue(), "From can't be bigger than to");
		this.modulo = ((to.intValue() - this.from) / this.step);
		Validate.inclusiveBetween(Short.MIN_VALUE, (Short.MAX_VALUE * 2) + 1, this.modulo, "Range between from and to exceeded type capacity");
	}

	@Override
	public Short newInstance(int uniqueId) {
		int amountToAdd = (uniqueId % modulo) * step;
		return (short) (from + amountToAdd);
	}

	public short getFrom() {
		return (byte) from;
	}

	public short getStep() {
		return (byte) step;
	}

	public short getModulo() {
		return (byte) modulo;
	}

}
