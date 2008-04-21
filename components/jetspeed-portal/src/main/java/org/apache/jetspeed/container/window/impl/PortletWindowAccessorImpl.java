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
package org.apache.jetspeed.container.window.impl;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.PortletWindowCache;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotGeneratedException;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryEventListener;
import org.apache.jetspeed.container.window.FailedToCreateWindowException;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
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
public class PortletWindowAccessorImpl implements PortletWindowAccessor, RegistryEventListener
{
    protected final static Log log = LogFactory.getLog(PortletWindowAccessorImpl.class);
   
    private PortletEntityAccessComponent entityAccessor;
    private PortletFactory portletFactory;
    private boolean validateWindows = false;
    private PortletWindowCache portletWindowCache;

    public PortletWindowAccessorImpl(PortletEntityAccessComponent entityAccessor, PortletFactory portletFactory, PortletWindowCache portletWindowCache, boolean validateWindows)
    {
        this.entityAccessor = entityAccessor;
        this.portletFactory = portletFactory;
        this.validateWindows = validateWindows;
        this.portletWindowCache = portletWindowCache;

    }

    public PortletWindowAccessorImpl(PortletEntityAccessComponent entityAccessor, 
                                     PortletFactory portletFactory, 
                                     PortletRegistry registry, 
                                     PortletWindowCache portletWindowCache,
                                     boolean validateWindows)
    {
        this.entityAccessor = entityAccessor;
        this.portletFactory = portletFactory;
        this.validateWindows = validateWindows;
        this.portletWindowCache = portletWindowCache;
        registry.addRegistryListener(this);
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
            // remove from cache if invalid entity
            checkPortletWindowEntity(found);
            ((PortletWindowCtrl)found).setPortletEntity(entity);
            return found;
        }
        
        PortletWindowImpl window = new PortletWindowImpl(windowId);
        window.setPortletEntity(entity);
        if ( isValidPortletEntity(entity))
        {
        	portletWindowCache.putPortletWindow(window);
        }
        return window;        
    }

    public PortletWindow createPortletWindow(String windowId)
    {
        PortletWindow found = getWindowFromCache(windowId);
        if (found != null)
        {
            // remove from cache if invalid entity
            checkPortletWindowEntity(found);
            return found;
        }        
        PortletWindowImpl window = new PortletWindowImpl(windowId);
        return window;                
    }
    
    public PortletWindow getPortletWindow(String windowId)
    {
        PortletWindow window = getWindowFromCache(windowId);
        if (window != null)
        {
            // remove from cache if invalid entity
            checkPortletWindowEntity(window);
        }        
        return window;
    }
    
    public PortletWindow getPortletWindow(ContentFragment fragment) throws FailedToRetrievePortletWindow, PortletEntityNotStoredException
    {
        ArgUtil.assertNotNull(ContentFragment.class, fragment, this, "getPortletWindow(Fragment fragment)");
        PortletWindow portletWindow = getWindowFromCache(fragment);        
        if (portletWindow == null || !checkPortletWindowEntity(portletWindow))
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
            // if not a valid entity, remove window from cache
            checkPortletWindowEntity(portletWindow);
        }
        else
        {
            removeWindow(portletWindow);  
            throw new FailedToRetrievePortletWindow("No PortletEntity exists for for id "+fragment.getId()+" removing window from cache.");
        }
    }

    private PortletWindow createPortletWindow(ContentFragment fragment) throws FailedToCreateWindowException, PortletEntityNotStoredException
    {        
        PortletWindow portletWindow = new PortletWindowImpl(fragment.getId());
        boolean temporaryWindow = false;
                
        MutablePortletEntity portletEntity = entityAccessor.getPortletEntityForFragment(fragment);
        if (portletEntity == null)
        {
            log.info("No portlet entity defined for fragment ID "+fragment.getId()+" attempting to auto-generate...");
            try
            {
                portletEntity = entityAccessor.generateEntityFromFragment(fragment);
                // not portlet definition most likely means that the portlet has not been deployed so dont worry about storing off the entity
                if(isValidPortletEntity(portletEntity))
                {
                    entityAccessor.storePortletEntity(portletEntity);
                }
                else
                {
                    // don't cache the incomplete window
                    temporaryWindow = true;
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
        
        if ( !temporaryWindow )
        {
            portletWindowCache.putPortletWindow(portletWindow);
        }
        return portletWindow;
    }
    

    public void removeWindows(PortletEntity portletEntity)
    {
        portletWindowCache.removePortletWindowByPortletEntityId(portletEntity.getId().toString());
    }
    
    public void removeWindow(PortletWindow window)
    {
    	portletWindowCache.removePortletWindow(window.getId().toString());
    }
    
    private PortletWindow getWindowFromCache(ContentFragment fragment)
    {
        return portletWindowCache.getPortletWindow(fragment.getId());
    }
    
    private PortletWindow getWindowFromCache(String id)
    {
        return portletWindowCache.getPortletWindow(id);
    }

    private boolean checkPortletWindowEntity(PortletWindow window)
    {
        if (!isValidPortletEntity(window.getPortletEntity()))
        {
            removeWindow(window);
            return false;
        }
        return true;
    }
    
    private boolean isValidPortletEntity(PortletEntity pe)
    {
        return pe != null
                && pe.getPortletDefinition() != null
                && pe.getPortletDefinition().getPortletApplicationDefinition() != null
                && portletFactory.isPortletApplicationRegistered((PortletApplication) pe.getPortletDefinition()
                        .getPortletApplicationDefinition());
    }
    
    public Set getPortletWindows()
    {
        return portletWindowCache.getAllPortletWindows();
    }

    protected void removeForPortletDefinition(PortletDefinitionComposite def)
    {
        Iterator windows  = getPortletWindows().iterator();
        while(windows.hasNext())
        {
        	PortletWindow window = (PortletWindow) windows.next();
        	PortletDefinitionComposite windowDef = (PortletDefinitionComposite)window.getPortletEntity().getPortletDefinition();            
            if(def != null && windowDef != null && def.getUniqueName() != null && def.getUniqueName().equals(windowDef.getUniqueName()))
            {
                removeWindow(window);
            }
        }
        if (def != null)
            portletFactory.updatePortletConfig(def);
    }

    protected void removeForPortletApplication(MutablePortletApplication app)
    {
        Iterator windows  = getPortletWindows().iterator();
        while(windows.hasNext())
        {
        	PortletWindow window = (PortletWindow) windows.next();
        	PortletDefinitionComposite pd =  (PortletDefinitionComposite)window.getPortletEntity().getPortletDefinition();            
        	 if (pd != null)
             {
                 MutablePortletApplication windowApp = (MutablePortletApplication)pd.getPortletApplicationDefinition();            
                 if (app.getName().equals(windowApp.getName()))
                 {
                     removeWindow(window);
                 }
             }
        }
    }
    
    public void applicationRemoved(MutablePortletApplication app)
    {
        if (app != null)
        {
            removeForPortletApplication(app);
        }
    }

 
    public void applicationUpdated(MutablePortletApplication app)
    {
        if (app != null)
        {
            removeForPortletApplication(app);
        }
    }

    public void portletRemoved(PortletDefinitionComposite def)
    {
        if (def != null)
        {
            removeForPortletDefinition(def);
        }
    }
 
    public void portletUpdated(PortletDefinitionComposite def)
    {
        if (def != null)
        {
            removeForPortletDefinition(def);
        }
    }
}
