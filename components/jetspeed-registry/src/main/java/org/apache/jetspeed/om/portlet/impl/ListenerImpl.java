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
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * @version $Id$
 *
 */
public class ListenerImpl implements Listener, Serializable
{
    private static final long serialVersionUID = 1L;
    
    protected String listenerClass;
    protected List<Description> descriptions;
    protected List<DisplayName> displayNames;
    
    public String getListenerClass()
    {
        return listenerClass;
    }

    public void setListenerClass(String listenerClass)
    {
        this.listenerClass = listenerClass;
    }

    public Description getDescription(Locale locale)
    {
        return (Description)JetspeedLocale.getBestLocalizedObject(getDescriptions(), locale);
    }
    
    @SuppressWarnings("unchecked")
    public List<Description> getDescriptions()
    {
        if (descriptions == null)
        {
            descriptions = CollectionUtils.createList();
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

    public DisplayName getDisplayName(Locale locale)
    {
        return (DisplayName)JetspeedLocale.getBestLocalizedObject(getDisplayNames(), locale);
    }
    
    @SuppressWarnings("unchecked")
    public List<DisplayName> getDisplayNames()
    {
        if (displayNames == null)
        {
            displayNames = CollectionUtils.createList();
        }
        return displayNames;
    }
    
    public DisplayName addDisplayName(String lang)
    {
        DisplayNameImpl d = new DisplayNameImpl(this, lang);
        for (DisplayName dn : getDisplayNames())
        {
            if (dn.getLocale().equals(d.getLocale()))
            {
                throw new IllegalArgumentException("DisplayName for language: "+d.getLocale()+" already defined");
            }
        }
        displayNames.add(d);
        return d;
    }
}
