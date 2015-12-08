package org.esbench.cmd;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.esbench.core.DefaultProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Display resolved DefaultProperties to logger's output.
 * Helps user to check that all properties are resolved as he expects.
 */
public class ListPropertiesAction implements EsBenchAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListPropertiesAction.class);

	@Override
	public void perform(DefaultProperties properties) throws IOException {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		properties.list(printWriter);
		LOGGER.info("\nDefined properties:\n{}", writer.toString());
	}

}
