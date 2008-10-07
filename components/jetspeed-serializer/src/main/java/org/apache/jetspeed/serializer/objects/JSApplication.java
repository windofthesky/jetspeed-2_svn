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
public class JSApplication
{

    private String name;

    private String id;



    private JSPortlets portlets = null;


    public JSApplication()
    {
    }


    public String getID()
    {
        return id;
    }

 
    public void setID(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

 
    public String getName()
    {
        return name;
    }

  
  
    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSApplication.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSApplication g = (JSApplication) o;
                String s = g.getName();
                if ((s != null) && (s.length() > 0))
                	xml.setAttribute("name", s);
                
				xml.add(g.id, "ID",String.class);
                xml.add(g.portlets);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSApplication g = (JSApplication) o;
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "unknown"));
                
                
 
                Object o1 = xml.get("ID",String.class);
                if (o1 instanceof String) g.id = StringEscapeUtils.unescapeHtml((String) o1);

				while (xml.hasNext())
				{
					o1 = xml.getNext(); // mime
					
					
					if (o1 instanceof JSPortlets)
					{
						g.portlets = (JSPortlets) o1;
					}
                }
                
 
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    };


 


	public JSPortlets getPortlets()
	{
		return portlets;
	}

	public void setPortlets(JSPortlets portlets)
	{
		this.portlets = portlets;
	}

}