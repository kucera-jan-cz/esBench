package org.esbench.generator.document.simple;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public interface JsonBuilder {
  public void write(JsonGenerator gen, int instanceId) throws IOException;
}
