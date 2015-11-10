package org.esbench.generator.field.type.ipv4;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.esbench.generator.field.AbstractFieldFactory;
import org.esbench.generator.field.FiniteValueFieldFactory;
import org.esbench.generator.field.type.numeric.LongFieldFactory;
import org.esbench.generator.field.utils.AddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ipv4FieldFactory extends AbstractFieldFactory<String> implements FiniteValueFieldFactory<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Ipv4FieldFactory.class);
	private final String address;
	private final LongFieldFactory longFactory;

	public Ipv4FieldFactory(String address) {
		this.address = address;
		this.longFactory = cidrToLongFactory(address);
	}

	@Override
	public String newInstance(int uniqueId) {
		return AddressUtils.longToIpv4(longFactory.newInstance(uniqueId));
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(address).build();
	}

	private LongFieldFactory cidrToLongFactory(String cidrAddress) {
		SubnetInfo info = new SubnetUtils(cidrAddress).getInfo();
		long lowestAddrAsLong = AddressUtils.ipv4ToLong(info.getNetworkAddress());
		long modulo = AddressUtils.numberOfAddress(cidrAddress);
		LongFieldFactory factory = new LongFieldFactory(lowestAddrAsLong, 1L, modulo);
		return factory;
	}

	@Override
	public long uniqueValues() {
		return longFactory.getModulo();
	}

}
