package org.esbench.generator.field.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.core.JsonGenerator;

public class StringFieldMetadata extends FieldMetadata {
	private final int tokensPerValue;
	private final List<String> tokens;

	public StringFieldMetadata(String name, int valuesPerDoc, int tokenPerValue, List<String> tokens) {
		super(name, String.class, valuesPerDoc);
		Validate.notEmpty(tokens);
		this.tokens = new ArrayList<>(tokens);
		Collections.sort(this.tokens);
		this.tokensPerValue = tokenPerValue;
	}

	public int getTokensPerValue() {
		return tokensPerValue;
	}

	public List<String> getTokens() {
		return tokens;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

	@Override
	public void specificMetadataToJSON(JsonGenerator generator) throws IOException {
		// "title" : {"words" : 2, "tokens" : {"A::3135;;B::3000;;C::594"}},
		generator.writeNumberField("words", tokensPerValue);
		// @TODO
		generator.writeStringField("tokens", "TODO");
	}

}
