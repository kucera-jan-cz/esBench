package org.esbench.generator.field.type.bool;

import org.esbench.generator.field.AbstractFieldFactory;

public class BooleanFieldFactory extends AbstractFieldFactory<Boolean> {
  private static final int SINGLE_TOKEN_PER_FIELD = 1;

  public BooleanFieldFactory() {
    super(SINGLE_TOKEN_PER_FIELD);
  }

  @Override
  public Boolean newInstance(int uniqueId) {
    return isEven(uniqueId);
  }

  private static boolean isEven(int i) {
    return (i & 1) == 0;
  }

  /**
   * Returns true for every odd number, false otherwise.
   */
  public static final BooleanFieldFactory TICK_TOCK = new BooleanFieldFactory();

  /**
   * Returns true for any given uniqueId.
   */
  public static final AbstractFieldFactory<Boolean> ALWAYS_TRUE = new AbstractFieldFactory<Boolean>() {
    @Override
    public Boolean newInstance(int uniqueId) {
      return Boolean.TRUE;
    }
  };

  /**
   * Returns false for any given uniqueId.
   */
  public static final AbstractFieldFactory<Boolean> ALWAYS_FALSE = new AbstractFieldFactory<Boolean>() {
    @Override
    public Boolean newInstance(int uniqueId) {
      return Boolean.FALSE;
    }
  };
}
