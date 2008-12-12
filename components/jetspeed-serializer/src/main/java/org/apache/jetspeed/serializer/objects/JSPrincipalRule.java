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

public class JSPrincipalRule
{
// int refID;

	private String locator;

	private String rule;

    public JSPrincipalRule()
    {
    }
	public JSPrincipalRule(String locator, String rule)
	{
//		refID = id;
		this.locator = locator;
		this.rule = rule;
	}

	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSPrincipalRule.class)
	{
		public void write(Object o, OutputElement xml)
				throws XMLStreamException
		{
			try
			{
				JSPrincipalRule g = (JSPrincipalRule) o;
				xml.setAttribute("locator", g.locator);
				xml.setAttribute("rule", g.rule);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void read(InputElement xml, Object o)
		{
			try
			{
				JSPrincipalRule g = (JSPrincipalRule) o;
				g.setLocator(StringEscapeUtils.unescapeHtml(xml.getAttribute("locator","unknown")));
				g.setRule(StringEscapeUtils.unescapeHtml(xml.getAttribute("rule","unknown")));
				
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	};




	/**
	 * @return Returns the locator.
	 */
	public String getLocator()
	{
		return locator;
	}

	/**
	 * @param locator The locator to set.
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * @return Returns the rule.
	 */
	public String getRule()
	{
		return rule;
	}

	/**
	 * @param rule The rule to set.
	 */
	public void setRule(String rule)
	{
		this.rule = rule;
	}
}
