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

/**
 * Serialized Name Value Pairs <info> <name>user.first.name</name> <value>Paul</value>
 * </info>
 * 
 * @author <a href="mailto:hajo@bluesunrsie.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
import java.util.ArrayList;
import java.util.List;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class JSNVPElements
{
    private List<JSNVPElement> values = new ArrayList<JSNVPElement>();
    private String itemElementName = "preference";

    public int size()
    {
    	return values.size();
    	
    }
    public JSNVPElements()
    {
    }
    
    public JSNVPElements(String itemElementName)
    {
        this();
        this.itemElementName = itemElementName;
    }
 
    public List<JSNVPElement> getValues()
	{
		return values;
	}

    public void add(JSNVPElement element)
    {
    	values.add(element);
    }
    
    public String getItemElementName()
    {
        return this.itemElementName;
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSNVPElements.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSNVPElements g = (JSNVPElements) o;
                for (JSNVPElement element : g.values)
                {
                    xml.add(element, g.getItemElementName(), JSNVPElement.class);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {

            try
            {
                JSNVPElements g = (JSNVPElements) o;
                while (xml.hasNext())
				{
					JSNVPElement elem = (JSNVPElement)xml.get(g.getItemElementName(), JSNVPElement.class);
					g.add(elem);
				}
            } catch (Exception e)
            {
                /**
                 * while annoying invalid entries in the file should be
                 * just disregarded
                 */
                e.printStackTrace();
            }
        }
    };
}
