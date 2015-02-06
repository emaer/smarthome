package org.openhab.habdroid.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

	
	public static String normalizeUrl(String sourceUrl) {
		String normalizedUrl = "";
		try {
			URL url = new URL(sourceUrl);
			normalizedUrl = url.toString();
			if (!normalizedUrl.endsWith("/"))
				normalizedUrl = normalizedUrl + "/";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return normalizedUrl;
	}
	
}
