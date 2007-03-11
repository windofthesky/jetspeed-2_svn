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

import javolution.xml.*;
import javolution.xml.stream.XMLStreamException;

import java.util.prefs.Preferences;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.security.Role;

public class JSGroup 
{
	// private int refID;

	String name;

	public JSGroup()
	{
		// refID = id;
	}

	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSGroup.class)
	{
	public void write(Object o, OutputElement xml) throws XMLStreamException
	{

		try
		{
			JSGroup g = (JSGroup) o;
			xml.addText(g.getName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void read(InputElement xml, Object o)
	{
		try
		{
			JSGroup g = (JSGroup) o;
			g.setName(StringEscapeUtils.unescapeHtml(xml.getText().toString()));
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

}
