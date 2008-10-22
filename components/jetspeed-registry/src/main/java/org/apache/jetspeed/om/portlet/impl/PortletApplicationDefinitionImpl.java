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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.jetspeed.om.portlet.CustomPortletMode;
import org.apache.jetspeed.om.portlet.CustomWindowState;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.om.servlet.WebApplicationDefinition;

/**
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 * @since 1.0
 */
public class PortletApplicationDefinitionImpl implements PortletApplication, Serializable, Support
{ 
    /** Holds value of property name. */
    private String name;

    /** Holds value of property version. */
    private String version;

    /** WebApplication property */
    private transient WebApplicationDefinition webApplication;
    
    /** Metadata property */
    private Collection metadataFields = null;

    /** Metadata property */
    private List<JetspeedServiceReference> services = new ArrayList<JetspeedServiceReference>();
    
    /** Description */
    private String description;

    /** User attribute refs collection. */
    private List<UserAttributeRefImpl> userAttributeRefs;
    
    /** User attributes collection. */
    private Collection userAttributes;
    
    protected List<PortletDefinition> portlets;
    
    private int applicationType = PortletApplication.WEBAPP;
    
    private String checksum = "0";
    private long checksumLong = -1;
    
    private List customPortletModes;
    private List customWindowStates;
    
    private String jetspeedSecurityConstraint = null;
    
    private transient Map supportedCustomModes;
    private transient Map supportedCustomStates;
    private transient Map mappedCustomModes;
    private transient Map mappedCustomStates;    
    private transient Collection supportedPortletModes;
    private transient Collection supportedWindowStates;
    
    /** Creates a new instance of BaseApplication */
    public PortletApplicationDefinitionImpl()
    {
        portlets = new ArrayList<PortletDefinition>();
        userAttributes = new ArrayList();        
        userAttributeRefs = new ArrayList();
        customPortletModes = new ArrayList();
        customWindowStates = new ArrayList();
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#getPortletName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#setPortletName(String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#getVersion()
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#setVersion(String)
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return
     */
    public WebApplicationDefinition getWebApplicationDefinition()
    {
        return webApplication;
    }

    public PortletDefinition getPortlet(String portletName)
    {
        for (PortletDefinition pd : getPortlets())
        {
            if (pd.getPortletName().equals(portletName))
            {
                return pd;
            }
        }
        return null;
    }

    public ElementFactoryList<PortletDefinition> getPortlets()
    {
        if (portlets == null || !(portlets instanceof ElementFactoryList))
        {
            ElementFactoryList<PortletDefinition> lf = 
                new ElementFactoryList<PortletDefinition>( new ElementFactoryList.Factory<PortletDefinition>()
                {
                    public Class<? extends PortletDefinition> getElementClass()
                    {
                        return PortletDefinitionImpl.class;
                    }

                    public PortletDefinition newElement()
                    {
                        return new PortletDefinitionImpl();
                    }
                }); 
            if (portlets != null)
            {
                lf.addAll(portlets);
            }
            portlets = lf;
        }
        return (ElementFactoryList<PortletDefinition>)portlets;
    }

    /**
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param string
     */
    public void setDescription(String string)
    {
        description = string;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#setWebApplicationDefinition(org.apache.pluto.om.servlet.WebApplicationDefinition)
     */
    public void setWebApplicationDefinition(WebApplicationDefinition wad)
    {
        this.webApplication = wad;

    }

    /** 
     * @see org.apache.jetspeed.om.portlet.PortletApplication#addUserAttribute(java.lang.String, java.lang.String)
     */
    public void addUserAttribute(String userName, String description)
    {
        UserAttributeImpl userAttribute = new UserAttributeImpl();
        userAttribute.setName(userName);
        userAttribute.setDescription(description);
        userAttributes.add(userAttribute);
    }
    
    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#setApplicationType(int)
     */
    public void setApplicationType(int type)
    {
        this.applicationType = type;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#getApplicationType()
     */
    public int getApplicationType()
    {
        return applicationType;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
    	if(metadataFields == null)
        {
            metadataFields = new ArrayList();
        }
    	
    	GenericMetadata metadata = new PortletApplicationMetadataImpl();
    	metadata.setFields(metadataFields);
        
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getJetspeedServices()
     */
    public List<JetspeedServiceReference> getJetspeedServices()
    {
        return services;
    }
    
    public long getChecksum()
    {
        if(checksumLong == -1)
        {
            checksumLong = Long.parseLong(checksum);
        }
        return checksumLong;
    }
    
    public void setChecksum(long checksum)
    {
        this.checksumLong = checksum;
        this.checksum = Long.toString(checksum);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.Support#postLoad(java.lang.Object)
     */
    public void postLoad(Object parameter) throws Exception
    {
        for (PortletDefinition pd : portlets)
        {
            ((Support)pd).postLoad(this);
        }
    }

    public List<CustomPortletMode> getCustomPortletModes()
    {
        return customPortletModes;
    }
    
    public void addCustomPortletMode(CustomPortletMode customPortletMode)
    {
        // clear transient cache
        supportedPortletModes = null;
        supportedCustomModes = null;
        mappedCustomModes = null;
        
        if ( !customPortletModes.contains(customPortletMode) )
        {
            customPortletModes.add(customPortletMode);
        }
    }
    
    public void setCustomPortletModes(Collection customPortletModes)
    {
        // clear transient cache
        supportedPortletModes = null;
        supportedCustomModes = null;
        mappedCustomModes = null;

        this.customPortletModes.clear();
        
        if ( customPortletModes != null )
        {
            this.customPortletModes.addAll(customPortletModes);
        }
    }
    
    public PortletMode getMappedPortletMode(PortletMode mode)
    {
        if ( JetspeedActions.getStandardPortletModes().contains(mode) )
        {
            return mode;
        }
        else if ( getSupportedPortletModes().contains(mode) )
        {
            return (PortletMode)mappedCustomModes.get(mode);
        }
        return null;
    }
    
    public PortletMode getCustomPortletMode(PortletMode mode)
    {
        if (JetspeedActions.getStandardPortletModes().contains(mode))
        {
            return mode;
        }
        else if (JetspeedActions.getExtendedPortletModes().contains(mode))
        {
            // make sure transient cache is setup
            getSupportedPortletModes();
            return (PortletMode)supportedCustomModes.get(mode);
        }
        return null;            
    }
    
    public Collection getSupportedPortletModes()
    {
        if ( supportedPortletModes == null )
        {
            ArrayList list = new ArrayList(JetspeedActions.getStandardPortletModes());
            supportedCustomModes = new HashMap();
            mappedCustomModes = new HashMap();
            
            if ( customPortletModes.size() > 0 )
            {
                Iterator iter = customPortletModes.iterator();
                while ( iter.hasNext() )
                {
                    CustomPortletMode customMode = (CustomPortletMode)iter.next();
                    if ( !list.contains(customMode.getCustomMode()) && JetspeedActions.getExtendedPortletModes().contains(customMode.getMappedMode()) )
                    {
                        list.add(customMode.getCustomMode());
                        supportedCustomModes.put(customMode.getMappedMode(), customMode.getCustomMode());
                        mappedCustomModes.put(customMode.getCustomMode(), customMode.getMappedMode());
                    }
                }
            }
            supportedPortletModes = Collections.unmodifiableCollection(list);
        }
        return supportedPortletModes;
    }
    
    public Collection getCustomWindowStates()
    {
        return customWindowStates;
    }
    
    public void addCustomWindowState(CustomWindowState customWindowState)
    {
        // clear transient cache
        supportedWindowStates = null;
        supportedCustomStates = null;
        mappedCustomStates = null;
        
        if ( !customWindowStates.contains(customWindowState) )
        {
            customWindowStates.add(customWindowState);
        }
    }
    
    public void setCustomWindowStates(Collection customWindowStates)
    {
        // clear transient cache
        supportedWindowStates = null;
        supportedCustomStates = null;
        mappedCustomStates = null;

        this.customWindowStates.clear();

        if ( customWindowStates != null )
        {
            this.customWindowStates.addAll(customWindowStates);
        }
    }
    
    public WindowState getMappedWindowState(WindowState state)
    {
        if (JetspeedActions.getStandardWindowStates().contains(state) )
        {
            return state;
        }
        else if ( getSupportedWindowStates().contains(state) )
        {
            return (WindowState)mappedCustomStates.get(state);
        }
        return null;
    }
    
    public WindowState getCustomWindowState(WindowState state)
    {
        if (JetspeedActions.getStandardWindowStates().contains(state))
        {
            return state;
        }
        else if (JetspeedActions.getExtendedWindowStates().contains(state))
        {
            // make sure transient cache is setup
            getSupportedWindowStates();
            return (WindowState)supportedCustomStates.get(state);
        }
        return null;            
    }
    
    public Collection getSupportedWindowStates()
    {
        if ( supportedWindowStates == null )
        {
            ArrayList list = new ArrayList(JetspeedActions.getStandardWindowStates());
            supportedCustomStates = new HashMap();
            mappedCustomStates = new HashMap();
            
            if ( customWindowStates.size() > 0 )
            {
                Iterator iter = customWindowStates.iterator();
                while ( iter.hasNext() )
                {
                    CustomWindowState customState = (CustomWindowState)iter.next();
                    if ( !list.contains(customState.getCustomState()) && JetspeedActions.getExtendedWindowStates().contains(customState.getMappedState()) )
                    {
                        list.add(customState.getCustomState());
                        supportedCustomStates.put(customState.getMappedState(),customState.getCustomState());
                        mappedCustomStates.put(customState.getCustomState(),customState.getMappedState());
                    }
                }
            }
            supportedWindowStates = Collections.unmodifiableCollection(list);
        }
        return supportedWindowStates;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getJetspeedSecurityConstraint()
     */
    public String getJetspeedSecurityConstraint()
    {
        return this.jetspeedSecurityConstraint;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setJetspeedSecurityConstraint(java.lang.String)
     */
    public void setJetspeedSecurityConstraint(String constraint)
    {
        this.jetspeedSecurityConstraint = constraint;
    }
    
    public boolean isLayoutApplication()
    {
        if (this.getMetadata() != null)
        {
            Collection c = this.getMetadata().getFields("layout-app");
            if (c != null)
            {
                if (!c.isEmpty())
                {
                   return true;
                }
            }
        }
        return false;
     }
}
