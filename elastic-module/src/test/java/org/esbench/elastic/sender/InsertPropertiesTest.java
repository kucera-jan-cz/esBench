package org.esbench.elastic.sender;

import static org.esbench.cmd.CommandPropsConstants.INDEX_OPT;
import static org.esbench.cmd.CommandPropsConstants.TYPE_OPT;
import static org.esbench.elastic.sender.InsertProperties.DOCS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.esbench.core.DefaultProperties;
import org.esbench.core.ResourceUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InsertPropertiesTest {

	@Test
	public void valid() throws IOException {
		Properties defaults = ResourceUtils.asProperties("default.properties");
		InsertProperties ins = new InsertProperties(toProps(defaults, INDEX_OPT, "index", TYPE_OPT, "type", DOCS, "1"));
		assertEquals(ins.getIndex(), "index");
		assertEquals(ins.getType(), "type");
		assertEquals(ins.getWorkloadIndex(), "index");
		assertEquals(ins.getWorkloadType(), "type");
		assertEquals(ins.getDocPerIteration(), 1);
	}

	@DataProvider
	public Object[][] invalidDataProvider() {
		Object[][] values = { { toProps() }, { toProps(INDEX_OPT, "") }, { toProps(INDEX_OPT, "index") }, { toProps(INDEX_OPT, "index", TYPE_OPT, "") } };
		return values;
	}

	@Test(dataProvider = "invalidDataProvider", expectedExceptions = { IllegalArgumentException.class, NullPointerException.class })
	public void invalid(DefaultProperties defaults) {
		new InsertProperties(defaults);
	}

	private DefaultProperties toProps(String... keysAndValues) {
		return toProps(new Properties(), keysAndValues);
	}

	private DefaultProperties toProps(Properties defaults, String... keysAndValues) {
		Properties props = new Properties();
		for(int i = 0; i < keysAndValues.length; i += 2) {
			props.put(keysAndValues[i], keysAndValues[i + 1]);
		}
		return new DefaultProperties(props, defaults);
	}
}
