package org.esbench.generator.field.meta;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import org.esbench.generator.field.meta.BooleanFieldMetadata.Type;

import com.google.common.collect.ImmutableMap;

public final class MetadataConstants {
	public static final int UNDEFINED_UNQIUE_VALUES = -1;
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN).withZone(ZoneId.of("UTC"));

	public static final StringFieldMetadata DEFAULT_STRING_META = new StringFieldMetadata("string", 1, 1, Collections.emptyList());
	public static final BooleanFieldMetadata DEFAULT_BOOLEAN_META = new BooleanFieldMetadata("boolean", 1, Type.TICK_TOCK);
	public static final DateFieldMetadata DEFAULT_DATE_META = new DateFieldMetadata("date", 1, Instant.EPOCH, Instant.parse("2015-12-31T23:59:59Z"), 1L,
			ChronoUnit.SECONDS, DEFAULT_DATE_PATTERN);
	public static final NumericFieldMetadata DEFAULT_LONG_META = new NumericFieldMetadata("long", 1, MetaType.LONG, 0L, 1024L, 1L);
	public static final NumericFieldMetadata DEFAULT_INTEGER_META = new NumericFieldMetadata("integer", 1, MetaType.INTEGER, 0, 100, 1);

	public static final IPv4FieldMetadata DEFAULT_IP_META = new IPv4FieldMetadata("ip", 1, "192.168.0.0/24");

	public static final ObjectTypeMetadata DEFAULT_OBJECT_META = new ObjectTypeMetadata("object", Collections.emptyList());

	public static final MultiFieldMetadata DEFAULT_MULTI_META = new MultiFieldMetadata("multi", Collections.emptyList());

	public static final ImmutableMap<MetaType, FieldMetadata> DEFAULT_META_BY_TYPE = ImmutableMap.<MetaType, FieldMetadata> builder()
			.put(MetaType.BOOLEAN, DEFAULT_BOOLEAN_META)
			.put(MetaType.STRING, DEFAULT_STRING_META)
			.put(MetaType.DATE, DEFAULT_DATE_META)
			.put(MetaType.INTEGER, DEFAULT_INTEGER_META)
			.put(MetaType.LONG, DEFAULT_LONG_META)
			.put(MetaType.IP, DEFAULT_IP_META)
			.put(MetaType.OBJECT, DEFAULT_OBJECT_META)
			.put(MetaType.MULTI, DEFAULT_MULTI_META)
			.build();

	private MetadataConstants() {

	}
}
