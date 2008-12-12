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

/**
 * Import ProfilingRule
 * 
 *   <profilingRule name = j2>
 *       <description>whatever</description>
 *       <className>org.apache.jetspeed.profile.RuleImpl</className>
 *       <criteria>
 *          ...
 *       </criteria>
 *   </profilingRule>
 *   
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSProfilingRule
{
    private String id;
    private boolean standardRule;
    private String description;
    JSRuleCriterions criterions = new JSRuleCriterions();
    
    
    public JSProfilingRule()
    {        
    }

    
    public boolean isStandardRule()
    {
        return standardRule;
    }

    
    public void setStandardRule(boolean standard)
    {
        this.standardRule = standard;
    }

    
    public String getDescription()
    {
        return description;
    }

    
    public void setDescription(String description)
    {
        this.description = description;
    }

    
    public String getId()
    {
        return id;
    }

    
    public void setId(String id)
    {
        this.id = id;
    }

    
 
	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSProfilingRule.class)
	{
		public void write(Object o, OutputElement xml)
				throws XMLStreamException
		{

			try
			{
				JSProfilingRule g = (JSProfilingRule) o;
				xml.setAttribute("id", g.id);
				xml.setAttribute("standardRule", g.standardRule);
				xml.add( g.description, "description",String.class);
				xml.add(g.criterions);

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void read(InputElement xml, Object o)
		{
			try
			{
				JSProfilingRule g = (JSProfilingRule) o;
				g.id = StringEscapeUtils.unescapeHtml(xml.getAttribute("id","unkwown_id"));
				g.standardRule = xml.getAttribute("standardRule",false);
	               Object o1 = xml.get("description",String.class);
	                if (o1 instanceof String)
	                    g.description = (String) o1;
	                while (xml.hasNext())
	                {
	                    o1 = xml.getNext(); // mime

	                    if (o1 instanceof JSRuleCriterions)
	                        g.criterions = (JSRuleCriterions) o1;
	                }
	        			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};


	/**
	 * @return Returns the criterions.
	 */
	public JSRuleCriterions getCriterions()
	{
		return criterions;
	}


	/**
	 * @param criterions The criterions to set.
	 */
	public void setCriterions(JSRuleCriterions criterions)
	{
		this.criterions = criterions;
	}

    
}