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

import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javolution.xml.*;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.security.Credential;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributeType;

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
    private JSSecurityAttributes secAttrs;
    private JSSecurityAttributes infoAttrs;
    private JSPWAttributes pwData;
    private List<Credential> publicCredentials;
    private List<Credential> privateCredentials;
    private transient Principal principal;
    private JSPrincipalRules rules = new JSPrincipalRules();
    
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
                xml.setAttribute("type", p.getType());
                xml.setAttribute("name", p.getName());
                xml.setAttribute("mapped", false);
                xml.setAttribute("enabled", false);
                xml.setAttribute("readonly", false);
                xml.setAttribute("removable", false);
                
                if (p.pwData != null)
                    xml.add(p.pwData);
                
                if (p.secAttrs != null && p.secAttrs.size() > 0)
                    xml.add(p.secAttrs);
                
                if (p.infoAttrs != null && p.infoAttrs.size() > 0)
                    xml.add(p.infoAttrs);
                
                if (p.rules != null && p.rules.size() > 0)
                    xml.add(p.rules);
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
                p.setName(StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "")));
                p.mapped = xml.getAttribute("mapped", false);
                p.enabled = xml.getAttribute("enabled", false);
                p.readonly = xml.getAttribute("readonly", false);
                p.removable = xml.getAttribute("removable", false);
                
                Object o1 = null;
                while (xml.hasNext())
                {
                    o1 = xml.getNext(); // mime
                    
                    if (o1 instanceof JSSecurityAttributes)
                    {
                        JSSecurityAttributes sas = (JSSecurityAttributes) o1;
                        
                        if (SecurityAttributeType.JETSPEED_CATEGORY.equals(sas.getCategory()))
                        {
                            p.secAttrs = sas;
                        }
                        else if (SecurityAttributeType.INFO_CATEGORY.equals(sas.getCategory()))
                        {
                            p.infoAttrs = sas;
                        }
                    }
                    else if (o1 instanceof JSPWAttributes)
                    {
                        p.pwData = (JSPWAttributes) o1;
                    }
                    else if (o1 instanceof JSPrincipalRules)
                    {
                        p.rules = (JSPrincipalRules) o1;
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
        this.secAttrs = new JSSecurityAttributes(SecurityAttributeType.JETSPEED_CATEGORY);
        
        for (Map.Entry<String, SecurityAttribute> e : sa.entrySet())
        {
            SecurityAttribute attrib = e.getValue();
            JSNVPElement element = new JSNVPElement(attrib.getName(), attrib.getStringValue());
            this.secAttrs.add(element);
        }
    }

    public JSSecurityAttributes getInfoAttributes()
    {
        return this.infoAttrs;
    }
    
    public void setInfoAttributes(JSSecurityAttributes infoAttrs)
    {
        this.infoAttrs = infoAttrs;
    }
    
    public void setInfoAttributes(Map<String, SecurityAttribute> sa)
    {
        this.infoAttrs = new JSSecurityAttributes(SecurityAttributeType.INFO_CATEGORY); 
        
        for (Map.Entry<String, SecurityAttribute> e : sa.entrySet())
        {
            SecurityAttribute attrib = e.getValue();
            JSNVPElement element = new JSNVPElement(attrib.getName(), attrib.getStringValue());
            this.infoAttrs.add(element);
        }
    }
    
    public void setCredential(String name, char [] password, Date expirationDate, boolean isEnabled, boolean isExpired, boolean requireUpdate)
    {
        setName(name);
        this.pwData = new JSPWAttributes();
        
        if (password != null)
        {
            String passwordString = (password.length > 0 ? new String(password) : "");
            this.pwData.getMyMap().put("password", passwordString);
            
            if (expirationDate != null)
            {
                this.pwData.getMyMap().put("expirationDate",expirationDate.toString());
            }
            
            this.pwData.getMyMap().put("enabled", Boolean.toString(isEnabled));
            this.pwData.getMyMap().put("requiresUpdate", Boolean.toString(requireUpdate));
        }
    }
    
    public String getPwDataValue(String key)
    {
        return getPwDataValue(key, null);
    }
    
    public String getPwDataValue(String key, String defValue)
    {
        String value = (this.pwData != null ? this.pwData.getMyMap().get(key) : null);
        return (value != null ? value : defValue);
    }
    
    public boolean getPwDataValueAsBoolean(String key)
    {
        return getPwDataValueAsBoolean(key, false);
    }
    
    public boolean getPwDataValueAsBoolean(String key, boolean defValue)
    {
        String sv = getPwDataValue(key);        
        return (sv != null ? Boolean.parseBoolean(sv) : defValue);
    }
    
    public Date getPwDataValueAsDate(String key)
    {
        return getPwDataValueAsDate(key, null);
    }
    
    public Date getPwDataValueAsDate(String key, Date defValue)
    {
        Date value = null;
        String sv = getPwDataValue(key, null);
        
        if (sv != null)
        {
            value = Date.valueOf(sv);
        }
        
        return (value != null ? value : defValue);
    }
    
    public Principal getPrincipal()
    {
        return principal;
    }

    public void setPrincipal(Principal principal)
    {
        this.principal = principal;
    }
    
    public JSPrincipalRules getRules()
    {
        return rules;
    }

    public void setRules(JSPrincipalRules rules)
    {
        this.rules = rules;
    }
}