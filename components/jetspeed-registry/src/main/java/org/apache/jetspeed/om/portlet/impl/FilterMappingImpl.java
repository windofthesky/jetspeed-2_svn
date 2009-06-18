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

import org.apache.jetspeed.om.portlet.FilterMapping;
import org.apache.jetspeed.om.portlet.FilteredPortlet;
import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * @version $Id$
 *
 */
public class FilterMappingImpl implements FilterMapping, Serializable
{
    protected String filterName;
    protected List<FilteredPortlet> portletNames;
    
    public String getFilterName()
    {
        return filterName;
    }

    public void setFilterName(String value)
    {
        filterName = value;
    }

    @SuppressWarnings("unchecked")
    public List<String> getPortletNames()
    {
        if (portletNames == null)
        {
            portletNames = CollectionUtils.createList();
        }
        List<String> result = new ArrayList<String>();
        for (FilteredPortlet fp : portletNames)
        {
            result.add(fp.toString());
        }
        return result;        
    }
    
    @SuppressWarnings("unchecked")
    public void addPortletName(String name)
    {
        if (portletNames == null)
        {
            portletNames = CollectionUtils.createList();
        }
        for (FilteredPortlet fp : this.portletNames)
        {
            if (fp.equals(name))
            {
                throw new IllegalArgumentException("Support for filter-mapping portlet name parameter with identifier: "+name+" already defined");
            }
        }
        portletNames.add(new FilteredPortletImpl(name));        
    }
}
