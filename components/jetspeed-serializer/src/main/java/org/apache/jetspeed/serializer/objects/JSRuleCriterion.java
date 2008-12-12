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
import org.apache.jetspeed.profiler.rules.RuleCriterion;

public class JSRuleCriterion
{
	// private int refID;

	private String name;


	private String type;

	private String value;

	private int fallBackOrder;

	private int fallBackType;

	public JSRuleCriterion()
	{
		// refID = id;
	}

	public JSRuleCriterion(RuleCriterion c)
	{
		this.name = c.getName();
		this.type = c.getType();
		this.value = c.getValue();
		this.fallBackOrder = c.getFallbackOrder();
		this.fallBackType = c.getFallbackType();
	}

	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSRuleCriterion.class)
	{
		public void write(Object o, OutputElement xml)
				throws XMLStreamException
		{

			try
			{
				JSRuleCriterion g = (JSRuleCriterion) o;
				xml.setAttribute("name", g.name);
				xml.add( g.type, "type",String.class);
				xml.add(g.value,"value", String.class);
				xml.add(new Integer(g.fallBackOrder), "fallBackOrder", Integer.class);
				xml.add(new Integer(g.fallBackType), "fallBackType", Integer.class);

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
				JSRuleCriterion g = (JSRuleCriterion) o;
				g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name","unknown_name"));
                Object o1 = xml.get("type",String.class);
                if (o1 instanceof String) g.type = StringEscapeUtils.unescapeHtml((String) o1);
                o1 = xml.get("value",String.class);
                if (o1 instanceof String) g.value = StringEscapeUtils.unescapeHtml((String) o1);

	              o1 = xml.get("fallBackOrder",String.class);
	                if (o1 instanceof String)
	                    g.fallBackOrder = Integer.parseInt(((String) o1));
	                o1 = xml.get("fallBackType",String.class);
	                if (o1 instanceof String)
	                    g.fallBackType = Integer.parseInt(((String) o1));

	                while (xml.hasNext())
	                {
	                }
	  			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};


	/**
	 * @return Returns the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return Returns the fallBackOrder.
	 */
	public int getFallBackOrder()
	{
		return fallBackOrder;
	}

	/**
	 * @param fallBackOrder The fallBackOrder to set.
	 */
	public void setFallBackOrder(int fallBackOrder)
	{
		this.fallBackOrder = fallBackOrder;
	}
	/**
	 * @return Returns the fallBackType.
	 */
	public int getFallBackType()
	{
		return fallBackType;
	}

	/**
	 * @param fallBackTye The fallBackType to set.
	 */
	public void setFallBackType(int fallBackType)
	{
		this.fallBackType = fallBackType;
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
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}


}
