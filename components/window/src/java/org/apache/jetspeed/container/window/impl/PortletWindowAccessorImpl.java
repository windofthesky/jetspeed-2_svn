/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.container.window.impl;

import java.util.HashMap;
import java.util.Map;

import groovy.swing.impl.Startable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowCtrl;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.om.window.PortletWindowListCtrl;

/**
 * Portlet Window Accessor Implementation
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletWindowAccessorImpl implements PortletWindowAccessor, Startable 
{
    protected final static Log log = LogFactory.getLog(PortletWindowAccessorImpl.class);
   
    private Map windows = new HashMap();
    private PortletEntityAccessComponent entityAccessor;
    private PortletRegistryComponent registry;
    
 
    public PortletWindowAccessorImpl(PortletEntityAccessComponent entityAccessor,
                                     PortletRegistryComponent registry)
    {
        this.entityAccessor = entityAccessor;
        this.registry = registry;
    }
    
    public void start() 
    {
    }

    public PortletWindow getPortletWindow(String windowId)
    {
        return getWindowFromCache(windowId);
    }
    
    public PortletWindow getPortletWindow(Fragment fragment)
    {
        PortletWindow portletWindow = getWindowFromCache(fragment);
        if (portletWindow == null)
        {
            return createPortletWindow(fragment);
        }
        return portletWindow;
    }
    
    public PortletWindow getPortletWindow(Fragment fragment, String principal)
    {
        PortletWindow portletWindow = getWindowFromCache(fragment);
        if (portletWindow == null)
        {
            return createPortletWindow(fragment, principal);
        }        
        return portletWindow;
    }

    private PortletWindow createPortletWindow(Fragment fragment)
    {
        return createPortletWindow(fragment, null);
    }
    
    private PortletWindow createPortletWindow(Fragment fragment, String principal)
    {
        PortletWindow portletWindow = new PortletWindowImpl(fragment.getId());
        
        PortletEntity portletEntity = entityAccessor.getPortletEntity(makeEntityKey(fragment, principal));
        if (portletEntity == null)
        {
            PortletDefinition pd = registry.getPortletDefinitionByUniqueName(fragment.getName());
            if (pd == null)
            {
                log.error("Failed to retrieve Portlet Definition for " + fragment.getName());
                return null;
            }
            portletEntity = entityAccessor.newPortletEntityInstance(pd);
            if (portletEntity == null)
            {
                log.error("Failed to create Portlet Entity for " + fragment.getName());
                return null;
            }            
        }
        ((PortletWindowCtrl) portletWindow).setPortletEntity(portletEntity);
        PortletWindowList windowList = portletEntity.getPortletWindowList();
        ((PortletWindowListCtrl) windowList).add(portletWindow);
        
        windows.put(fragment.getId(), portletWindow);
        
        try
        {
            entityAccessor.storePortletEntity(portletEntity);
        }
        catch (Exception e)
        {
            log.error("Error persisting new portletEntity", e);
        }
        
        return portletWindow;
    }
    
    private ObjectID makeEntityKey(Fragment fragment, String principal)
    {
        StringBuffer key = new StringBuffer();
        if (principal != null && principal.length() > 0)
        {
            key.append(principal);
            key.append("/");
        }
        key.append(fragment.getId());
        return JetspeedObjectID.createFromString(key.toString());
    }
    
    private PortletWindow getWindowFromCache(Fragment fragment)
    {
        return (PortletWindow)windows.get(fragment.getId());
    }
    
    private PortletWindow getWindowFromCache(String id)
    {
        return (PortletWindow)windows.get(id);
    }
    
}
