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

import java.util.Map;

import javolution.xml.*;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.security.SecurityAttribute;

/**
 * Jetspeed Serialized (JS) JetspeedPrincipal
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 */
public class JSPrincipal
{
    private String type;
    private String name;
    private boolean mapped;
    private boolean enabled;
    private boolean readonly;
    private boolean removable;
    private JSSecurityAttributes secAttrs = null;
    
    public JSPrincipal()
    {
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSPrincipal.class)
    {
        public void write(Object o, OutputElement xml) throws XMLStreamException
        {
            try
            {
                JSPrincipal p = (JSPrincipal) o;
                xml.addText(p.getName());
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
                JSPrincipal p = (JSPrincipal) o;
                p.setName(StringEscapeUtils.unescapeHtml(xml.getText().toString()));
                p.mapped = Boolean.getBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("mapped", "false")));
                p.enabled = Boolean.getBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("enabled", "false")));
                p.readonly = Boolean.getBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("readonly", "false")));
                p.removable = Boolean.getBoolean(StringEscapeUtils.unescapeHtml(xml.getAttribute("removable", "false")));
                
                Object o1 = null;
                while (xml.hasNext())
                {
                    o1 = xml.getNext(); // mime
                    
                    if (o1 instanceof JSSecurityAttributes)
                    {
                        p.secAttrs  = (JSSecurityAttributes) o1;
                    }
                }
            } 
            catch (Exception e)
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
     * @param name The type to set.
     */
    public void setType(String type)
    {
        this.type = type;
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

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isMapped()
    {
        return mapped;
    }

    public void setMapped(boolean mapped)
    {
        this.mapped = mapped;
    }

    public boolean isReadonly()
    {
        return readonly;
    }

    public void setReadonly(boolean readonly)
    {
        this.readonly = readonly;
    }

    public boolean isRemovable()
    {
        return removable;
    }

    public void setRemovable(boolean removable)
    {
        this.removable = removable;
    }
    
    public JSSecurityAttributes getSecurityAttributes()
    {
        return this.secAttrs;
    }
    
    public void setSecurityAttributes(JSSecurityAttributes secAttrs)
    {
        this.secAttrs = secAttrs;
    }
    
    public void setSecurityAttributes(Map<String, SecurityAttribute> sa)
    {
        this.secAttrs = new JSSecurityAttributes();
        
        for (Map.Entry<String, SecurityAttribute> e : sa.entrySet())
        {
            SecurityAttribute attrib = e.getValue();
            JSNVPElement element = new JSNVPElement(attrib.getName(), attrib.getStringValue());
            this.secAttrs.add(element);
        }
    }
}
