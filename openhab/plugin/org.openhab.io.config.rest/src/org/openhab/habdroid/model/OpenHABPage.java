package org.openhab.habdroid.model;


public class OpenHABPage {
	private String pageUrl;
	private int widgetListPosition;
	
	public OpenHABPage (String pageUrl, int widgetListPosition) {
		this.pageUrl = pageUrl;
		this.widgetListPosition = widgetListPosition;
	}
	
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public int getWidgetListPosition() {
		return widgetListPosition;
	}
	public void setWidgetListPosition(int widgetListPosition) {
		this.widgetListPosition = widgetListPosition;
	}

}
