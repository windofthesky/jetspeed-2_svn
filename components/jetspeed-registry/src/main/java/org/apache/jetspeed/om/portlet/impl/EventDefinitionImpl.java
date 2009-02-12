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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.EventDefinition;
import org.apache.jetspeed.om.portlet.PortletQName;
import org.apache.jetspeed.util.JetspeedLocale;

/**
 * @version $Id$
 *
 */
public class EventDefinitionImpl implements EventDefinition, Serializable
{
    private static final long serialVersionUID = 1L;
    protected String localPart;
    protected String prefix;
    protected String namespace;
    protected String valueType;
    protected List<PortletQName> aliases;
    protected List<Description> descriptions;
    
    public Description getDescription(Locale locale)
    {
        return (Description)JetspeedLocale.getBestLocalizedObject(getDescriptions(), locale);
    }
    
    public List<Description> getDescriptions()
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList<Description>();
        }
        return descriptions;
    }
    
    public Description addDescription(String lang)
    {
        DescriptionImpl d = new DescriptionImpl(this, lang);
        for (Description desc : getDescriptions())
        {
            if (desc.getLocale().equals(d.getLocale()))
            {
                throw new IllegalArgumentException("Description for language: "+d.getLocale()+" already defined");
            }
        }
        descriptions.add(d);
        return d;
    }

    public QName getQName()
    {
        if (namespace == null)
        {
            return new QName(localPart);
        }
        else if (prefix == null)
        {
            return new QName(namespace, localPart);
        }
        else
        {
            return new QName(namespace, localPart, prefix);
        }
    }

    public void setQName(QName qname)
    {
        this.namespace = qname.getNamespaceURI();
        if (this.namespace != null && this.namespace.equals(""))
            this.namespace = null;
        this.prefix = qname.getPrefix();
        if (this.prefix != null && this.prefix.equals(""))
            this.prefix = null;
        this.localPart = qname.getLocalPart();
    }

    public String getName()
    {
        return this.localPart;
    }

    public void setName(String name)
    {
        this.localPart = name;
        this.prefix = null;
        this.namespace = null;
    }

    public List<QName> getAliases()
    {
        List<QName> result = new ArrayList<QName>();
        if (aliases != null)
        {
            for (PortletQName qname : aliases)
            {
                result.add(qname.getQName());
            }
        }
        return result;
    }
    
    public void addAlias(QName alias)
    {       
        if (aliases == null)
        {
            aliases = new ArrayList<PortletQName>();
        }
        if (!containsAlias(alias))
        {
            aliases.add(new PortletQNameImpl(this, alias));
        }
    }
    
    protected boolean containsAlias(QName qname)
    {
        PortletQName alias = new PortletQNameImpl(this, qname);
        for (PortletQName p : aliases)
        {
            if (p.equals(alias))
                return true;
        }
        return false;
    }

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType(String value)
    {
        valueType = value;
    }

    public QName getQualifiedName(String defaultnamespace)
    {
        return new QName(defaultnamespace, localPart);
        //return qname != null ? qname : name != null ? new QName(defaultNamespace, name) : null;
    }
}
