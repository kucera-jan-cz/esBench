package org.esbench.generator.field.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esbench.workload.WorkloadConstants;
import org.esbench.workload.json.databind.TokensIdGenerator;
import org.esbench.workload.json.databind.TokensIdResolver;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = TokensIdGenerator.class, property = WorkloadConstants.REF_ID_PROP, resolver = TokensIdResolver.class)
public class TokenList {
	private List<String> tokens = Collections.emptyList();

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = new ArrayList<String>(tokens);
		Collections.sort(this.tokens);
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
		return String.valueOf(tokens);
	}

}
