package org.esbench.testng;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Resources;

public class ResourcesUtils {
	public static String loadAsString(String locationPattern, Charset encoding) throws IOException {
		URL url = Resources.getResource(locationPattern);
		return Resources.toString(url, encoding);
	}

	public static String loadAsString(String locationPattern) throws IOException {
		return loadAsString(locationPattern, StandardCharsets.UTF_8);
	}
}
