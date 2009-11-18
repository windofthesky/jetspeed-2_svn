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
package org.apache.jetspeed.components.portletentity;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;

/**
 * Portlet Entity default implementation.
 * TODO: 2.2 - don't associate Fragment with Entity, should be with window
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: PortletEntityImpl.java,v 1.9 2005/04/29 13:59:08 weaver Exp $
 * @obsolete
 */
public class PortletEntityImpl implements PortletEntity
{   
    private static PortletRegistry registry;

    private Long oid;
    private String id;    
    private PortletWindow portletWindow = null;
    private PortletDefinition portletDefinition = null;  
    protected String portletName;
    protected String appName;
    private ContentFragment fragment;
    
    public PortletEntityImpl(ContentFragment fragment)
    {
        setFragment(fragment);
    }

    public PortletEntityImpl()
    {
        super();
    }
    
    public String getId()
    {
        return this.id;
    }
    
    public Long getOid()
    {
        return oid;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public void setPortletWindow(PortletWindow window)
    {
        this.portletWindow = window;
    }    

    public PortletWindow getPortletWindow()
    {
        return this.portletWindow;
    }
    
    public PortletDefinition getPortletDefinition()
    {
        // there are cases when jetspeed gets initialized before
        // all of the portlet web apps have.  In this event, premature
        // access to the portal would cause portlet entities to be cached
        // with their associated window without there corresponding PortletDefinition
        // (becuase the PortletApplication has yet to be registered).
        if (this.portletDefinition == null)
        {
            PortletDefinition pd = registry
                    .getPortletDefinitionByUniqueName(getPortletUniqueName());
            if (pd != null)
            {
                // only store a really found PortletDefinition
                // to prevent an IllegalArgumentException to be thrown
                setPortletDefinition(pd);
            }
            else
            {
                return null;
            }
        }            
        return this.portletDefinition;
    }

    /**
     * <p>
     * setPortletDefinition
     * </p>
     * 
     * @param composite
     *  
     */
    public void setPortletDefinition(PortletDefinition pd)
    {
        if (pd != null)
        {
            portletDefinition = pd;
            this.appName = portletDefinition.getApplication().getName();
            this.portletName = portletDefinition.getPortletName();
        }
        else
        {
            throw new IllegalArgumentException("Cannot pass a null PortletDefinition to a PortletEntity.");
        }
    }
    
    public String getPortletUniqueName()
    {
        if(this.appName != null && this.portletName != null)
        {
            return this.appName+"::"+this.portletName;
        }
        else if(fragment != null)
        {
            return fragment.getName();
        }
        else
        {
            return null;
        }
    }

    public ContentFragment getFragment()
    {
        return this.fragment;
    }
    
    public void setFragment(ContentFragment fragment)
    {
        this.fragment = fragment;
    }

    protected String getEntityFragmentKey()
    {
        String entityId = (this.getId() == null) ? "-unknown-entity" : this.getId().toString();
        return "org.apache.jetspeed" + entityId ;
    }
    
    public static void setPortletRegistry(PortletRegistry registry)
    {
        PortletEntityImpl.registry = registry;
    }
 
}