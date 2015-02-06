package org.openhab.io.config.rest.beans;

public interface IConfigRest { 
    /** 
     * 得到 hello 信息的接口 . 
     * @return the hello string. 
     */ 
    String getHello(); 
    boolean ChangeName(String siteUrl, String itemID, String strName);
    
    boolean Configure(String uri, String iid, String pid, String value);
}
