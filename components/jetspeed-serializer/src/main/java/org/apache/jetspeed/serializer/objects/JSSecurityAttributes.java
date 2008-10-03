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

import org.apache.jetspeed.security.SecurityAttributeType;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;


public class JSSecurityAttributes extends JSNVPElements
{
    private String category = SecurityAttributeType.JETSPEED_CATEGORY;
    
    public JSSecurityAttributes()
    {
        super("SecurityAttribute");
    }
    
    public JSSecurityAttributes(String category)
    {
        this();
        this.category = category;
    }
    
    public String getCategory()
    {
        return this.category;
    }
    
    public void setCategory(String category)
    {
        this.category = category;
    }
    
    /***************************************************************************
     * SERIALIZER
     */
    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSSecurityAttributes.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSSecurityAttributes g = (JSSecurityAttributes) o;
                xml.setAttribute("category", g.getCategory());
                
                for (JSNVPElement element : g.getValues())
                {
                    xml.add(element, g.getItemElementName(), JSNVPElement.class);
                }
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSSecurityAttributes g = (JSSecurityAttributes) o;
                g.setCategory(xml.getAttribute("category", SecurityAttributeType.JETSPEED_CATEGORY));
                
                while (xml.hasNext())
                {
                    JSNVPElement elem = (JSNVPElement)xml.get(g.getItemElementName(), JSNVPElement.class);
                    g.add(elem);
                }
            } 
            catch (Exception e)
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
