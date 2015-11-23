package org.esbench.elastic.stats;

import org.esbench.core.DefaultProperties;

public class CollectorProperties {
	private static final String TOKENS_STRING_MIN_OCCURENCE = "tokens.string.min_occurence";
	private static final String TOKENS_STRING_LIMIT = "tokens.string.limit";

	private int stringTokenLimit;
	private int stringTokenMinOccurent;

	public CollectorProperties(DefaultProperties props) {
		stringTokenLimit = props.get(TOKENS_STRING_LIMIT, 10_000);
		stringTokenMinOccurent = props.get(TOKENS_STRING_MIN_OCCURENCE, 1);
	}

	public int getStringTokenLimit() {
		return stringTokenLimit;
	}

	public int getStringTokenMinOccurent() {
		return stringTokenMinOccurent;
	}

}
