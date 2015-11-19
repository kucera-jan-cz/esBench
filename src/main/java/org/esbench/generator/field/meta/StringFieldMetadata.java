package org.esbench.generator.field.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.esbench.config.ConfigurationConstants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StringFieldMetadata extends FieldMetadata {
	@JsonProperty(value = ConfigurationConstants.WORDS_PROP)
	private Integer tokensPerValue;
	@JsonProperty(value = ConfigurationConstants.TOKENS_PROP)
	private List<String> tokens;

	@JsonCreator
	public StringFieldMetadata() {
	}

	public StringFieldMetadata(String name, int valuesPerDoc, int tokenPerValue, List<String> tokens) {
		super(name, MetaType.STRING, valuesPerDoc);
		Validate.notNull(tokens);
		this.tokens = new ArrayList<>(tokens);
		Collections.sort(this.tokens);
		this.tokensPerValue = tokenPerValue;
	}

	public Integer getTokensPerValue() {
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

	public void setTokensPerValue(Integer tokensPerValue) {
		this.tokensPerValue = tokensPerValue;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

}
