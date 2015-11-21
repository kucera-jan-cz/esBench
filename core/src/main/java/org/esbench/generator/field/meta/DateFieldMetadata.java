package org.esbench.generator.field.meta;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DateFieldMetadata extends FieldMetadata {
	private Instant from;
	private Instant to;
	private Long step;
	private ChronoUnit unit;
	private String pattern;

	private transient DateTimeFormatter formatter;

	public DateFieldMetadata() {

	}

	public DateFieldMetadata(String name, int valuesPerDoc, Instant from, Instant to, long step, ChronoUnit unit, String pattern) {
		super(name, MetaType.DATE, valuesPerDoc);
		Validate.isTrue(from.isBefore(to), String.format("From (%s) must be earlier than than to (%s) for field %s", from, to, name));
		Validate.inclusiveBetween(1, Long.MAX_VALUE, step, "Step can't be negative for field " + name);
		this.from = from;
		this.to = to;
		this.step = step;
		this.unit = unit;
		this.pattern = pattern;
		this.formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC"));
	}

	public Instant getFrom() {
		return from;
	}

	public Instant getTo() {
		return to;
	}

	public Long getStep() {
		return step;
	}

	public String getPattern() {
		return pattern;
	}

	public ChronoUnit getUnit() {
		return unit;
	}

	@JsonIgnore
	public DateTimeFormatter getFormatter() {
		return formatter;
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
		return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE).append("from", String.valueOf(from))
				.append("to", String.valueOf(to))
				.append("step", step)
				.append("unit", String.valueOf(unit))
				.append("pattern", pattern)
				.build();
	}

	public void setFrom(Instant from) {
		this.from = from;
	}

	public void setTo(Instant to) {
		this.to = to;
	}

	public void setStep(Long step) {
		this.step = step;
	}

	public void setUnit(ChronoUnit unit) {
		this.unit = unit;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setFormatter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

}
