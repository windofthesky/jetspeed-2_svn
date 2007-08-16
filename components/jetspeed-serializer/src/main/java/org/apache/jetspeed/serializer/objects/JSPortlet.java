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
public class JSPortlet
{

    private String name;


    private JSEntities entities = null;


    public JSPortlet()
    {
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

    
    public String getName()
    {
        return name;
    }

        /**
     * @param entities
     *            The entities to set.
     */
    public void setEntities(JSEntities entities)
    {
        this.entities = entities;
    }

    /**
     * @return Returns the entities.
     */
    public JSEntities getEntities()
    {
        return entities;
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSPortlet.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSPortlet g = (JSPortlet) o;
                String s = g.getName();
                if ((s != null) && (s.length() > 0))
                	xml.setAttribute("name", s);
                xml.add(g.entities);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSPortlet g = (JSPortlet) o;
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "unknown"));
                
                
                Object o1 = null;
 

				while (xml.hasNext())
				{
					o1 = xml.getNext(); // mime
					
	                           if (o1 instanceof JSEntities)
	                            g.entities  = (JSEntities) o1;
	            }
                
 
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    };


}