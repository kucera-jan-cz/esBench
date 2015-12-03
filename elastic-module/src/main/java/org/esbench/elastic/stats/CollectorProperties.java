package org.esbench.elastic.stats;

import org.esbench.core.DefaultProperties;

public class CollectorProperties {
	private static final String TOKENS_STRING_MIN_OCCURENCE = "tokens.string.min_occurence";
	private static final String TOKENS_STRING_LIMIT = "tokens.string.limit";

	private final int stringTokenLimit;
	private final int stringTokenMinOccurent;

	public CollectorProperties(DefaultProperties props) {
		stringTokenLimit = props.getInt(TOKENS_STRING_LIMIT);
		stringTokenMinOccurent = props.getInt(TOKENS_STRING_MIN_OCCURENCE);
	}

	public int getStringTokenLimit() {
		return stringTokenLimit;
	}

	public int getStringTokenMinOccurent() {
		return stringTokenMinOccurent;
	}

}
