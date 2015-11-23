package org.esbench.workload.json.databind;

import java.io.IOException;
import java.time.Instant;

import org.esbench.generator.field.meta.MetadataConstants;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class InstantSerializer extends JsonSerializer<Instant> {

	@Override
	public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		String valueAsString = MetadataConstants.DEFAULT_DATE_FORMATTER.format(value);
		gen.writeString(valueAsString);
	}

}
