package org.esbench.generator.field;

/**
 * Builds field values.
 * @param <T> defines return type of field value
 */
public interface FieldFactory<T> {
	/**
	 * @return number of tokens which generates returning value.
	 */
	public int getTokensPerField();

	/**
	 * Based on given instanceId return generated field value.
	 * 
	 * @param instanceId representing unique id
	 * @return T field value
	 */
	public T newInstance(int instanceId);

}