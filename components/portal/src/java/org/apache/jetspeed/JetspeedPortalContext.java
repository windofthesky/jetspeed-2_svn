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
package org.apache.jetspeed;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.engine.Engine;
import org.apache.pluto.util.Enumerator;

/**
 * Implementation of Portal Context associated with running thread of the engine
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortalContext implements PortalContext
{
    private static final String SUPPORTED_WINDOWSTATE_ATTR = "supported.windowstate";
    private static final String SUPPORTED_PORTLETMODE_ATTR = "supported.portletmode";
    private static final String PORTAL_VERSION_ATTR = "portal.version";
    private static final String PORTAL_NAME_ATTR = "portal.name";
    /**
     * The engine associated with this context.
     */
    private Engine engine = null;

    /**
     * Runtime attributes.
     */
    private HashMap attributes = new HashMap();

    /**
     * Configuration state
     */
    private Configuration configuration = null;

    /**
     * The base from which the Jetspped application will operate.
     */
    private String applicationRoot;
    
      /** Portlet Modes */
    private final List portletModes;
    
    /** Window States */
    private final List windowStates;

    private final String portalName;

    private final String portalVersion;


    public JetspeedPortalContext(Engine engine, Configuration configuration, String applicationRoot)
    {
        this.engine = engine;
        this.configuration = configuration;
        this.applicationRoot = applicationRoot;
             
        
        portalName = configuration.getString(PORTAL_NAME_ATTR);
        portalVersion = configuration.getString(PORTAL_VERSION_ATTR);
        
        // Inititalize supported portlet modes 
        String[] supportedModes = configuration.getStringArray(SUPPORTED_PORTLETMODE_ATTR);
        portletModes = new ArrayList(supportedModes.length);        
        for (int i=0; i<supportedModes.length; i++) 
        {
            portletModes.add(new PortletMode(supportedModes[i].toString().toLowerCase()));
        }
        
        // Initialize supported window states         
        String[] supportedStates = configuration.getStringArray(SUPPORTED_WINDOWSTATE_ATTR);
        windowStates = new ArrayList(supportedStates.length);
        for (int i=0; i<supportedStates.length; i++) 
        {
            windowStates.add(new WindowState(supportedStates[i].toString().toLowerCase()));
        }       
    }

    // ------------------------------------------------------------------------
    //  A C C E S S O R S
    // ------------------------------------------------------------------------

    /**
     * Returns the configuration properties for this Jetspeed engine context.
     *
     * @return a <code>Configuration</code> containing the configuration properties for this Jetspeed context.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    public String getConfigurationProperty(String key)
    {
        return configuration.getString(key);
    }

    public String getConfigurationProperty(String key, String defaultValue)
    {
        return configuration.getString(key, defaultValue);
    }

    /**
     * Set the configuration properties for this Jetspeed engine context.
     *
     * @param configuration - the configuration properties
     */
    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }

    /**
     * Returns the application root for this Jetspeed engine context.
     *
     * @return a <code>String</code> containing the application root path for this Jetspeed context.
     */
    public String getApplicationRoot()
    {
        return applicationRoot;
    }

    /**
     * Sets the application root path for this Jetspeed engine context.
     *
     * @param applicationRoot - the applicationRoot path on the file system.
     */
    public void setApplicationRoot(String applicationRoot)
    {
        this.applicationRoot = applicationRoot;
    }

    /**
     * Returns the engine associated with this context.
     *
     * @return an <code>Engine</code> associated with this context
     */
    public Engine getEngine()
    {
        return this.engine;
    }

    /**
     * Returns the engine attribute with the given name, or null if there is no attribute by that name.
     *
     * @return an <code>Object</code> containing the value of the attribute, or null if no attribute exists matching the given name
     */
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }


    /**
     * Binds an object to a given attribute name in this servlet context.
     *
     * @param  name - a <code>String</code> specifying the name of the attribute
     * @param value - an <code>Object</code> representing the attribute to be bound
     */
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
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
        return(String) configuration.getProperty(name);
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getPropertyNames()
     */
    public Enumeration getPropertyNames()
    {
        return new Enumerator(configuration.getKeys());
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getSupportedPortletModes()
     */
    public Enumeration getSupportedPortletModes()
    {
        return new Enumerator(portletModes);
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getSupportedWindowStates()
     */
    public Enumeration getSupportedWindowStates()
    {
        return new Enumerator(windowStates);
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortalContext#getPortalInfo()
     */
    public String getPortalInfo()
    {
        return portalName + "/" + portalVersion;
    }
}