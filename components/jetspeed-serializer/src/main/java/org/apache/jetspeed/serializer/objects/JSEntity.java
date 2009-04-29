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
 * Jetspeed Serialized (JS) User
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSEntity
{

    private String id;
    private String principalName;

    private JSEntityPreferences entityPreferences = null;


    public JSEntity()
    {
    }

    
    public void setId(String id)
    {
        this.id = id;
    }

    
    public String getId()
    {
        return id;
    }

        /**
     * @param entityPreferences
     *            The entityPreferences to set.
     */
    public void setEntityPreferences(JSEntityPreferences entityPreferences)
    {
        this.entityPreferences = entityPreferences;
    }

    /**
     * @return Returns the entityPreferences.
     */
    public JSEntityPreferences getEntityPreferences()
    {
        return entityPreferences;
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSEntity.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSEntity g = (JSEntity) o;
                String s = g.getId();
                if ((s != null) && (s.length() > 0))
                	xml.setAttribute("id", s);
                if ((g.entityPreferences != null)  && (g.entityPreferences .size()>0))
                	xml.add(g.entityPreferences);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSEntity g = (JSEntity) o;
                g.id = StringEscapeUtils.unescapeHtml(xml.getAttribute("id", "unknown"));
                
                
                Object o1 = null;
 

				while (xml.hasNext())
				{
					o1 = xml.getNext(); // mime
					
	                           if (o1 instanceof JSEntityPreferences)
	                            g.entityPreferences  = (JSEntityPreferences) o1;
	            }
                
 
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    };


    /**
     * @return the principalName
     */
    public String getPrincipalName()
    {
        return principalName;
    }


    /**
     * @param principalName the principalName to set
     */
    public void setPrincipalName(String principalName)
    {
        this.principalName = principalName;
    }


}