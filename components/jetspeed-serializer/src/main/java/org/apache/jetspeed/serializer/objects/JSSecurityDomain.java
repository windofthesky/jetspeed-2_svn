/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
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
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public class JSSecurityDomain
{

 // private int refID;
   
    String name;
    String ownerDomain;
    boolean remote;
    boolean enabled;
    
    public JSSecurityDomain()
    {
        // refID = id;
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSSecurityDomain.class)
    {
    public void write(Object o, OutputElement xml) throws XMLStreamException
    {

        try
        {
            JSSecurityDomain domain = (JSSecurityDomain) o;
            xml.setAttribute("name",domain.getName());
            xml.setAttribute("ownerDomain",domain.getOwnerDomain());
            xml.setAttribute("remote",domain.isRemote());
            xml.setAttribute("enabled",domain.isEnabled());            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void read(InputElement xml, Object o)
    {
        try
        {
            JSSecurityDomain domain = (JSSecurityDomain) o;
            domain.setName(xml.getAttribute("name").toString());
            domain.setOwnerDomain(xml.getAttribute("ownerDomain",(String)null));
            domain.setRemote(xml.getAttribute("remote",false));
            domain.setEnabled(xml.getAttribute("enabled",true));
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

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setNameId(String name)
    {
        this.name = name;
    }

    public boolean isRemote()
    {
        return remote;
    }
    
    public void setRemote(boolean remote)
    {
        this.remote = remote;
    }

    
    public boolean isEnabled()
    {
        return enabled;
    }

    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    
    public String getOwnerDomain()
    {
        return ownerDomain;
    }

    
    public void setOwnerDomain(String ownerDomain)
    {
        this.ownerDomain = ownerDomain;
    }
    
    
}
