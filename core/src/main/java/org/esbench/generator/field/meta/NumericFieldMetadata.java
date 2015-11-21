package org.esbench.generator.field.meta;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NumericFieldMetadata extends FieldMetadata {
	public enum Type {
		BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE
	}

	private Number from;
	private Number to;
	private Number step;

	@JsonCreator
	public NumericFieldMetadata(@JsonProperty("type") MetaType type) {
		setMetaType(type);
	}

	public NumericFieldMetadata(String fullPath, int valuesPerDoc, MetaType metaType, Number from, Number to, Number step) {
		super(fullPath, metaType, valuesPerDoc);
		this.from = from;
		this.to = to;
		this.step = step;
	}

	public Number getFrom() {
		return from;
	}

	public Number getTo() {
		return to;
	}

	public Number getStep() {
		return step;
	}

	public void setFrom(Number from) {
		this.from = from;
	}

	public void setTo(Number to) {
		this.to = to;
	}

	public void setStep(Number step) {
		this.step = step;
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
