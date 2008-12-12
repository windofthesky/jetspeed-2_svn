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

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.capabilities.MediaType;

public class JSMediaType
{
	// private int refID;

	private String name;

	private int id;

	private String characterSet;

	private String title;

	private String description;

    private ArrayList capabilities;

    private ArrayList mimeTypes;

    private JSClientCapabilities capabilitiesString;

    private JSClientMimeTypes mimeTypesString;


	public JSMediaType()
	{
		// refID = id;
	}

	public JSMediaType(MediaType c)
	{
		this.id = c.getMediatypeId();
		this.name = c.getName();

		this.characterSet = c.getCharacterSet();
		this.title = c.getTitle();
		this.description = c.getDescription();
        capabilities = new ArrayList();
        mimeTypes = new ArrayList();
        
	}

	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSMediaType.class)
	{
		public void write(Object o, OutputElement xml)
				throws XMLStreamException
		{

			try
			{
				JSMediaType g = (JSMediaType) o;
                /** attributes here */

				xml.setAttribute("name", g.name);

                /** named fields HERE */

                xml.add( g.characterSet, "charcterSet",String.class);
				xml.add(g.title,"title", String.class);
				xml.add(g.description, "description", String.class);

                /** implicitly named (through binding) fields here */

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
				JSMediaType g = (JSMediaType) o;
               g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name",""));

                /** named fields HERE */
                Object o1 = xml.get("charcterSet",String.class); //characterSet
                if ((o1 != null) && (o1 instanceof String))
                	g.characterSet = StringEscapeUtils.unescapeHtml((String)o1);
                g.title = StringEscapeUtils.unescapeHtml((String)xml.get("title", String.class)); //title;
                g.description  = StringEscapeUtils.unescapeHtml((String)xml.get("description", String.class)); //description;

                while (xml.hasNext())
                {
                    o1 = xml.getNext(); // mime

                    if (o1 instanceof JSClientCapabilities)
                        g.capabilitiesString = (JSClientCapabilities) o1; //capabilitiesString;
                    else
                        if (o1 instanceof JSClientMimeTypes)
                            g.mimeTypesString  = (JSClientMimeTypes)o1; //mimeTypesString;
                }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};


	/**
	 * @return Returns the characterSet.
	 */
	public String getCharacterSet()
	{
		return characterSet;
	}

	/**
	 * @param characterSet The characterSet to set.
	 */
	public void setCharacterSet(String characterSet)
	{
		this.characterSet = characterSet;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String titel)
	{
		this.title = titel;
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

	public JSClientCapabilities getCapabilitiesString()
	{
		return capabilitiesString;
	}

	public JSClientMimeTypes getMimeTypesString()
	{
		return mimeTypesString;
	}

}
