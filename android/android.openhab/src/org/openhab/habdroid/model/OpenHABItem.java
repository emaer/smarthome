/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.habdroid.model;

import java.io.Serializable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a class to hold basic information about openHAB Item.
 * 
 * @author Victor Belov
 *
 */

public class OpenHABItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7974252266509543765L;
	private String name;
	private String type;
	private String state;
	private String link;

	public OpenHABItem(Node startNode) {
		if (startNode.hasChildNodes()) {
			NodeList childNodes = startNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i ++) {
				Node childNode = childNodes.item(i);
				if (childNode.getNodeName().equals("type")) {
					this.setType(childNode.getTextContent());
				} else if (childNode.getNodeName().equals("name")) {
					this.setName(childNode.getTextContent());
				} else if (childNode.getNodeName().equals("state")) {
					if (childNode.getTextContent().equals("Uninitialized")) {
						this.setState("0");
					} else {
						this.setState(childNode.getTextContent());
					}
				} else if (childNode.getNodeName().equals("link")) {					
					this.setLink(childNode.getTextContent());
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
