/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.serializer.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.capabilities.Client;
/**
 * Jetspeed Serializer - Client Wrapper
 * <p>
 * Wrapper to process XML representation of a client
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JSClient
{
	// private int refID;

	private String name;

	private int id;

	private int evalOrder;

	private String manufacturer;

	private String model;

	private String preferredMimeTypeID;

	private String userAgentPattern;

	private String version;

	private ArrayList capabilities;

	private ArrayList mimeTypes;

	private JSClientCapabilities capabilitiesString;

	private JSClientMimeTypes mimeTypesString;

	public JSClient()
	{
		// refID = id;
	}

	public JSClient(Client c)
	{
		this.id = c.getClientId();
		this.name = c.getName();

		this.userAgentPattern = c.getUserAgentPattern();
		this.version = c.getVersion();
		this.model = c.getModel();

		this.evalOrder = c.getEvalOrder();
		this.manufacturer = c.getManufacturer();

		capabilities = new ArrayList();
		mimeTypes = new ArrayList();
	}

	public static final String XML_TAG = "Client".intern();
    
	/**
     * All local attributes and list-type classes are bound here,
     * referenced classes should return their own binding.
     * @param binding
     */
	
	public static void setupAliases(XMLBinding binding)
{
        binding.setAlias(JSClient.class, JSClient.XML_TAG);
    }
  
	
	
	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSClient.class)
	{
		public void write(Object o, OutputElement xml)
				throws XMLStreamException
		{

			try
			{
				JSClient g = (JSClient) o;
				xml.setAttribute("name", g.name);
				xml.setAttribute("evalOrder", g.evalOrder);
				xml.setAttribute("preferredMimeTypeID", g.preferredMimeTypeID);
				xml.add( g.userAgentPattern, "userAgentPattern",String.class);
				xml.add(g.version,"version", String.class);
				xml.add(g.model, "model", String.class);
				xml.add(g.manufacturer, "manufacturer", String.class);

				g.capabilitiesString = new JSClientCapabilities(g.putTokens(g.capabilities));
				g.mimeTypesString = new JSClientMimeTypes(g.putTokens(g.mimeTypes));
				xml.add(g.capabilitiesString);
				xml.add(g.mimeTypesString);
				// xml.add(g.groupString);

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void read(InputElement xml, Object o)
		{
			try
			{
				JSClient g = (JSClient) o;
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name",""));
                g.evalOrder = xml.getAttribute("evalOrder",0);
                g.preferredMimeTypeID = StringEscapeUtils.unescapeHtml(xml.getAttribute("preferredMimeTypeID","0"));
                
                g.userAgentPattern = StringEscapeUtils.unescapeHtml((String)xml.get("userAgentPattern",String.class));
                g.version = StringEscapeUtils.unescapeHtml((String)xml.get("version",String.class));
                g.model = StringEscapeUtils.unescapeHtml((String)xml.get("model",String.class));
                g.manufacturer = StringEscapeUtils.unescapeHtml((String)xml.get("manufacturer",String.class));
                g.capabilitiesString = (JSClientCapabilities) xml.getNext();
                g.mimeTypesString = (JSClientMimeTypes) xml.getNext();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};

	/**
	 * @return Returns the capabilities.
	 */
	public List getCapabilities()
	{
		return capabilities;
	}

	/**
	 * @param capabilities
	 *            The capabilities to set.
	 */
	public void setCapabilities(ArrayList capabilities)
	{
		this.capabilities = capabilities;
	}

	/**
	 * @return Returns the evalOrder.
	 */
	public int getEvalOrder()
	{
		return evalOrder;
	}

	/**
	 * @param evalOrder
	 *            The evalOrder to set.
	 */
	public void setEvalOrder(int evalOrder)
	{
		this.evalOrder = evalOrder;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return Returns the manufacturer.
	 */
	public String getManufacturer()
	{
		return manufacturer;
	}

	/**
	 * @param manufacturer
	 *            The manufacturer to set.
	 */
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	/**
	 * @return Returns the mimeTypes.
	 */
	public List getMimeTypes()
	{
		return mimeTypes;
	}

	/**
	 * @param mimeTypes
	 *            The mimeTypes to set.
	 */
	public void setMimeTypes(ArrayList mimeTypes)
	{
		this.mimeTypes = mimeTypes;
	}

	/**
	 * @return Returns the model.
	 */
	public String getModel()
	{
		return model;
	}

	/**
	 * @param model
	 *            The model to set.
	 */
	public void setModel(String model)
	{
		this.model = model;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the preferredMimeTypeID.
	 */
	public String getPreferredMimeTypeID()
	{
		return preferredMimeTypeID;
	}

	/**
	 * @param preferredMimeTypeID
	 *            The preferredMimeTypeID to set.
	 */
	public void setPreferredMimeTypeID(String preferredMimeTypeID)
	{
		this.preferredMimeTypeID = preferredMimeTypeID;
	}

	/**
	 * @return Returns the userAgentPattern.
	 */
	public String getUserAgentPattern()
	{
		return userAgentPattern;
	}

	/**
	 * @param userAgentPattern
	 *            The userAgentPattern to set.
	 */
	public void setUserAgentPattern(String userAgentPattern)
	{
		this.userAgentPattern = userAgentPattern;
	}

	/**
	 * @return Returns the version.
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	private String append(JSCapability capability)
	{
		return capability.getName();
	}

	private String append(JSMimeType mime)
	{
		return mime.getName();
	}

	private String append(Object s)
	{
		if (s instanceof JSCapability)
			return append((JSCapability) s);
		if (s instanceof JSMimeType)
			return append((JSMimeType) s);

		return s.toString();
	}

	private String putTokens(ArrayList _list)
	{
		if ((_list == null) || (_list.size() == 0))
			return "";
		boolean _start = true;
		Iterator _it = _list.iterator();
		StringBuffer _sb = new StringBuffer();
		while (_it.hasNext())
		{
			if (!_start)
				_sb.append(',');
			else
				_start = false;

			_sb.append(append(_it.next()));
		}
		return _sb.toString();
	}

	public JSClientCapabilities getCapabilitiesString()
	{
		return capabilitiesString;
	}

	public JSClientMimeTypes getMimeTypesString()
	{
		return mimeTypesString;
	}


	
}
