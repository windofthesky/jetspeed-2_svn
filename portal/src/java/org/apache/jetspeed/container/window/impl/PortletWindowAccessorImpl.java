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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotGeneratedException;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.window.FailedToCreateWindowException;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowCtrl;

/**
 * Portlet Window Accessor Implementation
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletWindowAccessorImpl.java,v 1.12 2005/04/29 14:01:57 weaver Exp $
 */
public class PortletWindowAccessorImpl implements PortletWindowAccessor 
{
    protected final static Log log = LogFactory.getLog(PortletWindowAccessorImpl.class);
   
    private Map windows = new HashMap();
    private PortletEntityAccessComponent entityAccessor;
    private boolean validateWindows = false;
    

    public PortletWindowAccessorImpl(PortletEntityAccessComponent entityAccessor, boolean validateWindows)
    {
        this.entityAccessor = entityAccessor;
        this.validateWindows = validateWindows;
    }

    public PortletWindow createPortletWindow(PortletEntity entity, String windowId)
    {
        if(entity == null)
        {
            throw new IllegalArgumentException("cratePortletWindow requires a non-null PortletEntity");
        }
        
        PortletWindow found = getWindowFromCache(windowId);
        if (found != null)
        {
            ((PortletWindowCtrl)found).setPortletEntity(entity);
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
    
    public PortletWindow getPortletWindow(ContentFragment fragment) throws FailedToRetrievePortletWindow, PortletEntityNotStoredException
    {
        ArgUtil.assertNotNull(ContentFragment.class, fragment, this, "getPortletWindow(Fragment fragment)");
        PortletWindow portletWindow = getWindowFromCache(fragment);
        if (portletWindow == null)
        {
            try
            {
                return createPortletWindow(fragment);
            }
            catch (FailedToCreateWindowException e)
            {
                throw new FailedToRetrievePortletWindow(e.toString(), e);
            }
        }
        else
        {
            if (validateWindows)
            {
                validateWindow(fragment, portletWindow);
            }
        }
        
        return portletWindow;
    }
    
    /**
     * <p>
     * validateWindow
     * </p>
     *
     * @param fragment
     * @param portletWindow
     * @throws PortletEntityNotStoredException 
     * @throws InconsistentWindowStateException
     */
    protected void validateWindow( ContentFragment fragment, PortletWindow portletWindow ) throws FailedToRetrievePortletWindow, PortletEntityNotStoredException
    {
        // make sure the window has the most up-to-date portlet entity
        PortletEntity portletEntity = entityAccessor.getPortletEntityForFragment(fragment);
        if(portletEntity != null)
        {
            ((PortletWindowCtrl) portletWindow).setPortletEntity(portletEntity);
        }
        else
        {
            removeWindow(portletWindow);  
            throw new FailedToRetrievePortletWindow("No PortletEntity exists for for id "+fragment.getId()+" removing window from cache.");
        }
    }

    public PortletWindow getPortletWindow(ContentFragment fragment, String principal) throws FailedToRetrievePortletWindow, FailedToCreateWindowException, PortletEntityNotStoredException
    {
        ArgUtil.assertNotNull(ContentFragment.class, fragment, this, "getPortletWindow(Fragment fragment, String principal)");
        ArgUtil.assertNotNull(String.class, principal, this, "getPortletWindow(Fragment fragment, String principal)");
        PortletWindow portletWindow = getWindowFromCache(fragment);
        if (portletWindow == null)
        {
            return createPortletWindow(fragment, principal);
        }        
        else
        {
            // make sure the window has the most up-to-date portlet entity
            validateWindow(fragment, portletWindow);
        }
        return portletWindow;
    }

    private PortletWindow createPortletWindow(ContentFragment fragment) throws FailedToCreateWindowException, PortletEntityNotStoredException
    {
        return createPortletWindow(fragment, null);
    }
    
    private PortletWindow createPortletWindow(ContentFragment fragment, String principal) throws FailedToCreateWindowException, PortletEntityNotStoredException
    {
        PortletWindow portletWindow = new PortletWindowImpl(fragment.getId());
                
        MutablePortletEntity portletEntity = entityAccessor.getPortletEntityForFragment(fragment, principal);
        if (portletEntity == null)
        {
            log.info("No portlet entity defined for fragment ID "+fragment.getId()+" attempting to auto-generate...");
            try
            {
                portletEntity = entityAccessor.generateEntityFromFragment(fragment, principal);
                // not portlet definition most likely means that the portlet has not been deployed so dont worry about storing off the entity
                if(portletEntity.getPortletDefinition() != null)
                {
                    entityAccessor.storePortletEntity(portletEntity);
                }
            }
            catch (PortletEntityNotGeneratedException e)
            {
                throw new FailedToCreateWindowException("Error generating new PortletEntity: "+e.toString(), e);                
            }
            catch (PortletEntityNotStoredException e)
            {
                throw new FailedToCreateWindowException("Error storing new PortletEntity: "+e.toString(), e);
            }
            
            if(portletEntity == null)
            {
                throw new FailedToCreateWindowException("Unable to generate portlet entity.");
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
    
    private PortletWindow getWindowFromCache(ContentFragment fragment)
    {
        return (PortletWindow)windows.get(fragment.getId());
    }
    
    private PortletWindow getWindowFromCache(String id)
    {
        return (PortletWindow)windows.get(id);
    }
    
}
