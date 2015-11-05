package org.esbench.generator.field.type.date;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs dates by adding _step_ of time _unit_ to _from_.
 * Variable _modulo_ represents number of unique dates. 
 */
public class DateFieldFactory extends AbstractFieldFactory<LocalDateTime> {
	private final LocalDateTime from;
	private final long step;
	private final ChronoUnit unit;
	private final int modulo;

	public DateFieldFactory(LocalDateTime from, long step, ChronoUnit unit, int modulo) {
		this.from = from;
		this.step = step;
		this.unit = unit;
		this.modulo = modulo;
	}

	@Override
	public LocalDateTime newInstance(int uniqueId) {
		long amountToAdd = (uniqueId % modulo) * step;
		return from.plus(amountToAdd, unit);
	}
}
