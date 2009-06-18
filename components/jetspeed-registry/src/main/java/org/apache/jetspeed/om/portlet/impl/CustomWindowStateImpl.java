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
package org.apache.jetspeed.om.portlet.impl;

import java.util.List;
import java.util.Locale;

import javax.portlet.WindowState;

import org.apache.jetspeed.om.portlet.CustomWindowState;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;

public class CustomWindowStateImpl implements CustomWindowState
{
    protected String customName;
    protected String mappedName;
    protected List<Description> descriptions;
    protected transient WindowState customState;
    protected transient WindowState mappedState;

    public CustomWindowStateImpl()
    {
    }

    public void setWindowState(String customName)
    {
        this.customName = customName.toLowerCase();
    }

    public void setMappedName(String mappedName)
    {
        if ( this.mappedName != null || this.mappedState != null )
        {
            throw new IllegalArgumentException("MappedName already set");
        }
        else if ( mappedName != null )
        {
            this.mappedName = mappedName.toLowerCase();
        }
    }

    public WindowState getCustomState()
    {
        if (customState == null)
        {
            customState = new WindowState(customName);
        }
        return customState;
    }

    public WindowState getMappedState()
    {
        if (mappedState == null)
        {
            if (mappedName != null)
            {
                mappedState = new WindowState(mappedName);
            } else
            {
                mappedState = getCustomState();
            }
        }
        return mappedState;
    }

    public int hashCode()
    {
        return customName != null ? customName.hashCode() : super.hashCode();
    }

    public boolean equals(Object object)
    {
        if (object instanceof CustomWindowStateImpl)
            return customName.equals(((CustomWindowStateImpl) object).customName);
        else
            return false;
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

    public String getWindowState()
    {
        return customName;
    }
}
