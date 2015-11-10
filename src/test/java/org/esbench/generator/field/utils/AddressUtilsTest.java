package org.esbench.generator.field.utils;

import static org.testng.Assert.assertEquals;

import org.apache.commons.lang3.RandomUtils;
import org.esbench.testng.UtilityClassValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddressUtilsTest {

	private static final long HIGHEST_IP = 4294967295L;
	private static final int LOWEST_IP = 0;

	@Test
	public void utilityTest() {
		UtilityClassValidator.validate(AddressUtils.class);
	}

	@DataProvider
	public Object[][] invalidTextInputsDataProvider() {
		Object[][] values = { { "" }, { "127." }, { "256.0.0.0" }, { "A.B.C.D" } };
		return values;
	}

	@Test(dataProvider = "invalidTextInputsDataProvider", expectedExceptions = IllegalArgumentException.class)
	public void invalidTextInputs(String ipAsText) {
		AddressUtils.ipv4ToLong(ipAsText);
	}

	@DataProvider
	public Object[][] invalidLongInputsDataProvider() {
		Object[][] values = { { -1L }, { HIGHEST_IP + 1 } };
		return values;
	}

	@Test(dataProvider = "invalidLongInputsDataProvider", expectedExceptions = IllegalArgumentException.class)
	public void invalidLongInputs(long ipAsLong) {
		AddressUtils.longToIpv4(ipAsLong);
	}

	@DataProvider
	public Object[][] longToIpv4DataProvider() {
		Object[][] values = { { LOWEST_IP, "0.0.0.0" }, { HIGHEST_IP, "255.255.255.255" }, { 255, "0.0.0.255" }, { 2130706433, "127.0.0.1" },
				{ 2130706434, "127.0.0.2" }, };
		return values;
	}

	@Test(dataProvider = "longToIpv4DataProvider")
	public void longToIpv4(long ipAsLong, String expected) {
		assertEquals(AddressUtils.longToIpv4(ipAsLong), expected);
	}

	@Test(dataProvider = "longToIpv4DataProvider")
	public void ipv4ToLong(long expected, String ipAsText) {
		assertEquals(AddressUtils.ipv4ToLong(ipAsText), expected);
	}

	@Test(invocationCount = 10)
	public void backAndForth() {
		long expected = RandomUtils.nextLong(0L, HIGHEST_IP + 1);
		String ip = AddressUtils.longToIpv4(expected);
		assertEquals(AddressUtils.ipv4ToLong(ip), expected);
	}

	@DataProvider
	public Object[][] toCIDRDataProvider() {

		Object[][] values = { { "127.0.0.5", "127.0.0.55", "127.0.0.0/26" }, { "127.0.0.32", "127.0.0.55", "127.0.0.0/26" },
				{ "127.0.0.50", "127.0.0.255", "127.0.0.0/24" }, { "127.0.0.50", "127.0.1.50", "127.0.0.0/23" },
				{ "127.0.4.50", "127.0.32.255", "127.0.0.0/18" }, { "127.0.4.50", "127.0.31.255", "127.0.0.0/19" },
				{ "127.0.4.50", "127.0.8.255", "127.0.0.0/20" }, { "255.255.255.255", "255.255.255.255", "255.255.255.255/32" }, };
		return values;
	}

	@Test(dataProvider = "toCIDRDataProvider")
	public void toCIDR(String ipAsText, String ipBsText, String expected) {
		long ipA = AddressUtils.ipv4ToLong(ipAsText);
		long ipB = AddressUtils.ipv4ToLong(ipBsText);
		assertEquals(AddressUtils.toCIDR(ipA, ipB), expected);
		assertEquals(AddressUtils.toCIDR(ipB, ipA), expected);
	}

	@DataProvider
	public Object[][] numberOfAddressDataProvider() {
		Object[][] values = { { "127.0.0.0/32", 1 }, { "127.0.0.0/31", 2 }, { "127.0.0.0/30", 4 }, { "127.0.0.0/19", 8192 } };
		return values;
	}

	@Test(dataProvider = "numberOfAddressDataProvider")
	public void numberOfAddress(String cidrAddress, int expected) {
		assertEquals(AddressUtils.numberOfAddress(cidrAddress), expected);
	}

}
