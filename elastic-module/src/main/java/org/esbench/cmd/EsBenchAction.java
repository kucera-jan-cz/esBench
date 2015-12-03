package org.esbench.cmd;

import java.io.IOException;

import org.esbench.core.DefaultProperties;

/**
 * Interface for execution tasks.
 */
public interface EsBenchAction {
	/**
	 * Execute desired action with passed properties
	 * @param properties holding configuration options from user and defaults
	 * @throws IOException when action failed for any reason
	 */
	public void perform(DefaultProperties properties) throws IOException;
}
