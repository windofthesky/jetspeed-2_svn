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
package org.apache.jetspeed.container;

import javax.portlet.Portlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.pluto.om.portlet.PortletDefinition;

public class PortletRequestContext
{
    private static ThreadLocal context = new ThreadLocal();
    
    private PortletDefinition pd;
    private Portlet portlet;
    private PortletRequest request;
    private PortletResponse response;
    
    public static PortletRequestContext getContext()
    {
        return (PortletRequestContext)context.get();
    }
    
    public static void createContext(PortletDefinition pd, Portlet portlet, PortletRequest request, PortletResponse response)
    {
        context.set(new PortletRequestContext(pd, portlet, request, response));
    }
    
    public static void clearContext()
    {        
        context.set(null);
    }

    private PortletRequestContext(PortletDefinition pd, Portlet portlet, PortletRequest request, PortletResponse response)
    {
        this.pd = pd;
        this.portlet = portlet;
        this.request = request;
        this.response = response;
    }

    public PortletDefinition getPortletDefinition()
    {
        return pd;
    }

    public Portlet getPortlet()
    {
        return portlet;
    }

    public PortletRequest getRequest()
    {
        return request;
    }

    public PortletResponse getResponse()
    {
        return response;
    }
}