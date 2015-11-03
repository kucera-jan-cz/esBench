package org.esbench.generator.document;

/**
 * Factory for creating new instances of JSON document. 
 * @param <T> defines return type of JSON document
 */
public interface DocumentFactory<T> {

  /**
   * Create new instance of JSON document.
   * @param instanceId used for generating event's data.  
   * @return T document instance
   */
  public T newInstance(int instanceId);
}
