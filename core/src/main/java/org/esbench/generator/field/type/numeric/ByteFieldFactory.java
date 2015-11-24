package org.esbench.generator.field.type.numeric;

import org.apache.commons.lang3.Validate;
import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs integer by adding _step_ to _from_.
 * Variable _modulo_ represents number of unique values.
 */
public class ByteFieldFactory extends AbstractFieldFactory<Byte> {
	private final int from;
	private final int step;
	private final int modulo;

	public ByteFieldFactory(Number from, Number to, Number step) {
		this.from = from.intValue();
		this.step = step.intValue();
		Validate.inclusiveBetween(Byte.MIN_VALUE, Byte.MAX_VALUE, this.from);
		Validate.inclusiveBetween(Byte.MIN_VALUE, Byte.MAX_VALUE, this.step);
		Validate.isTrue(this.from < to.intValue(), "From can't be bigger than to");
		this.modulo = ((to.intValue() - this.from) / this.step);
		Validate.inclusiveBetween(Byte.MIN_VALUE, (Byte.MAX_VALUE * 2) + 1, this.modulo, "Range between from and to exceeded type capacity");
	}

	@Override
	public Byte newInstance(int uniqueId) {
		int amountToAdd = (uniqueId % modulo) * step;
		return (byte) (from + amountToAdd);
	}

	public byte getFrom() {
		return (byte) from;
	}

	public byte getStep() {
		return (byte) step;
	}

	public byte getModulo() {
		return (byte) modulo;
	}

}
