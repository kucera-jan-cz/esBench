package org.esbench.elastic.sender.exceptions;

import java.io.IOException;

/**
 * Thrown when document insertion failed for any reason.
 */
public class InsertionFailure extends IOException {
	private static final long serialVersionUID = -2220133404588519718L;

	public InsertionFailure(String message, Throwable cause) {
		super(message, cause);
	}

	public InsertionFailure(String message) {
		super(message);
	}

}
