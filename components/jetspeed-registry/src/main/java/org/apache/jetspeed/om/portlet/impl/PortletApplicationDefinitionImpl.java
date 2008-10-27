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
import javax.xml.namespace.QName;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.CustomPortletMode;
import org.apache.jetspeed.om.portlet.CustomWindowState;
import org.apache.jetspeed.om.portlet.EventDefinition;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.FilterMapping;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.PublicRenderParameter;
import org.apache.jetspeed.om.portlet.SecurityConstraint;
import org.apache.jetspeed.om.portlet.UserAttribute;
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
    private transient List<PortletMode> supportedPortletModes;
    private transient List<WindowState> supportedWindowStates;
    
    /** Creates a new instance of BaseApplication */
    public PortletApplicationDefinitionImpl()
    {
        portlets = new ArrayList<PortletDefinition>();
        userAttributes = new ArrayList();        
        userAttributeRefs = new ArrayList();
        customPortletModes = new ArrayList<PortletMode>();
        customWindowStates = new ArrayList<WindowState>();
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
    
    public List<PortletMode> getSupportedPortletModes()
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
            supportedPortletModes = Collections.unmodifiableList(list);
        }
        return supportedPortletModes;
    }
    
    public List<CustomWindowState> getCustomWindowStates()
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
    
    public List<WindowState> getSupportedWindowStates()
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
            supportedWindowStates = Collections.unmodifiableList(list);
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

    public ContainerRuntimeOption addContainerRuntimeOption(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public CustomPortletMode addCustomPortletMode(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public CustomWindowState addCustomWindowState(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public EventDefinition addEventDefinition(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public EventDefinition addEventDefinition(QName qname)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Filter addFilter(String filterName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public FilterMapping addFilterMapping(String filterName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void addJetspeedServiceReference(String name)
    {
        // TODO Auto-generated method stub
        
    }

    public Listener addListener(String listenerClass)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletDefinition addPortlet(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PublicRenderParameter addPublicRenderParameter(String name, String identifier)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PublicRenderParameter addPublicRenderParameter(QName qname, String identifier)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public SecurityConstraint addSecurityConstraint(String transportGuarantee)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAttribute addUserAttribute(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAttributeRef addUserAttributeRef(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ContainerRuntimeOption getContainerRuntimeOption(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ContainerRuntimeOption> getContainerRuntimeOptions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public CustomPortletMode getCustomPortletMode(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public CustomWindowState getCustomWindowState(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<EventDefinition> getEventDefinitions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Filter getFilter(String filterName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public FilterMapping getFilterMapping(String filterName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<FilterMapping> getFilterMappings()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Filter> getFilters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Listener> getListeners()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<PortletDefinition> getPortlets()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PublicRenderParameter getPublicRenderParameter(String identifier)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<PublicRenderParameter> getPublicRenderParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<SecurityConstraint> getSecurityConstraints()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAttribute getUserAttribute(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAttributeRef getUserAttributeRef(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<UserAttributeRef> getUserAttributeRefs()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<UserAttribute> getUserAttributes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDefaultNamespace()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getResourceBundle()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDefaultNamespace(String defaultNamespace)
    {
        // TODO Auto-generated method stub
        
    }

    public void setResourceBundle(String resourceBundle)
    {
        // TODO Auto-generated method stub
        
    }
}
