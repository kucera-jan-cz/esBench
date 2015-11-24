package org.esbench.generator.document.simple;

import java.util.Random;

import org.esbench.generator.field.meta.Strategy;

public class InstanceIdTransformer {
	private static final Random generator = new Random();
	public static final InstanceIdTransformer NOP = new InstanceIdTransformer();
	public static final InstanceIdTransformer RANDOM = new InstanceIdTransformer() {
		@Override
		public int compute(int uniqueId) {
			return generator.nextInt(Integer.MAX_VALUE);
		}
	};

	public int compute(int uniqueId) {
		return uniqueId;
	}

	public static InstanceIdTransformer valueOf(Strategy strategy) {
		switch(strategy) {
		case SEQUENCE:
			return NOP;
		case RANDOM:
			return RANDOM;
		default:
			throw new IllegalArgumentException("Unknown type: " + strategy);
		}
	}
}
