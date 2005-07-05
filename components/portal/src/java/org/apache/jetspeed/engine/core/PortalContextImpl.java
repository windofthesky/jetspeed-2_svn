/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.engine.core;

import java.util.Enumeration;

import javax.portlet.PortalContext;

import org.apache.pluto.services.information.PortalContextProvider;
import org.apache.pluto.util.Enumerator;

/**
 * PortalContextImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortalContextImpl.java 185962 2004-03-08 01:03:33Z jford $
 */
public class PortalContextImpl implements PortalContext
{
    PortalContextProvider provider = null;

    public PortalContextImpl(PortalContextProvider provider) 
    {
        this.provider = provider;
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getProperty(java.lang.String)
     */
    public String getProperty(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Property name == null");
        }
        return provider.getProperty(name);
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getPropertyNames()
     */
    public Enumeration getPropertyNames()
    {
        return(new Enumerator(provider.getPropertyNames()));
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getSupportedPortletModes()
     */
    public Enumeration getSupportedPortletModes()
    {
        return new Enumerator(provider.getSupportedPortletModes());
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getSupportedWindowStates()
     */
    public Enumeration getSupportedWindowStates()
    {
        return new Enumerator(provider.getSupportedWindowStates());        
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getPortalInfo()
     */
    public String getPortalInfo()
    {
        return provider.getPortalInfo();        
    }
}
