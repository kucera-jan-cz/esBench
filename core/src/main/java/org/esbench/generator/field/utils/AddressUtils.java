package org.esbench.generator.field.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

import com.google.common.net.InetAddresses;

public final class AddressUtils {
	private static final long HIGHEST_IP = 4294967295L;
	private static final int LOWEST_IP = 0;
	private static final Pattern IP_PATTER = Pattern.compile("\\.");
	private static final int IP_SEGMENTS = 4;

	private AddressUtils() {

	}

	public static String longToIpv4(long ipAsLong) {
		Validate.inclusiveBetween(LOWEST_IP, HIGHEST_IP, ipAsLong);
		int octetA = (int) ((ipAsLong >> 24) % 256);
		int octetB = (int) ((ipAsLong >> 16) % 256);
		int octetC = (int) ((ipAsLong >> 8) % 256);
		int octetD = (int) ((ipAsLong) % 256);
		return octetA + "." + octetB + "." + octetC + "." + octetD;
	}

	public static long ipv4ToLong(String ipAsText) {
		if(!InetAddresses.isInetAddress(ipAsText)) {
			throw new IllegalArgumentException("failed to parse ip [" + ipAsText + "], not a valid ip address");
		}
		String[] octets = IP_PATTER.split(ipAsText);
		return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16) + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
	}

	public static String toCIDR(long ipA, long ipB) {
		int shift = 24;
		int routingPrefix = 32;
		int[] addressOctets = new int[4];

		for(int i = 0; i < IP_SEGMENTS; i++) {
			int octetA = (int) ((ipA >> shift) % 256);
			int octetB = (int) ((ipB >> shift) % 256);
			if(octetA == octetB) {
				addressOctets[i] = octetA;
			} else {
				int higherOctet = Math.max(octetA, octetB);
				routingPrefix = Integer.numberOfLeadingZeros(higherOctet << shift);
				break;
			}
			shift -= 8;
		}
		String address = StringUtils.join(addressOctets, '.');
		return address + '/' + routingPrefix;
	}

	public static long numberOfAddress(String cidrAddress) {
		SubnetInfo info = new SubnetUtils(cidrAddress).getInfo();
		// Need to use network and broadcast address to get whole range
		long lowestAddrAsLong = AddressUtils.ipv4ToLong(info.getNetworkAddress());
		long highestAddrAsLong = AddressUtils.ipv4ToLong(info.getBroadcastAddress());

		return (highestAddrAsLong - lowestAddrAsLong) + 1;
	}
}
