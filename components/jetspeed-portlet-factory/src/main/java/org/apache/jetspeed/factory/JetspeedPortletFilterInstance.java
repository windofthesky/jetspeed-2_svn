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
package org.apache.jetspeed.factory;

import javax.portlet.PortletException;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.PortletFilter;

import org.apache.jetspeed.om.portlet.Filter;

/**
 * JetspeedPortletFilterInstance
 * 
 * @version $Id$
 */
public class JetspeedPortletFilterInstance implements PortletFilterInstance
{
    private Filter filter;
    private PortletFilter portletFilter;
    private FilterConfig filterConfig;
    
    private boolean initialized;
    private boolean destroyed;
    
    public JetspeedPortletFilterInstance(Filter filter, PortletFilter portletFilter)
    {
        this.filter = filter;
        this.portletFilter = portletFilter;
    }
    
    public boolean equals(Object obj)
    {
        return (this.portletFilter != null ? this.portletFilter.equals(obj) : false);
    }
    
    public int hashCode()
    {
        return (this.portletFilter != null ? this.portletFilter.hashCode() : 0);
    }
    
    public String toString()
    {
        return (this.portletFilter != null ? this.portletFilter.toString() : super.toString());
    }
    
    public void init(FilterConfig filterConfig) throws PortletException
    {
        this.filterConfig = filterConfig;
        this.portletFilter.init(filterConfig);
        this.initialized = true;
        this.destroyed = false;
    }

    public void destroy()
    {
        if (!this.destroyed)
        {
            this.destroyed = true;
            this.initialized = false;
            
            if (this.portletFilter != null && this.filterConfig != null)
            {
                this.portletFilter.destroy();
            }
        }
    }
    
    public boolean isInitialized()
    {
        return this.initialized;
    }
    
    public Filter getFilter()
    {
        return this.filter;
    }
    
    public FilterConfig getFilterConfig()
    {
        return this.filterConfig;
    }

    public PortletFilter getRealPortletFilter()
    {
        return this.portletFilter;
    }
    
}
