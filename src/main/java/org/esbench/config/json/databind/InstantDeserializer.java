package org.esbench.config.json.databind;

import java.io.IOException;
import java.time.Instant;

import org.esbench.generator.field.meta.MetadataConstants;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class InstantDeserializer extends JsonDeserializer<Instant> {

	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return Instant.from(MetadataConstants.DEFAULT_DATE_FORMATTER.parse(p.getText()));
	}

}
