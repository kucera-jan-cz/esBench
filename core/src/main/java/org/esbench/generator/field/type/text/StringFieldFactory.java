package org.esbench.generator.field.type.text;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.esbench.generator.field.AbstractFieldFactory;

/**
 * Constructs field by combining String tokens.
 * Example: tokens and 3 tokens per field:
 * :: A B C D E A
 * 0: X X X
 * 1:       X X X
 */
public class StringFieldFactory extends AbstractFieldFactory<String> {
	private final int tokensPerField;
	private final String[] tokens;

	public StringFieldFactory(int numOfTokens, String... tokens) {
		this.tokensPerField = numOfTokens;
		this.tokens = tokens.clone();
	}

	public StringFieldFactory(int numOfTokens, Collection<String> tokens) {
		this(numOfTokens, tokens.toArray(new String[0]));
	}

	@Override
	public String newInstance(int uniqueId) {
		StringBuilder buffer = new StringBuilder();
		long tokenPointer = uniqueId * tokensPerField;
		int id = (int) tokenPointer % tokens.length;
		buffer.append(tokens[id]);

		for(int i = 1; i < tokensPerField; i++) {
			int index = (id + i) % tokens.length;
			buffer.append(StringUtils.SPACE).append(tokens[index]);
		}
		return buffer.toString();
	}
}
