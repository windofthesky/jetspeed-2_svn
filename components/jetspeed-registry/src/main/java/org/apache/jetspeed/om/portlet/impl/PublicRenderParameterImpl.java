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
import org.apache.jetspeed.om.portlet.PublicRenderParameter;
import org.apache.jetspeed.util.JetspeedLocale;

/**
 * @version $Id$
 *
 */
public class PublicRenderParameterImpl implements PublicRenderParameter, Serializable
{
    protected String identifier;
    protected QName qname;
    protected String name;
    protected List<QName> alias;
    protected List<Description> descriptions;
    
    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String value)
    {
        identifier = value;
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
        if (alias == null)
        {
            alias = new ArrayList<QName>();
        }
        return alias;
    }

    public void addAlias(QName alias)
    {
        // TODO: check duplicates
        getAliases().add(alias);
    }
    
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
}
