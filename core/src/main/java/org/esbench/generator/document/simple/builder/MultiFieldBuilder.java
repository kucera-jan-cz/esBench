package org.esbench.generator.document.simple.builder;

import java.io.IOException;
import java.util.List;

import org.esbench.generator.document.simple.JsonBuilder;

import com.fasterxml.jackson.core.JsonGenerator;

public class MultiFieldBuilder implements JsonBuilder {
	private final JsonBuilder[] builders;
	private final int numberOfbuilders;

	public MultiFieldBuilder(List<JsonBuilder> buildersAsList) {
		this.builders = buildersAsList.toArray(new JsonBuilder[0]);
		this.numberOfbuilders = builders.length;
	}

	@Override
	public void write(JsonGenerator gen, int instanceId) throws IOException {
		int rounded = instanceId / numberOfbuilders;
		builders[instanceId % numberOfbuilders].write(gen, rounded);
	}

}
