package org.esbench.workload.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class WorkloadPrettyPrinter extends DefaultPrettyPrinter {
	private static final long serialVersionUID = -8069251060423659883L;
	private String path = StringUtils.EMPTY;
	private final LinkedList<Integer> pathIndices = new LinkedList<>();
	private boolean isFieldProperties = false;
	private final List<Pattern> patterns;

	public WorkloadPrettyPrinter() {
		this("(/fields/[^/]*){1,}", "/defaults/[^/]*");
	}

	public WorkloadPrettyPrinter(String... regexes) {
		this._spacesInObjectEntries = false;
		patterns = Arrays.asList(regexes).stream().map(r -> Pattern.compile(r)).collect(Collectors.toList());
	}

	@Override
	public WorkloadPrettyPrinter createInstance() {
		return new WorkloadPrettyPrinter();
	}

	@Override
	public void writeStartObject(JsonGenerator jg) throws IOException, JsonGenerationException {
		String tokenName = jg.getOutputContext().getParent().getCurrentName();

		if(tokenName == null) {
			pathIndices.push(path.length());
		} else {
			pathIndices.push(path.length());
			path += "/" + tokenName;
		}
		this.isFieldProperties = isFieldProperty();
		super.writeStartObject(jg);
	}

	@Override
	public void beforeObjectEntries(JsonGenerator jg) throws IOException, JsonGenerationException {
		if(!this.isFieldProperties) {
			super.beforeObjectEntries(jg);
		}
	}

	@Override
	public void writeObjectEntrySeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
		if(isFieldProperties) {
			jg.writeRaw(", ");
		} else {
			super.writeObjectEntrySeparator(jg);
		}
	}

	@Override
	public void writeEndObject(JsonGenerator jg, int nrOfEntries) throws IOException, JsonGenerationException {
		int length = pathIndices.pop();
		path = path.substring(0, length);
		if(isFieldProperties) {
			--_nesting;
			jg.writeRaw('}');
		} else {
			super.writeEndObject(jg, nrOfEntries);
		}
		this.isFieldProperties = isFieldProperty();
	}

	private boolean isFieldProperty() {
		return patterns.stream().anyMatch(p -> p.matcher(path).matches());
	}
}
