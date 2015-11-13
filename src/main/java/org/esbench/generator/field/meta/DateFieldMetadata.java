package org.esbench.generator.field.meta;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.core.JsonGenerator;

public class DateFieldMetadata extends FieldMetadata {
	private Instant from;
	private Instant to;
	private long step;
	private ChronoUnit unit;
	private String pattern;
	private transient DateTimeFormatter formatter;

	public DateFieldMetadata(String name, int valuesPerDoc, Instant from, Instant to, long step, ChronoUnit unit, String pattern) {
		super(name, Date.class, valuesPerDoc);
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

	public long getStep() {
		return step;
	}

	public String getPattern() {
		return pattern;
	}

	public ChronoUnit getUnit() {
		return unit;
	}

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
		return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE).append("from", from.toString())
				.append("to", to.toString())
				.append("step", step)
				.append("unit", unit.toString())
				.append("pattern", pattern)
				.build();

	}

	@Override
	public void specificMetadataToJSON(JsonGenerator generator) throws IOException {
		// "published" : {"from" : "2015-01-01T00:00:00", "to" : "2015-01-10T00:00:00", "step" : "5 Minutes"},
		generator.writeStringField("from", formatter.format(from));
		generator.writeStringField("to", formatter.format(to));
		generator.writeStringField("step", step + " " + unit);
	}

}
