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

import javax.portlet.PortletMode;

import org.apache.jetspeed.om.portlet.CustomPortletMode;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;

public class CustomPortletModeImpl implements CustomPortletMode
{
    protected String  customName;
    protected String  mappedName;
    protected Boolean portalManaged;
    protected List<Description> descriptions;
    protected transient PortletMode customMode;
    protected transient PortletMode mappedMode;

    public CustomPortletModeImpl()
    {
    }
    
    public void setPortletMode(String customName)
    {
        this.customName = customName.toLowerCase();
    }

    public void setMappedName(String mappedName)
    {
        if (this.mappedName != null || this.mappedMode != null)
        {
            throw new IllegalArgumentException("MappedName already set");
        } else if (mappedName != null)
        {
            this.mappedName = mappedName.toLowerCase();
        }
    }

    public PortletMode getCustomMode()
    {
        if (customMode == null)
        {
            customMode = new PortletMode(customName);
        }
        return customMode;
    }

    public PortletMode getMappedMode()
    {
        if (mappedMode == null)
        {
            if (mappedName != null)
            {
                mappedMode = new PortletMode(mappedName);
            } else
            {
                mappedMode = getCustomMode();
            }
        }
        return mappedMode;
    }

    public int hashCode()
    {
        return customName != null ? customName.hashCode() : super.hashCode();
    }

    public boolean equals(Object object)
    {
        if (object instanceof CustomPortletModeImpl)
            return customName.equals(((CustomPortletModeImpl) object).customName);
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

    public String getPortletMode()
    {
        return customName;
    }

    public boolean isPortalManaged()
    {
        return portalManaged == null ? true : portalManaged.booleanValue();
    }

    public void setPortalManaged(boolean value)
    {
        portalManaged = value ? Boolean.TRUE : Boolean.FALSE;
    }
}
