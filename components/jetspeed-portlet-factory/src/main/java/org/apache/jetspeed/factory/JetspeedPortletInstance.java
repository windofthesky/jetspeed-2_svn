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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.UnavailableException;

import org.apache.jetspeed.container.JetspeedPortletConfig;
import org.apache.jetspeed.util.GenericPortletUtils;

/**
 * JetspeedPortletInstance
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedPortletInstance implements PortletInstance
{
    private Portlet portlet;
    private JetspeedPortletConfig config;
    private boolean destroyed;
    private final String portletName;
    
    protected Map<PortletMode, Boolean> helperMethodAccessibilities;

    public JetspeedPortletInstance(String portletName, Portlet portlet)
    {
        this.portletName = portletName;
        this.portlet = portlet;
    }

    private void checkAvailable() throws UnavailableException
    {
        if (destroyed)
        {
            throw new UnavailableException("Portlet " + portletName + " no longer available");
        }
    }

    public void destroy()
    {
        if (!destroyed)
        {
            destroyed = true;
            if (config != null)
            {
                // Portlet really has been put into service, now destroy it.
                portlet.destroy();
            }
        }
    }

    public boolean equals(Object obj)
    {
        return portlet.equals(obj);
    }

    public int hashCode()
    {
        return portlet.hashCode();
    }

    public void init(PortletConfig config) throws PortletException
    {
        portlet.init(config);
        this.config = (JetspeedPortletConfig) config;
    }

    public JetspeedPortletConfig getConfig()
    {
        return config;
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        checkAvailable();
        portlet.processAction(request, response);
    }

    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        checkAvailable();
        portlet.render(request, response);
    }

    public String toString()
    {
        return portlet.toString();
    }

    public void processEvent(EventRequest request, EventResponse response) throws PortletException, IOException
    {
        if (portlet instanceof EventPortlet)
        {
            checkAvailable();
            ((EventPortlet) portlet).processEvent(request, response);
        }
    }

    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException
    {
        if (portlet instanceof ResourceServingPortlet)
        {
            checkAvailable();
            ((ResourceServingPortlet) portlet).serveResource(request, response);
        }
    }

    /**
     * @return Returns the portlet.
     */
    public Portlet getRealPortlet()
    {
        return portlet;
    }

    public boolean isProxyInstance()
    {
        return false;
    }
    
    public boolean hasRenderHelperMethod(PortletMode mode)
    {
        if (helperMethodAccessibilities == null)
        {
            helperMethodAccessibilities = Collections.synchronizedMap(new HashMap<PortletMode, Boolean>());
        }
        
        Boolean accessible = helperMethodAccessibilities.get(mode);
        
        if (accessible != null)
        {
            return accessible.booleanValue();
        }
        else
        {
            Method helperMethod = null;
            Portlet nonProxyPortletObject = getNonProxyPortletObject();
            
            if (nonProxyPortletObject instanceof GenericPortlet)
            {
                helperMethod = GenericPortletUtils.getRenderModeHelperMethod((GenericPortlet) nonProxyPortletObject, mode);
            }
            
            boolean helperMethodAccessible = (helperMethod != null);
            helperMethodAccessibilities.put(mode, helperMethodAccessible ? Boolean.TRUE : Boolean.FALSE);
            
            return helperMethodAccessible;
        }
    }
    
    protected Portlet getNonProxyPortletObject()
    {
        return portlet;
    }
    
}
