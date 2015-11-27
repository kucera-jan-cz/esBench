package org.esbench.cmd;

import java.io.IOException;

import org.esbench.core.DefaultProperties;

public interface EsBenchAction {
	public void perform(DefaultProperties props) throws IOException;
}
