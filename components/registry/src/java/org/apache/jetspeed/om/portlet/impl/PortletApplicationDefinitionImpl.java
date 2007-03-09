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
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.jetspeed.om.common.portlet.CustomPortletMode;
import org.apache.jetspeed.om.common.portlet.CustomWindowState;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.jetspeed.util.JetspeedLongObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 * @since 1.0
 */
public class PortletApplicationDefinitionImpl implements MutablePortletApplication, Serializable, Support
{ 
    /**
     * Unique id of the application.  This serves as the primary key in database
     * and in any caching of this object.
     */
    private Long id;
    
    private JetspeedLongObjectID oid;
    
    /** Holds value of property name. */
    private String name;

    /** Holds value of property version. */
    private String version;

    /** Holds the optional application identifier from the portlet.xml */
    private String applicationIdentifier;

    /** WebApplication property */
    private WebApplicationDefinition webApplication;
    /** PK of this Portlet Application's Web Application */
    protected long webApplicationId;
    
    /** Metadata property */
    private Collection metadataFields = null;

    /** Metadata property */
    private Collection services = new ArrayList();
    
    /** Description */
    private String description;

    private Collection portlets;

    /** User attribute refs collection. */
    private Collection userAttributeRefs;
    
    /** User attributes collection. */
    private Collection userAttributes;
    
    private PortletDefinitionListImpl listWrapper = new PortletDefinitionListImpl();

    private int applicationType = MutablePortletApplication.WEBAPP;
    
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
        portlets = new ArrayList();
        userAttributes = new ArrayList();        
        userAttributeRefs = new ArrayList();
        customPortletModes = new ArrayList();
        customWindowStates = new ArrayList();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getId()
     */
    public ObjectID getId()
    {
        if ( oid == null && id != null )
        {
            oid = new JetspeedLongObjectID(id);
        }
        return oid;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#setName(String)
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

    /**
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getPortletDefinitionList()
     */
    public PortletDefinitionList getPortletDefinitionList()
    {
        return new PortletDefinitionListImpl(portlets);
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
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setWebApplicationDefinition(org.apache.pluto.om.servlet.WebApplicationDefinition)
     */
    public void setWebApplicationDefinition(WebApplicationDefinition wad)
    {
        this.webApplication = wad;

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#addPortletDefinition(org.apache.pluto.om.portlet.PortletDefinition)
     */
    public void addPortletDefinition(PortletDefinition pd)
    {
       ((PortletDefinitionComposite) pd).setPortletApplicationDefinition(this);
        portlets.add(pd);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getPortletDefinitions()
     */
    public Collection getPortletDefinitions()
    {
        return portlets;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getPortletDefinitionByName(java.lang.String)
     */
    public PortletDefinition getPortletDefinitionByName(String name)
    {
    	listWrapper.setInnerCollection(portlets);
        return listWrapper.get(name);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setPortletDefinitionList(org.apache.pluto.om.portlet.PortletDefinitionList)
     */
    public void setPortletDefinitionList(PortletDefinitionList portlets)
    {
        this.portlets = ((PortletDefinitionListImpl) portlets).getInnerCollection();
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setUserAttributeRefs(java.util.Collection)
     */
    public void setUserAttributeRefs(Collection userAttributeRefs)
    {
        this.userAttributeRefs = userAttributeRefs;
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getUserAttributeRefs()
     */
    public Collection getUserAttributeRefs()
    {
        return this.userAttributeRefs;
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#addUserAttributeRef(org.apache.jetspeed.om.common.UserAttributeRef)
     */
    public void addUserAttributeRef(UserAttributeRef userAttributeRef)
    {
        userAttributeRefs.add(userAttributeRef);
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#addUserAttribute(org.apache.jetspeed.om.common.UserAttribute)
     */
    public void addUserAttribute(UserAttribute userAttribute)
    {
        userAttributes.add(userAttribute);
    }
    
    /** 
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#addUserAttribute(java.lang.String, java.lang.String)
     */
    public void addUserAttribute(String userName, String description)
    {
        UserAttributeImpl userAttribute = new UserAttributeImpl();
        userAttribute.setName(userName);
        userAttribute.setDescription(description);
        userAttributes.add(userAttribute);
    }
    
    /** 
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setUserAttributes(java.util.Collection)
     */
    public void setUserAttributes(Collection userAttributes)
    {
        this.userAttributes = userAttributes;
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getUserAttributes()
     */
    public Collection getUserAttributes()
    {
        return this.userAttributes;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setApplicationIdentifier(java.lang.String)
     */
    public void setApplicationIdentifier(String applicationIdentifier)
    {
        this.applicationIdentifier = applicationIdentifier;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getApplicationIdentifier()
     */
    public String getApplicationIdentifier()
    {
        return this.applicationIdentifier;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setApplicationType(int)
     */
    public void setApplicationType(int type)
    {
        this.applicationType = type;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getApplicationType()
     */
    public int getApplicationType()
    {
        return applicationType;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getMetadata()
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

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setMetadata(org.apache.jetspeed.om.common.GenericMetadata)
     */
    public void setMetadata(GenericMetadata metadata)
    {
        this.metadataFields = metadata.getFields();     
    }

    /**
     * @return
     */
    protected Collection getMetadataFields()
    {
        return metadataFields;
    }

    /**
     * @param collection
     */
    protected void setMetadataFields(Collection metadataFields)
    {
        this.metadataFields = metadataFields;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getJetspeedServices()
     */
    public Collection getJetspeedServices()
    {
        return services;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#addJetspeedService(org.apache.jetspeed.om.common.JetspeedServiceReference)
     */
    public void addJetspeedService(JetspeedServiceReference service)
    {
        services.add(service);
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
        Iterator portletDefinitions = getPortletDefinitions().iterator();
        while (portletDefinitions.hasNext())
        {
            ((Support) portletDefinitions.next()).postLoad(this);
        }
    }

    public Collection getCustomPortletModes()
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
