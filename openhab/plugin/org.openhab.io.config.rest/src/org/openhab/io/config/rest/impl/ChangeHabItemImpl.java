package org.openhab.io.config.rest.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetDataSource;
import org.openhab.habdroid.model.OpenHABWidgetMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ChangeHabItemImpl {

	StringBuilder sitemap = new StringBuilder();
	StringBuilder items = new StringBuilder();
	final String space = " ";
	ArrayList<String> ids = new ArrayList<String>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String sitmap = "http://localhost:8080/rest/sitemaps/default/default/";
		ChangeHabItemImpl t = new ChangeHabItemImpl();
//		t.getSitemaps(sitmap);
//		t.changeItemName(sitmap, "default_0_0", "起居室");
//		t.switchItemPos(sitmap, "default_0", "0000_0");
		t.switchAndChangeItem(sitmap, "0000_0", "default_0", "起居室");
	}
	
	
	public void getSitemaps(String sitemapUrl){
		modifyItemInfo(sitemapUrl, null, null, null);
	}
	
	public void changeItemName(String sitemapUrl, String itemId, String newName){
		modifyItemInfo(sitemapUrl, itemId, null, newName);
	}
	
	public void switchItemPos(String sitemapUrl, String pid, String itemId){
		
		modifyItemInfo(sitemapUrl, itemId, pid, null);
	}
	
	public void switchAndChangeItem(String sitemapUrl, String itemId, String pid, String newName){
		
		modifyItemInfo(sitemapUrl, itemId, pid, newName);
		
	}
	
	/**
	 * modify item info, 1. if itemId is empty, we just parse siteMap and items;
	 * 2. if item
	 * @param sitemapUrl
	 * @param itemId
	 * @param pid
	 * @param newName
	 */
	public boolean modifyItemInfo(String sitemapUrl, String itemId, String pid, String newName){
		
		sitemapUrl = MyTool.normalizeUrl(sitemapUrl);
		if(MyTool.isStrEmpty(sitemapUrl))
			return false;
		
		OpenHABWidget child = getItemById(sitemapUrl, itemId);
		
		OpenHABWidgetDataSource openHABWidgetDataSource = new OpenHABWidgetDataSource();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document;
			String content = HttpClientUtil.get(sitemapUrl, null);
			// TODO: fix crash with null content
			document = builder.parse(new ByteArrayInputStream(content
					.getBytes("UTF-8")));
			Node rootNode = document.getFirstChild();
			openHABWidgetDataSource.setSourceNode(rootNode);
		
			sitemap.append("sitemap" + space);
			sitemap.append(openHABWidgetDataSource.getId() + space);
			String dataSourceTitle = openHABWidgetDataSource.getTitle();
			if (null != dataSourceTitle && dataSourceTitle.length() != 0)
				sitemap.append("label=" + "\"" + dataSourceTitle + "\"");
			sitemap.append("\n{\n");

			ArrayList<OpenHABWidget> siteWidgets = openHABWidgetDataSource.getWidgets();
			for (OpenHABWidget w : siteWidgets) {
				
				String ss = getWidgetString(w, pid, child, newName);
				if(null != ss && ss.length() != 0)
					sitemap.append(ss);

				
				items.append(getItemsString( w, pid, child, newName));
			}
			
			sitemap.append("\n}");
			
			String strMainPath = System.getProperty("user.dir");
			String strItemsPath = strMainPath +
					"/configurations/items/default.items";
			String strSitemapsPath = strMainPath +
					"/configurations/sitemaps/default.sitemap";
			
			if (!WriteToFile.CoverWriteMethod(strSitemapsPath, sitemap.toString(), "UTF-8"))
				return false;
			if (!WriteToFile.CoverWriteMethod(strItemsPath, items.toString(), "UTF-8"))
				return false;

			return true;
//			System.out.println(sitemap);
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println(items);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private OpenHABWidget getItemById(String siteUrl, String id){
		
		if(null == siteUrl || siteUrl.length() ==0)
			return null;
		
		if(null == id || id.length() ==0)
			return null;
		
		ArrayList<OpenHABWidget>ws = getWidgets(siteUrl);
		for (OpenHABWidget w : ws) {
			if(id.equalsIgnoreCase(w.getWidgetId()))
				return w;
			if(w.hasLinkedPage()){
				ArrayList<OpenHABWidget> ts = getWidgets(w.getLinkedPage().getLink());
				for (OpenHABWidget t : ts) {
					if(id.equalsIgnoreCase(t.getWidgetId()))
						return t;
				}
			}
		}
		
		return null;
	}
	
	private void changeWidgetName(OpenHABWidget w, String wid, String newName){
		
		
		if(null == w ||
				MyTool.isStrEmpty(wid) ||
				MyTool.isStrEmpty(newName))
			return;
		if(wid.equalsIgnoreCase(w.getWidgetId()))
			w.setLabel(newName);
		
	}
	
	
	private String getItemsString(OpenHABWidget w, String parentId, OpenHABWidget child, String newName){
		
		if(!w.hasLinkedPage() || !w.hasItem())
			return "";
		StringBuilder str = new StringBuilder();
		
		OpenHABItem tempItem = w.getItem();
		str.append(w.getType() + space + tempItem.getName() + "\n");
		
		if(null != child ){
			
			if(isSwitchItem(child, parentId) &&
					parentId.equalsIgnoreCase(w.getWidgetId()) ){
				
				if(isChangeItemName(child, newName))
					child.setLabel(newName);
				
				str.append(getSingleItemString(child, tempItem));
				
			}				
		
		}
		
		ArrayList<OpenHABWidget> tempList = getWidgets(w.getLinkedPage().getLink());
		for (OpenHABWidget openHABWidget : tempList) {
			
			if(openHABWidget.hasLinkedPage()){
				getItemsString(openHABWidget, parentId, child, newName);
			}else{
				
				if (!openHABWidget.hasChildren()) {
					if (openHABWidget.hasItem()) {
						
						if(null != child){
							
							if(isChangeItemName(child, newName) && 
									openHABWidget.getWidgetId().equalsIgnoreCase(child.getWidgetId()))
								openHABWidget.setLabel(newName);
							
							if(isSwitchItem(child, parentId) && 
									openHABWidget.getWidgetId().equalsIgnoreCase(child.getWidgetId())){
								str.append("");
							}
							//not switch item or not found the child
							else
								str.append(getSingleItemString(openHABWidget, tempItem));
							
						}
						//just parse items
						else
							str.append(getSingleItemString(openHABWidget, tempItem));
							
					}
				}else{
					for (OpenHABWidget c : openHABWidget.getChildren()) {
						getItemsString(c, parentId, child, newName);
					}
				}
				
			}
			
		}
		
		str.append("\n\n");
		
		return str.toString();
		
	}
	
	private String getSingleItemString(OpenHABWidget w, OpenHABItem parentItem){
				
		String itemtype = w.getItem().getType();
		itemtype = itemtype.substring(0, itemtype.length()-4);
		return itemtype + space 
				+ w.getItem().getName() + space + "\""
				+ w.getLabel() + "\"" + space
				+ "   (" + parentItem.getName() + ")\n";
		
	}
	
	private ArrayList<OpenHABWidget> getWidgets(String url){
		
		ArrayList<OpenHABWidget> tempList = new ArrayList<OpenHABWidget>();
		try {
			String str = HttpClientUtil.get(url, null);
			tempList = parseContent(str);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempList;
		
	}

	private ArrayList<OpenHABWidget> parseContent(String content){
		
		OpenHABWidgetDataSource tempSource = new OpenHABWidgetDataSource();
		ArrayList<OpenHABWidget> tempList = new ArrayList<OpenHABWidget>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document;
			// TODO: fix crash with null content
			document = builder.parse(new ByteArrayInputStream(content
					.getBytes("UTF-8")));
			Node rootNode = document.getFirstChild();
			tempSource.setSourceNode(rootNode);
			tempList = tempSource.getWidgets();
			
				
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempList;
		
	}
	
	private String getWidgetString(OpenHABWidget w, String parentId, OpenHABWidget child, String newName) {

		if (!ids.contains(w.getWidgetId())) {
			ids.add(w.getWidgetId());
			StringBuilder str = new StringBuilder();

			if (!w.hasChildren()) {
				if(isChangeItemName(child, newName))
					changeWidgetName(w, child.getWidgetId(), newName);
				if(isSwitchItem(child, parentId) &&
						w.getWidgetId().equalsIgnoreCase(child.getWidgetId()))
					return "";				
				
				str.append(getSingleWidgetString(w));
			} else {

				str.append("\n    " + w.getType() + " label=" + w.getLabel() + "{\n");
				
				if(isChangeItemName(child, newName))
					changeWidgetName(child, w.getWidgetId(), newName);
				
				if(isSwitchItem(child, parentId) &&
						w.getWidgetId().equalsIgnoreCase(parentId)){
					
					if(isChangeItemName(child, newName))
						changeWidgetName(child, child.getWidgetId(), newName);
					str.append("       " + getSingleWidgetString(child));
				}
				
				for (OpenHABWidget c : w.getChildren()) {
					str.append("       " + getWidgetString(c, parentId, child, newName));
				}
				
				str.append("\n    }\n");

			}
			return str.toString();
		}

		return null;

	}

	private String getSingleWidgetString(OpenHABWidget w){
		
		StringBuilder str = new StringBuilder();		
		
		if (w.hasItem()) {
			
			if("Setpoint".equalsIgnoreCase(w.getType())){
				str.append(w.getType() + " item=" + w.getLabel()
						+ " label=\"" + w.getLabel() + "\"" + space
						+ "minValue=" + w.getMinValue() + space
						+ "maxValue=" + w.getMaxValue() + space
						+ "step=" + w.getStep());
			}else{
				OpenHABItem item = w.getItem();
				str.append(w.getType() + space);
				str.append("item=" + item.getName() + space);

				str.append("label=\"" + w.getLabel() + "\"" + space + space);
				str.append("icon=\"" + w.getIcon() + "\"" + space);
			}
			
		}else{
			
			if("Setpoint".equalsIgnoreCase(w.getType())){
				
				str.append(w.getType() + " item=" + w.getLabel()
						+ " label=\"" + w.getLabel() + "\""
						+ "minValue=" + w.getMinValue() + space
						+ "maxValue=" + w.getMaxValue() + space
						+ "step=" + w.getStep());
				
			}else{
				str.append(w.getType() + " label=\"" + w.getLabel() + "\"");
			}
			
			
		}

		if (w.hasMappings()) {
			// mappings=[0=off, 1=TV, 2=Dinner, 3=Reading]
			str.append(" mappings=[");
			int size = w.getMappings().size();
			for (int i = 0; i < size; i++) {
				OpenHABWidgetMapping mapping = w.getMappings().get(i);
				str.append(mapping.getLabel() + "=\""
						+ mapping.getCommand() + "\"");
				if (i != (size - 1))
					str.append(", ");
			}
			str.append("]");
		}
		str.append("\n");
		
		return str.toString();
		
	}
	
	private boolean isChangeItemName(OpenHABWidget w, String newName){
		
		if(null == w || 
				MyTool.isStrEmpty(newName) || 
				MyTool.isStrEmpty(w.getWidgetId()))
			return false;
		
		return true;
		
	}
	
	private boolean isSwitchItem(OpenHABWidget w, String pid){
		
		if(null == w ||
				MyTool.isStrEmpty(pid) || 
				MyTool.isStrEmpty(w.getWidgetId()))
			return false;
		
		return true;
		
	}
	
	private boolean isJustParse(String pid, String itemId, String newName){
		
		if(MyTool.isStrEmpty(pid) && 
				MyTool.isStrEmpty(itemId) &&
				MyTool.isStrEmpty(newName))
			return true;
		
		return false;
		
	}

}
