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
package org.apache.jetspeed.container.services;

import java.util.Enumeration;
import java.util.Vector;

import javax.portlet.PortletContext;
import javax.portlet.filter.FilterConfig;

import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.InitParam;

public class JetspeedFilterConfigImpl implements FilterConfig
{
    private final Filter filter;
    private final PortletContext portletContext;
    private Vector<String> initParamNames;

    public JetspeedFilterConfigImpl(Filter filter, PortletContext portletContext){
        this.filter = filter;
        this.portletContext = portletContext;
    }

    /**
     * @see javax.portlet.filter.FilterConfig#getFilterName()
     */
    public String getFilterName() {
        return this.filter.getFilterName();
    }

    /**
     * @see javax.portlet.filter.FilterConfig#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String name) {
        InitParam initParam = this.filter.getInitParam(name);
        return (initParam != null ? initParam.getParamValue() : null);
    }

    /**
     * @see javax.portlet.filter.FilterConfig#getInitParameterNames()
     */
    public Enumeration<String> getInitParameterNames() {
        if (this.initParamNames == null)
        {
            this.initParamNames = new Vector<String>();
            
            for (InitParam initParam : this.filter.getInitParams())
            {
                this.initParamNames.add(initParam.getParamName());
            }
        }
        
        return this.initParamNames.elements();
    }

    /**
     * @see javax.portlet.filter.FilterConfig#getPortletContext()
     */
    public PortletContext getPortletContext() {
        return this.portletContext;
    }
}
