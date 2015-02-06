package org.openhab.io.config.rest.impl;

import java.net.MalformedURLException;
import java.net.URL;

public class MyTool {

	public static boolean isStrEmpty(String str){
		if(null == str)
			return true;
		if(str.length() == 0)
			return true;
		return false;
	}
	
	public static String normalizeUrl(String sourceUrl) {
		String normalizedUrl = "";
		try {
			URL url = new URL(sourceUrl);
			normalizedUrl = url.toString();
//			if (!normalizedUrl.endsWith("/"))
//				normalizedUrl = normalizedUrl + "/";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return normalizedUrl;
	}
}
