package org.esbench.generator.field;

/**
 * Field factory returning null value for every call.
 */
public class NullFieldFactory implements FieldFactory<Object> {

  @Override
  public int getTokensPerField() {
    return 1;
  }

  @Override
  public Object newInstance(int uniqueId) {
    return null;
  }

}
