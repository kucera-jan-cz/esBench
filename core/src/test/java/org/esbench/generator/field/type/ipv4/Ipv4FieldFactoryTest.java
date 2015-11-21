package org.esbench.generator.field.type.ipv4;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class Ipv4FieldFactoryTest {
	@Test
	public void simpleCidr() {
		Ipv4FieldFactory factory = new Ipv4FieldFactory("127.0.0.0/30");
		assertEquals(factory.newInstance(0), "127.0.0.0");
		assertEquals(factory.newInstance(1), "127.0.0.1");
		assertEquals(factory.newInstance(2), "127.0.0.2");
		assertEquals(factory.newInstance(3), "127.0.0.3");
		assertEquals(factory.newInstance(4), "127.0.0.0");
	}

	@Test
	public void oneAddress() {
		Ipv4FieldFactory factory = new Ipv4FieldFactory("192.168.0.101/32");
		assertEquals(factory.newInstance(0), "192.168.0.101");
		assertEquals(factory.newInstance(1), "192.168.0.101");
		assertEquals(factory.newInstance(2), "192.168.0.101");
	}
}
