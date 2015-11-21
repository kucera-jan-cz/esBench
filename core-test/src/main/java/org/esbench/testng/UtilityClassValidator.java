package org.esbench.testng;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class UtilityClassValidator {
	private static final int SINGLE_CONSTRUCTOR = 1;

	private UtilityClassValidator() {
	}

	public static void validate(final Class<?> clazz) {
		assertEquals(clazz.getDeclaredConstructors().length, SINGLE_CONSTRUCTOR, "Found multiple constructors");
		assertTrue(Modifier.isFinal(clazz.getModifiers()), "Class is not final");
		try {
			Constructor<?> constructor = validateConstructor(clazz);
			constructor.setAccessible(true);
			constructor.newInstance();
			constructor.setAccessible(false);
			for(final Method method : clazz.getMethods()) {
				if(!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
					fail("Found non static method: " + method);
				}
			}
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
			fail("Reflection error", e);
		}
	}

	private static Constructor<?> validateConstructor(final Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			if(constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
				fail("Constructor is not private");
			}
			return constructor;
		} catch (NoSuchMethodException ex) {
			fail("Empty constructor has not been found", ex);
		}
		return null;
	}
}
