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
package org.apache.jetspeed.container.url.impl;

import java.util.Iterator;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * PortalURL defines the interface for manipulating Jetspeed Portal URLs.
 * These URLs are used internally by the portal and are not available to
 * Portlet Applications. This implementation stores its navigational state
 * in the session and does not encode navigational state in the URL.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SessionPortalURL extends AbstractPortalURL implements PortalURL 
{
    private String stateKey = null;
    private WindowState state = null;
    private String modeKey = null;
    private PortletMode mode = null;
    
    public SessionPortalURL(RequestContext context, NavigationalStateComponent nsc)
    {
        super(context, nsc);        
        // analyze();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#setState(org.apache.pluto.om.window.PortletWindow, javax.portlet.WindowState)
     */
    public void setState(PortletWindow window, WindowState state) 
    {
        stateKey = getStateKey(window);
        this.state = state;
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#setMode(org.apache.pluto.om.window.PortletWindow, javax.portlet.PortletMode)
     */
    public void setMode(PortletWindow window, PortletMode mode) 
    {
        modeKey = getModeKey(window);
        this.mode = mode;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#getState(org.apache.pluto.om.window.PortletWindow)
     */
    public WindowState getState(PortletWindow window) 
    {
        return this.state; 
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#getMode(org.apache.pluto.om.window.PortletWindow)
     */
    public PortletMode getMode(PortletWindow window) 
    {
        return this.mode;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#getPreviousMode(org.apache.pluto.om.window.PortletWindow)
     */
    public PortletMode getPreviousMode(PortletWindow window) 
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#getPreviousState(org.apache.pluto.om.window.PortletWindow)
     */
    public WindowState getPreviousState(PortletWindow window) 
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#getRenderParamNames(org.apache.pluto.om.window.PortletWindow)
     */
    public Iterator getRenderParamNames(PortletWindow window) 
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#getRenderParamValues(org.apache.pluto.om.window.PortletWindow, java.lang.String)
     */
    public String[] getRenderParamValues(PortletWindow window, String paramName) 
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#getPortletWindowOfAction()
     */
    public PortletWindow getPortletWindowOfAction() 
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#clearRenderParameters(org.apache.pluto.om.window.PortletWindow)
     */
    public void clearRenderParameters(PortletWindow portletWindow) 
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#setAction(org.apache.pluto.om.window.PortletWindow)
     */
    public void setAction(PortletWindow window) 
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#setRequestParam(java.lang.String, java.lang.String[])
     */
    public void setRequestParam(String name, String[] values) 
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#setRenderParam(org.apache.pluto.om.window.PortletWindow, java.lang.String, java.lang.String[])
     */
    public void setRenderParam(PortletWindow window, String name,
            String[] values) 
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.container.url.PortalURL#toString(boolean)
     */
    public String toString(boolean secure) 
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
