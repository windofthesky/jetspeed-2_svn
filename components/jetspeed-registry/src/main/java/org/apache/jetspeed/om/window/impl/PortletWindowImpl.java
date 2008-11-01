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
package org.apache.jetspeed.om.window.impl;

import java.io.Serializable;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletWindowID;

/**
 * <P>
 * The <CODE>PortletWindow</CODE> implementation represents a single window
 * of an portlet instance as it can be shown only once on a single page. 
 * Adding the same portlet e.g. twice on a page results in two different windows.
 * </P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 **/
public class PortletWindowImpl implements PortletWindow, PortletWindowID, Serializable
{
    private static final long serialVersionUID = 6578938580906866201L;
    
    private transient PortletEntity portletEntity = null;
    private String id;
    private PortletMode portletMode;
    private WindowState windowState;
    
    private boolean instantlyRendered;

    public PortletWindowImpl(String id)
    {
        this.id = id;
    }

    public PortletWindowImpl()
    {
        super();
    }    

    /**
    * Returns the identifier of this portlet instance window as object id
    *
    * @return the object identifier
    **/
    public PortletWindowID getId()
    {
        return this;
    }
    
    public String getStringId()
    {
        return id;
    }
    
    /**
     * Returns the portlet entity
     *
     * @return the portlet entity
     **/
    public PortletEntity getPortletEntity()
    {
        return portletEntity;
    }

    // controller impl
    /**
     * binds an identifier to this portlet window
     *
     * @param id the new identifier
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * binds a portlet instance to this portlet window
     * 
     * @param portletEntity a portlet entity object
     **/
    public void setPortletEntity(PortletEntity portletEntity)
    {
        this.portletEntity = portletEntity;
        this.portletEntity.setPortletWindow(this);
    }
    
    /**
     * Sets flag that the content is instantly rendered from JPT.
     */
    public void setInstantlyRendered(boolean instantlyRendered)
    {
        this.instantlyRendered = instantlyRendered;
    }
    
    /**
     * Checks if the content is instantly rendered from JPT.
     */
    public boolean isInstantlyRendered()
    {
        return this.instantlyRendered;
    }

    public PortletMode getPortletMode()
    {
        // TODO: 2.2 this works, but we might want to better wire things in
        return Jetspeed.getCurrentRequestContext().getPortalURL().getNavigationalState().getMode(this);
    }

    public void setPortletMode(PortletMode portletMode)
    {
        this.portletMode = portletMode;
    }

    public WindowState getWindowState()
    {
        // TODO: 2.2 this works, but we might want to better wire things in
        return Jetspeed.getCurrentRequestContext().getPortalURL().getNavigationalState().getState(this);
    }

    public void setWindowState(WindowState windowState)
    {
        this.windowState = windowState;
    }
}
