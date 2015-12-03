package org.esbench.generator.field.meta;

import static org.esbench.workload.WorkloadConstants.ARRAY_PROP;
import static org.esbench.workload.WorkloadConstants.STRATEGY_PROP;
import static org.esbench.workload.WorkloadConstants.TOKENS_PROP;
import static org.esbench.workload.WorkloadConstants.TYPE_PROP;
import static org.esbench.workload.WorkloadConstants.WORDS_PROP;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { TYPE_PROP, ARRAY_PROP, STRATEGY_PROP, WORDS_PROP, TOKENS_PROP })
public class StringFieldMetadata extends FieldMetadata {
	@JsonProperty(value = WORDS_PROP)
	private Integer tokensPerValue;

	@JsonIdentityReference(alwaysAsId = true)
	@JsonProperty(value = TOKENS_PROP)
	private TokenList tokenList = new TokenList();

	/**
	 * Protected constructor for JSON serialization
	 */
	protected StringFieldMetadata() {
	}

	@Override
	public boolean isFinite() {
		return Strategy.SEQUENCE.equals(getStrategy());
	}

	@Override
	public int getUniqueValueCount() {
		if(tokenList.getTokens().isEmpty()) {
			return MetadataConstants.UNDEFINED_UNQIUE_VALUES;
		}
		int i = 1;
		while(true) {
			int restAfterModulo = (i * tokensPerValue.intValue()) % tokenList.getTokens().size();
			if(restAfterModulo == 0) {
				return i;
			} else {
				i++;
			}
		}
	}

	public StringFieldMetadata(String name, int valuesPerDoc, int tokenPerValue, List<String> tokens) {
		super(name, MetaType.STRING, valuesPerDoc);
		Validate.notNull(tokens);
		this.setTokens(tokens);
		this.tokensPerValue = tokenPerValue;
	}

	public Integer getTokensPerValue() {
		return tokensPerValue;
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

	@JsonProperty(value = TOKENS_PROP)
	public TokenList getTokenList() {
		return tokenList;
	}

	@JsonProperty(value = TOKENS_PROP)
	public void setTokenList(TokenList tokenList) {
		this.tokenList = tokenList;
	}

	public void setTokens(List<String> tokens) {
		this.tokenList.setTokens(tokens);
	}

	public List<String> getTokens() {
		return tokenList.getTokens();
	}

}
