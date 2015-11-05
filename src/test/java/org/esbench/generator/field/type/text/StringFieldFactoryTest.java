package org.esbench.generator.field.type.text;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class StringFieldFactoryTest {

	private static final String[] A_F = generateTokens(6);
	private static final List<String> A_D = Arrays.asList(generateTokens(4));

	@Test
	public void newInstanceCase01() {
		StringFieldFactory factory = new StringFieldFactory(3, A_F);
		assertEquals(factory.newInstance(0), "A B C");
		assertEquals(factory.newInstance(1), "D E F");
		assertEquals(factory.newInstance(2), "A B C");
		assertEquals(factory.newInstance(3), "D E F");
	}

	@Test
	public void newInstanceCase02() {
		StringFieldFactory factory = new StringFieldFactory(4, A_F);
		assertEquals(factory.newInstance(0), "A B C D");
		assertEquals(factory.newInstance(1), "E F A B");
		assertEquals(factory.newInstance(2), "C D E F");
		assertEquals(factory.newInstance(3), "A B C D");
	}

	@Test
	public void newInstanceCase03() {
		StringFieldFactory factory = new StringFieldFactory(3, A_D);
		assertEquals(factory.newInstance(0), "A B C");
		assertEquals(factory.newInstance(1), "D A B");
		assertEquals(factory.newInstance(2), "C D A");
		assertEquals(factory.newInstance(3), "B C D");
		assertEquals(factory.newInstance(4), "A B C");
	}

	private static String[] generateTokens(int numOfTokens) {
		String[] tokens = new String[numOfTokens];
		for(int i = 0; i < numOfTokens; i++) {
			tokens[i] = new Character((char) ('A' + i)).toString();
		}
		return tokens;
	}

}
