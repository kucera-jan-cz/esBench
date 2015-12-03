package org.esbench.generator.field;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.esbench.generator.field.meta.IPv4FieldMetadata;
import org.esbench.generator.field.meta.StringFieldMetadata;
import org.esbench.generator.field.type.ipv4.Ipv4FieldFactory;
import org.esbench.generator.field.type.text.StringFieldFactory;
import org.testng.annotations.Test;

public class CachedFieldFactoryTest {
	@Test
	public void ipCaching() {
		IPv4FieldMetadata metadata = new IPv4FieldMetadata("ip", 1, "192.168.0.0/16");
		Ipv4FieldFactory factory = new Ipv4FieldFactory("192.168.0.0/16");
		CachedFieldFactory<String> cachedFactory = new CachedFieldFactory<String>(factory, String.class, metadata.getUniqueValueCount());
		for(int i = 0; i < 100; i++) {
			assertEquals(cachedFactory.newInstance(i), factory.newInstance(i));
		}
	}

	@Test
	public void stringCaching() {
		StringFieldMetadata metadata = new StringFieldMetadata("text", 1, 4, generateTokens(18));
		StringFieldFactory factory = new StringFieldFactory(metadata.getTokensPerValue(), metadata.getTokens());
		CachedFieldFactory<String> cachedFactory = new CachedFieldFactory<String>(factory, String.class, metadata.getUniqueValueCount());
		for(int i = 0; i < 100; i++) {
			assertEquals(cachedFactory.newInstance(i), factory.newInstance(i));
		}
	}

	private static List<String> generateTokens(int numOfTokens) {
		List<String> tokens = new ArrayList<String>(numOfTokens);
		for(int i = 0; i < numOfTokens; i++) {
			tokens.add(new Character((char) ('A' + i)).toString());
		}
		return tokens;
	}
}
