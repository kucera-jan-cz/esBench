package org.esbench.generator.field.type.bool;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BooleanFieldFactoryTest {

  @Test(dataProvider = "newInstanceDataProvider")
  public void newInstance(int uniqueId, Boolean expectedValue) {
    assertEquals(BooleanFieldFactory.TICK_TOCK.newInstance(uniqueId), expectedValue);
  }

  @DataProvider
  public Object[][] newInstanceDataProvider() {
    Object[][] values = { { 0, true }, { 1, false }, { 33, false }, { 20, true }, { 1024, true } };
    return values;
  }

  @Test(invocationCount = 10)
  public void alwaysTrue() {
    int uniqueId = RandomUtils.nextInt(0, Integer.MAX_VALUE);
    assertTrue(BooleanFieldFactory.ALWAYS_TRUE.newInstance(uniqueId));
  }

  @Test(invocationCount = 10)
  public void alwaysFalse() {
    int uniqueId = RandomUtils.nextInt(0, Integer.MAX_VALUE);
    assertFalse(BooleanFieldFactory.ALWAYS_FALSE.newInstance(uniqueId));
  }
}
