package org.esbench.generator.field.meta;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.core.JsonGenerator;

public class IPv4FieldMetadata extends FieldMetadata {
	private String cidrAddress;

	public IPv4FieldMetadata(String name, int valuesPerDoc, String cidrAddress) {
		super(name, String.class, valuesPerDoc);
		Validate.notEmpty(cidrAddress);
		this.cidrAddress = cidrAddress;
	}

	public String getCidrAddress() {
		return cidrAddress;
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
		// "ipv4" : {"127.0.0.1/30"},
		generator.writeStringField("cidr", cidrAddress);
	}

}
