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

import groovy.swing.impl.Startable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotGeneratedException;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.pluto.om.entity.PortletEntity;
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
public class PortletWindowAccessorImpl implements PortletWindowAccessor 
{
    protected final static Log log = LogFactory.getLog(PortletWindowAccessorImpl.class);
   
    private Map windows = new HashMap();
    private PortletEntityAccessComponent entityAccessor;
    

    public PortletWindowAccessorImpl(PortletEntityAccessComponent entityAccessor )
    {
        this.entityAccessor = entityAccessor;

    }

    public PortletWindow createPortletWindow(PortletEntity entity, String windowId)
    {
        PortletWindow found = getWindowFromCache(windowId);
        if (found != null)
        {
            return found;
        }
        
        PortletWindowImpl window = new PortletWindowImpl(windowId);
        window.setPortletEntity(entity);
        windows.put(windowId, window);
        return window;        
    }

    public PortletWindow createPortletWindow(String windowId)
    {
        PortletWindow found = getWindowFromCache(windowId);
        if (found != null)
        {
            return found;
        }        
        PortletWindowImpl window = new PortletWindowImpl(windowId);
        return window;                
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
                
        MutablePortletEntity portletEntity = entityAccessor.getPortletEntityForFragment(fragment, principal);
        if (portletEntity == null)
        {
            log.info("No portlet entity defined for fragment ID "+fragment.getId()+" attempting to auto-generate...");
            try
            {
                portletEntity = entityAccessor.generateEntityFromFragment(fragment, principal);
                entityAccessor.storePortletEntity(portletEntity);
            }
            catch (PortletEntityNotGeneratedException e)
            {
                log.error("Error generating new PortletEntity: "+e.toString(), e);                
            }
            catch (PortletEntityNotStoredException e)
            {
                log.error("Error storing new PortletEntity: "+e.toString(), e);
            }
            
            if(portletEntity == null)
            {
                throw new IllegalStateException("Unable to generate portlet entity.");
            }
            
        }
        ((PortletWindowCtrl) portletWindow).setPortletEntity(portletEntity);
        
        windows.put(fragment.getId(), portletWindow);       
        
        
        return portletWindow;
    }
    
    public void removeWindows(PortletEntity portletEntity)
    {
        Iterator entityWindows = portletEntity.getPortletWindowList().iterator();
        while(entityWindows.hasNext())
        {
            Object obj = entityWindows.next();
            PortletWindow window = (PortletWindow) obj;
            removeWindow(window);
        }
    }
    
    public void removeWindow(PortletWindow window)
    {
        windows.remove(window.getId().toString());
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
