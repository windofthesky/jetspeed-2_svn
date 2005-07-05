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

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import javax.portlet.PortalContext;

import org.apache.jetspeed.Jetspeed;
import org.apache.pluto.services.information.PortalContextProvider;

/**
 * Provide information about the calling portal.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortalContextProviderImpl.java 185962 2004-03-08 01:03:33Z jford $
 */
public class PortalContextProviderImpl 
    implements PortalContextProvider
{
    PortalContextImpl portalContext = null;
    /** Portal information */

    private String info = null;
    private String portalName = null;
    private String portalVersion = null;
    
    /** supported portlet modes by this portal */
    private Vector modes;

    /** supported window states by this portal */
    private Vector states;

    /** portal properties */
    private HashMap properties = new HashMap();


    public PortalContextProviderImpl()
    {
        portalContext = new PortalContextImpl(this);
        
        modes = getDefaultModes();

        // these are the minimum states that the portal needs to support

        states = getDefaultStates(); 

        // set info
        portalName = Jetspeed.getContext().getConfiguration().getString("portal.name");
        portalVersion = Jetspeed.getContext().getConfiguration().getString("portal.version");         
        info = portalName + "/" + portalVersion;   
        
    }
    
    /* (non-Javadoc)
     * @see org.apache.pluto.services.information.PortalContextProvider#getPortalContext()
     */
    public PortalContext getPortalContext() 
    {
        return portalContext;
    }
    
    /** 
     * <p>
     * getPortalInfo
     * </p>
     * 
     * @see org.apache.pluto.services.information.PortalContextProvider#getPortalInfo()
     * @return
     */
    public String getPortalInfo()
    {
        return info;
    }

    /** 
     * <p>
     * getProperty
     * </p>
     * 
     * @see org.apache.pluto.services.information.PortalContextProvider#getProperty(java.lang.String)
     * @param name
     * @return
     */
    public String getProperty(String name)
    {        
        if (name == null) 
        {
            throw new IllegalArgumentException("Property name == null");
        }
        return(String) properties.get(name);
    }

    /** 
     * <p>
     * getPropertyNames
     * </p>
     * 
     * @see org.apache.pluto.services.information.PortalContextProvider#getPropertyNames()
     * @return
     */
    public Collection getPropertyNames()
    {     
        return properties.keySet();
    }

    /** 
     * <p>
     * getSupportedPortletModes
     * </p>
     * 
     * @see org.apache.pluto.services.information.PortalContextProvider#getSupportedPortletModes()
     * @return
     */
    public Collection getSupportedPortletModes()
    {
        return modes;
    }

    /** 
     * <p>
     * getSupportedWindowStates
     * </p>
     * 
     * @see org.apache.pluto.services.information.PortalContextProvider#getSupportedWindowStates()
     * @return
     */
    public Collection getSupportedWindowStates()
    {        
        return states;
    }

    private Vector getDefaultModes()
    {
        Vector m = new Vector();
        String[] supportedModes = Jetspeed.getContext().getConfiguration().getStringArray("supported.portletmode");
        
        for (int i=0; i<supportedModes.length; i++) 
        {
            m.add(new PortletMode(supportedModes[i].toString().toLowerCase()));
        }

        return m;
    }

    private Vector getDefaultStates()
    {
        Vector s = new Vector();
        String[] supportedStates = Jetspeed.getContext().getConfiguration().getStringArray("supported.windowstate");

        for (int i=0; i<supportedStates.length; i++) 
        {
            s.add(new WindowState(supportedStates[i].toString().toLowerCase()));
        }

        return s;
    }

    public void setProperty(String name, String value)
    {
        if (name == null) 
        {
            throw new IllegalArgumentException("Property name == null");
        }
        properties.put(name, value);
    }      

    // expects enumeration of PortletMode objects

    public void setSupportedPortletModes(Enumeration portletModes)
    {
        Vector v = new Vector();

        while (portletModes.hasMoreElements()) 
        {
            v.add(portletModes.nextElement());
        }

        modes = v;
    }



    // expects enumeration of WindowState objects

    public void setSupportedWindowStates(Enumeration windowStates)
    {
        Vector v = new Vector();
        while (windowStates.hasMoreElements()) 
        {
            v.add(windowStates.nextElement());
        }

        states = v;
    }



    /**
     * reset all values to default portlet modes and window states;
     * delete all properties and set the given portlet information
     * as portlet info string.
     * 
     * @param  
     * @param portalInfo  portal information string that will be returned
     *                    by the <code>getPortalInfo</code> call.
     */
    public void reset(String portalInfo)

    {
        info = new String(portalInfo);

        // these are the minimum modes that the portal needs to support
        modes = getDefaultModes();

        // these are the minimum states that the portal needs to support
        states = getDefaultStates();    

        properties.clear();
    }

}
