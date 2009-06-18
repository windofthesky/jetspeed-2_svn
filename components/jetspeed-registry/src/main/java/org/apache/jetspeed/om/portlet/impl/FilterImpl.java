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

import javax.portlet.filter.PortletFilter;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.FilterLifecycle;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * @version $Id$
 *
 */
public class FilterImpl implements Filter, Serializable
{
    protected String filterName;
    protected String filterClass;
    protected List<FilterLifecycle> lifecycles;
    protected List<InitParam> initParams;
    protected List<Description> descriptions;
    protected List<DisplayName> displayNames;
    protected PortletFilter filterInstance;
    
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

    public String getFilterName()
    {
        return filterName;
    }

    public void setFilterName(String value)
    {
        filterName = value;
    }

    public String getFilterClass()
    {
        return filterClass;
    }

    public void setFilterClass(String value)
    {
        filterClass = value;
    }

    @SuppressWarnings("unchecked")
    public List<String> getLifecycles()
    {
        if (lifecycles == null)
        {
            lifecycles = CollectionUtils.createList();
        }
        List<String> result = new ArrayList<String>();
        for (FilterLifecycle flc : lifecycles)
        {
            result.add(flc.toString());
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public void addLifecycle(String name)
    {
        if (lifecycles == null)
        {
            lifecycles = CollectionUtils.createList();
        }
        for (FilterLifecycle flc : this.lifecycles)
        {
            if (flc.equals(name))
            {
                throw new IllegalArgumentException("Support for filter lifecycle parameter with identifier: "+name+" already defined");
            }
        }
        lifecycles.add(new FilterLifecycleImpl(name));        
    }
    
    public InitParam getInitParam(String name)
    {
        for (InitParam param : getInitParams())
        {
            if (param.getParamName().equals(name))
            {
                return param;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<InitParam> getInitParams()
    {
        if (initParams == null)
        {
            initParams = CollectionUtils.createList();
        }
        return initParams;
    }
    
    public InitParam addInitParam(String paramName)
    {
        if (getInitParam(paramName) != null)
        {
            throw new IllegalArgumentException("Init parameter: "+paramName+" already defined");
        }
        InitParamImpl param = new InitParamImpl(this, paramName);
        getInitParams();
        initParams.add(param);
        return param;
    }
}
