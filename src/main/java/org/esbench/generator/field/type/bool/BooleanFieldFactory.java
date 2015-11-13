package org.esbench.generator.field.type.bool;

import org.esbench.generator.field.AbstractFieldFactory;
import org.esbench.generator.field.FieldFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanFieldFactory extends AbstractFieldFactory<Boolean> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BooleanFieldFactory.class);

	@Override
	public Boolean newInstance(int uniqueId) {
		return isEven(uniqueId);
	}

	private static boolean isEven(int i) {
		return (i & 1) == 0;
	}

	public static FieldFactory<Boolean> valueOf(String name) {
		switch(name) {
		case "TICK_TOCK":
			return TICK_TOCK;
		case "ALWAYS_TRUE":
			return ALWAYS_TRUE;
		case "ALWAYS_FALSE":
			return ALWAYS_FALSE;
		default:
			LOGGER.warn("Unknown type {} returning TICK_TOCK", name);
			return TICK_TOCK;
		}
	}

	/**
	 * Returns true for every odd number, false otherwise.
	 */
	public static final BooleanFieldFactory TICK_TOCK = new BooleanFieldFactory();

	/**
	 * Returns true for any given uniqueId.
	 */
	public static final AbstractFieldFactory<Boolean> ALWAYS_TRUE = new AbstractFieldFactory<Boolean>() {
		@Override
		public Boolean newInstance(int uniqueId) {
			return Boolean.TRUE;
		}
	};

	/**
	 * Returns false for any given uniqueId.
	 */
	public static final AbstractFieldFactory<Boolean> ALWAYS_FALSE = new AbstractFieldFactory<Boolean>() {
		@Override
		public Boolean newInstance(int uniqueId) {
			return Boolean.FALSE;
		}
	};
}
