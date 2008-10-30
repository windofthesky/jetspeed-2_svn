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

/**
 * @version $Id$
 *
 */
public class EventDefinitionImpl implements EventDefinition, Serializable
{
    protected String name;
    protected QName qname;
    protected String valueType;
    protected List<QName> aliases;
    protected List<Description> descriptions;
    
    public Description getDescription(Locale locale)
    {
        for (Description d : getDescriptions())
        {
            if (d.getLocale().equals(locale))
            {
                return d;
            }
        }
        return null;
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
        if (getDescription(d.getLocale()) != null)
        {
            throw new IllegalArgumentException("Description for language: "+d.getLocale()+" already defined");
        }
        getDescriptions();
        descriptions.add(d);
        return d;
    }

    public QName getQName()
    {
        return qname;
    }

    public void setQName(QName value)
    {
        qname = value;
        name = null;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String value)
    {
        name = value;
        qname = null;
    }

    public List<QName> getAliases()
    {
        if (aliases == null)
        {
            aliases = new ArrayList<QName>();
        }
        return aliases;
    }
    
    public void addAlias(QName alias)
    {
        // TODO: check for duplicates
        getAliases().add(alias);
    }

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType(String value)
    {
        valueType = value;
    }

    public QName getQualifiedName(String defaultNamespace)
    {
        return qname != null ? qname : name != null ? new QName(defaultNamespace, name) : null;
    }
}
