package org.esbench.elastic.exceptions;

/**
 * Thrown when elasticsearch client can't connect to cluster.
 */
public class ElasticsearchUnavailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ElasticsearchUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElasticsearchUnavailableException(String message) {
		super(message);
	}

}
