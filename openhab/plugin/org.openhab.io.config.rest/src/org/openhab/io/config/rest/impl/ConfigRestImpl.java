package org.openhab.io.config.rest.impl;

import org.openhab.io.config.rest.beans.IConfigRest;


public class ConfigRestImpl implements IConfigRest { 

	@Override 
	public String getHello() {
		String strMainPath = System.getProperty("user.dir");
		String strItemsPath = strMainPath +
				"/configurations/items/default.items";
		String strSitemaps = strMainPath +
				"/configurations/items/default.sitemap";
				
		String strRest = ConfigRestImpl.class.getResource("/").toString() + "\r\n"
				+ strItemsPath + "\n" +
				strSitemaps;
//		readFileByLines(strItemsPath);
//		appendMethodA(strItemsPath, "TestWriteFile");
		return strRest; 
	}
	
	@Override
	public boolean ChangeName(String siteUrl, String itemID, String strName) {
		
//		ChangeHabItemImpl changeHabItemImpl = new ChangeHabItemImpl();
//		System.out.println("-------------------------ChangeName.strName code:" + ManageCharset.getEncoding(strName));
//		changeHabItemImpl.changeRoomName(siteUrl, itemID, strName);
		
		return true;
	}

	@Override
	public boolean Configure(String uri, String iid, String pid, String value) {
		ChangeHabItemImpl changeHabItemImpl = new ChangeHabItemImpl();
		if(!changeHabItemImpl.modifyItemInfo(uri, iid, pid, value)){
			return false;
		}
		return true;
	}
}
