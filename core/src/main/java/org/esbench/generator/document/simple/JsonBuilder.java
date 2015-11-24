package org.esbench.generator.document.simple;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Serialize given type to JSON.
 */
public interface JsonBuilder {
	/**
	 * Write to given generator JSON representation of field created with instanceId.
	 * @param generator Jackson's generator to which object should be serialized to
	 * @param instanceId for generating unique fields
	 * @throws IOException when serialization fails for any reason
	 */
	public void write(JsonGenerator generator, int instanceId) throws IOException;
}
