package org.esbench.generator.field.meta;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.esbench.generator.field.utils.AddressUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class IPv4FieldMetadata extends FieldMetadata {
	private String cidrAddress;
	@JsonIgnore
	private long numberOfAddresses;

	/**
	 * Protected constructor for JSON serialization
	 */
	protected IPv4FieldMetadata() {

	}

	public IPv4FieldMetadata(String name, int valuesPerDoc, String cidrAddress) {
		super(name, MetaType.IP, valuesPerDoc);
		Validate.notEmpty(cidrAddress);
		this.setCidrAddress(cidrAddress);
	}

	@Override
	public boolean isFinite() {
		return Strategy.SEQUENCE.equals(getStrategy()) && numberOfAddresses < Integer.MAX_VALUE;
	}

	@Override
	public int getUniqueValueCount() {
		return Math.toIntExact(numberOfAddresses);
	}

	public String getCidrAddress() {
		return cidrAddress;
	}

	public void setCidrAddress(String cidrAddress) {
		this.cidrAddress = cidrAddress;
		this.numberOfAddresses = AddressUtils.numberOfAddress(getCidrAddress());
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

}
