package org.esbench.generator.field.meta;

import static org.esbench.config.ConfigurationConstants.ARRAY_PROP;
import static org.esbench.config.ConfigurationConstants.STRATEGY_PROP;
import static org.esbench.config.ConfigurationConstants.TOKENS_PROP;
import static org.esbench.config.ConfigurationConstants.TYPE_PROP;
import static org.esbench.config.ConfigurationConstants.WORDS_PROP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { TYPE_PROP, ARRAY_PROP, STRATEGY_PROP, WORDS_PROP, TOKENS_PROP })
public class StringFieldMetadata extends FieldMetadata {
	@JsonProperty(value = WORDS_PROP)
	private Integer tokensPerValue;
	@JsonProperty(value = TOKENS_PROP)
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
