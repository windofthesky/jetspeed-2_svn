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

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;

public class JSSimpleIDName 
{
	// private int refID;

	private String name;

	private int id;
	
	public JSSimpleIDName()
	{
		// refID = id;
	}
	
	public JSSimpleIDName(int id, String name)
	{
		this.id = id;
		this.name = name;
		// refID = id;
	}

	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSSimpleIDName.class)
	{
	public void write(Object o, OutputElement xml) throws XMLStreamException
	{

		try
		{
			JSSimpleIDName g = (JSSimpleIDName) o;
			xml.setAttribute("name",g.name );
			xml.setAttribute("id",g.id);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void read(InputElement xml, Object o)
	{
		try
		{
			JSSimpleIDName g = (JSSimpleIDName) o;
			g.setName(StringEscapeUtils.unescapeHtml(xml.getAttribute("name","Unknown")));
			g.setId(xml.getAttribute("id",0));
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	};
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

	
}
