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

public class JSPrincipalAssociation
{
    private static final long serialVersionUID = -7954617309602239376L;
    
    private String name;
    private String fromName;
    private String fromType;
    private String toName;
    private String toType;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setFromName(String fromName)
    {
        this.fromName = fromName;        
    }
    
    public String getFromName()
    {
        return this.fromName;
    }
    
    public void setFromType(String fromType)
    {
        this.fromType = fromType;
    }
    
    public String getFromType()
    {
        return this.fromType;
    }
    
    public void setToName(String toName)
    {
        this.toName = toName;        
    }
    
    public String getToName()
    {
        return this.toName;
    }
    
    public void setToType(String toType)
    {
        this.toType = toType;
    }
    
    public String getToType()
    {
        return this.toType;
    }
    
    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSPrincipalAssociation.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSPrincipalAssociation g = (JSPrincipalAssociation) o;
                xml.setAttribute("name", g.getName());
                xml.setAttribute("fromName", g.getFromName());
                xml.setAttribute("fromType", g.getFromType());
                xml.setAttribute("toName", g.getToName());
                xml.setAttribute("toType", g.getToType());
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
                JSPrincipalAssociation g = (JSPrincipalAssociation) o;
                g.setName(xml.getAttribute("name").toString());
                g.setFromName(xml.getAttribute("fromName", ""));
                g.setFromType(xml.getAttribute("fromType", ""));
                g.setToName(xml.getAttribute("toName", ""));
                g.setToType(xml.getAttribute("toType", ""));
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    };
    
}
