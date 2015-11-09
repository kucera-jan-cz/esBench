package org.esbench.generator.field.type.ipv4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.esbench.generator.field.AbstractFieldFactory;
import org.esbench.generator.field.FieldConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ipv4FieldFactory extends AbstractFieldFactory<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Ipv4FieldFactory.class);
	private static final int MAXIMUM_CACHED_IPS = 1_000_000;
	// Represents CIDR address with only one IP, f.e 127.0.0.1/32
	private static final String HIGHEST_CIDR_NETMASK = "255.255.255.255";
	private final String[] addresses;

	public Ipv4FieldFactory(String... cidrAddresses) {
		super(FieldConstants.SINGLE_TOKEN);
		addresses = convertToAddresses(cidrAddresses);
	}

	@Override
	public String newInstance(int uniqueId) {
		return addresses[uniqueId % addresses.length];
	}

	private String[] convertToAddresses(String[] cidrAddresses) {
		SubnetInfo[] subnets = translateToSubnets(cidrAddresses);
		validateSubnets(MAXIMUM_CACHED_IPS, subnets);

		List<String> addressesAsList = new ArrayList<>();
		for(SubnetInfo subnet : subnets) {
			addressesAsList.add(subnet.getNetworkAddress());
			addressesAsList.addAll(Arrays.asList(subnet.getAllAddresses()));
			if(!HIGHEST_CIDR_NETMASK.equals(subnet.getNetmask())) {
				addressesAsList.add(subnet.getBroadcastAddress());
			}
		}
		return addressesAsList.toArray(new String[0]);
	}

	private void validateSubnets(int maximumAllowedIps, SubnetInfo[] subnets) {
		int totalIps = calculateTotal(subnets);
		if(maximumAllowedIps < totalIps) {
			LOGGER.error("Limit exceeded for {}", Arrays.toString(subnets));
			throw new IllegalArgumentException(String.format("Only %d addresses allowed (counted %d)", maximumAllowedIps, totalIps));
		}
	}

	private SubnetInfo[] translateToSubnets(String[] cidrAddresses) {
		SubnetInfo[] infos = new SubnetInfo[cidrAddresses.length];
		for(int i = 0; i < cidrAddresses.length; i++) {
			String cidrAddress = cidrAddresses[i];
			infos[i] = new SubnetUtils(cidrAddress).getInfo();
		}
		return infos;
	}

	private int calculateTotal(SubnetInfo[] addresses) {
		int totalUniqueAddresses = 0;
		for(SubnetInfo info : addresses) {
			totalUniqueAddresses += HIGHEST_CIDR_NETMASK.equals(info.getNetmask()) ? 1 : 2;
			totalUniqueAddresses += info.getAddressCount();
		}
		return totalUniqueAddresses;
	}

}
