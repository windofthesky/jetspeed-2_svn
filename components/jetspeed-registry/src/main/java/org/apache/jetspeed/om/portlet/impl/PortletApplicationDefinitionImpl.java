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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.CustomPortletMode;
import org.apache.jetspeed.om.portlet.CustomWindowState;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.EventDefinition;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.FilterMapping;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.LocaleEncodingMapping;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.PublicRenderParameter;
import org.apache.jetspeed.om.portlet.SecurityConstraint;
import org.apache.jetspeed.om.portlet.SecurityRole;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.pluto.container.om.portlet.UserDataConstraint;

/**
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 * @since 1.0
 */
public class PortletApplicationDefinitionImpl implements PortletApplication, Serializable, Support, PersistenceBrokerAware
{ 
    private static final long serialVersionUID = 1L;

    private int applicationType = PortletApplication.WEBAPP;
    
    private String checksum = "0";
    private long checksumLong = -1;
    private long revision;
    
    /** Holds value of property version. */
    private String version;

    /** Holds value of property name. */
    private String name;
    
    private String contextPath;

    /** Metadata property */
    private Collection<LocalizedField> metadataFields = null;
    
    private String resourceBundle;
    private String defaultNamespace;
    
    private String jetspeedSecurityConstraint;
    
    private List<Description> descriptions;
    private List<DisplayName> displayNames;
    private List<SecurityRole> roles;
    private List<PortletDefinition> portlets;
    private List<PortletDefinition> clones;
    private List<EventDefinition> eventDefinitions;
    private List<PublicRenderParameter> publicRenderParameters;
    private List<CustomPortletMode> customPortletModes;
    private List<CustomWindowState> customWindowStates;
    private List<UserAttribute> userAttributes;
    private List<SecurityConstraint> securityConstraints;
    private List<Filter> filters;
    private List<FilterMapping> filterMappings;
    private List<Listener> listeners;
    private List<ContainerRuntimeOption> containerRuntimeOptions;

    private List<UserAttributeRef> userAttributeRefs;
    private List<JetspeedServiceReference> services;

    private List<LocaleEncodingMapping> localeEncodingMappingList;
    
    private transient Map<PortletMode,PortletMode> supportedCustomModes;
    private transient Map<WindowState,WindowState> supportedCustomStates;
    private transient Map<PortletMode,PortletMode> mappedCustomModes;
    private transient Map<WindowState,WindowState> mappedCustomStates;    
    private transient List<PortletMode> supportedPortletModes;
    private transient List<WindowState> supportedWindowStates;
    private transient Map<Locale, String> localeEncodingMappings;
    
    private transient String localContextPath;
    
    /** Creates a new instance of BaseApplication */
    public PortletApplicationDefinitionImpl()
    {
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

    public long getRevision()
    {
        return revision;
    }

    public void setRevision(long revision)
    {
        this.revision = revision;
    }
    
    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }
    
    public String getContextPath()
    {
        if (applicationType == PortletApplication.LOCAL && localContextPath != null)
        {
            return localContextPath;
        }
        else
        {
            return contextPath;
        }
    }
    
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace)
    {
        this.defaultNamespace = defaultNamespace;
    }

    public String getResourceBundle()
    {
        return resourceBundle;
    }

    public void setResourceBundle(String resourceBundle)
    {
        this.resourceBundle = resourceBundle;
    }
    
    public String getJetspeedSecurityConstraint()
    {
        return this.jetspeedSecurityConstraint;
    }

    public void setJetspeedSecurityConstraint(String constraint)
    {
        this.jetspeedSecurityConstraint = constraint;
    }
    
    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#getMetadata()
     */
    @SuppressWarnings("unchecked")
    public GenericMetadata getMetadata()
    {
        if(metadataFields == null)
        {
            metadataFields = CollectionUtils.createCollection();
        }
        
        GenericMetadata metadata = new PortletApplicationMetadataImpl();
        metadata.setFields(metadataFields);
        
        return metadata;
    }
    
    public Description getDescription(Locale locale)
    {
        return (Description)JetspeedLocale.getBestLocalizedObject(getDescriptions(), locale);
    }
    
    @SuppressWarnings("unchecked")
    public List<Description> getDescriptions()
    {
        if (descriptions == null)
        {
            descriptions = CollectionUtils.createList();
        }
        return descriptions;
    }
    
    public Description addDescription(String lang)
    {
        DescriptionImpl d = new DescriptionImpl(this, lang);
        for (Description desc : getDescriptions())
        {
            if (desc.getLocale().equals(d.getLocale()))
            {
                throw new IllegalArgumentException("Description for language: "+d.getLocale()+" already defined");
            }
        }
        descriptions.add(d);
        return d;
    }

    public DisplayName getDisplayName(Locale locale)
    {
        return (DisplayName)JetspeedLocale.getBestLocalizedObject(getDisplayNames(), locale);
    }
    
    @SuppressWarnings("unchecked")
    public List<DisplayName> getDisplayNames()
    {
        if (displayNames == null)
        {
            displayNames = CollectionUtils.createList();
        }
        return displayNames;
    }
    
    public DisplayName addDisplayName(String lang)
    {
        DisplayNameImpl d = new DisplayNameImpl(this, lang);
        for (DisplayName dn : getDisplayNames())
        {
            if (dn.getLocale().equals(d.getLocale()))
            {
                throw new IllegalArgumentException("DisplayName for language: "+d.getLocale()+" already defined");
            }
        }
        displayNames.add(d);
        return d;
    }

    @SuppressWarnings("unchecked")
    public List<SecurityRole> getSecurityRoles()
    {
        if (roles == null)
        {
            roles = CollectionUtils.createList();
        }
        return roles;
    }
    
    public SecurityRole addSecurityRole(String name)
    {
        for (SecurityRole role : getSecurityRoles())
        {
            if (role.getName().equals(name))
            {
                throw new IllegalArgumentException("Security Role "+name+" already defined");
            }
        }
        SecurityRoleImpl role = new SecurityRoleImpl();
        role.setName(name);
        roles.add(role);
        return role;
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

    public PortletDefinition getClone(String cloneName)
    {
        for (PortletDefinition clone : getClones())
        {
            if (clone.getPortletName().equals(cloneName))
            {
                return clone;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<PortletDefinition> getPortlets()
    {
        if (portlets == null)
        {
            portlets = CollectionUtils.createList();
        }
        return portlets;
    }

    @SuppressWarnings("unchecked")
    public List<PortletDefinition> getClones()
    {
        if (clones == null)
        {
            clones = CollectionUtils.createList();
        }
        return clones;
    }


    public PortletDefinition addPortlet(String name)
    {
        if (getPortlet(name) != null)
        {
            throw new IllegalArgumentException("Portlet with name: "+name+" already defined");
        }
        PortletDefinitionImpl portlet = new PortletDefinitionImpl();
        portlet.setPortletName(name);
        portlet.setApplication(this);
        getPortlets().add(portlet);
        return portlet;
    }

    public PortletDefinition addClone(String name)
    {
        if (getClone(name) != null)
        {
            throw new IllegalArgumentException("Portlet/Clone with name: "+name+" already defined");
        }
        PortletDefinitionImpl clone = new PortletDefinitionImpl(this.getName());
        clone.setPortletName(name);
        clone.setApplication(this);       
        getClones().add(clone);
        return clone;
    }

    @SuppressWarnings("unchecked")
    public List<EventDefinition> getEventDefinitions()
    {
        if (eventDefinitions == null)
        {
            eventDefinitions = CollectionUtils.createList();
        }
        return eventDefinitions;
    }

    public EventDefinition addEventDefinition(String name)
    {
        QName qname = new QName(getDefaultNamespace(), name);
        return addEventDefinition(qname);
    }

    public EventDefinition addEventDefinition(QName qname)
    {
        List<EventDefinition> defs = getEventDefinitions();
        for (EventDefinition def : defs)
        {
            if (def.getQName().equals(qname))
            {
                return def;
            }
        }
        EventDefinitionImpl ed = new EventDefinitionImpl(qname);
        getEventDefinitions().add(ed);
        return ed;
    }

    public PublicRenderParameter getPublicRenderParameter(String identifier)
    {
        for (PublicRenderParameter p : getPublicRenderParameters())
        {
            if (p.getIdentifier().equals(identifier))
            {
                return p;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<PublicRenderParameter> getPublicRenderParameters()
    {
        if (publicRenderParameters == null)
        {
            publicRenderParameters = CollectionUtils.createList();
        }
        return publicRenderParameters;
    }

    public PublicRenderParameter addPublicRenderParameter(String name, String identifier)
    {
        if (getPublicRenderParameter(identifier) != null)
        {
            throw new IllegalArgumentException("PublicRenderParameter with identifier: "+identifier+" already defined");
        }
        PublicRenderParameterImpl p = new PublicRenderParameterImpl(name, identifier);
        if (!containsPublicRenderParameter(p))
        {
            getPublicRenderParameters().add(p);
        }
        return p;        
    }

    public PublicRenderParameter addPublicRenderParameter(QName qname, String identifier)
    {
        if (getPublicRenderParameter(identifier) != null)
        {
            throw new IllegalArgumentException("PublicRenderParameter with identifier: "+identifier+" already defined");
        }
        PublicRenderParameterImpl p = new PublicRenderParameterImpl(qname);        
        p.setIdentifier(identifier);
        if (!containsPublicRenderParameter(p))
        {
            getPublicRenderParameters().add(p);
        }
        return p;        
    }

    protected boolean containsPublicRenderParameter(PublicRenderParameter prp)
    {
        for (PublicRenderParameter p : this.publicRenderParameters)
        {
            if (p.equals(prp))
                return true;
        }
        return false;
    }
    
    public CustomPortletMode getCustomPortletMode(String name)
    {
        for (CustomPortletMode cpm : getCustomPortletModes())
        {
            if (cpm.getPortletMode().equalsIgnoreCase(name))
            {
                return cpm;
            }
        }
        return null;
    }

    public PortletMode getCustomPortletMode(PortletMode mode)
    {
        if (JetspeedActions.getStandardPortletModes().contains(mode))
        {
            return mode;
        }
        else
        {
            // make sure transient cache is setup
            getSupportedPortletModes();
            return supportedCustomModes.get(mode);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<CustomPortletMode> getCustomPortletModes()
    {
        if (customPortletModes == null)
        {
            customPortletModes = CollectionUtils.createList();
        }
        return customPortletModes;
    }
    
    public CustomPortletMode addCustomPortletMode(String name)
    {
        if (getCustomPortletMode(name) != null)
        {
            throw new IllegalArgumentException("Custom PortletMode with mode name: "+name+" already defined");
        }
        
        // clear transient cache
        supportedPortletModes = null;
        supportedCustomModes = null;
        mappedCustomModes = null;
        
        CustomPortletModeImpl cpm = new CustomPortletModeImpl();
        cpm.setPortletMode(name);
        getCustomPortletModes().add(cpm);
        return cpm;        
    }

    public CustomWindowState getCustomWindowState(String name)
    {
        for (CustomWindowState cws : getCustomWindowStates())
        {
            if (cws.getWindowState().equalsIgnoreCase(name))
            {
                return cws;
            }
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
            return supportedCustomStates.get(state);
        }
        return null;            
    }
    
    @SuppressWarnings("unchecked")
    public List<CustomWindowState> getCustomWindowStates()
    {
        if (customWindowStates == null)
        {
            customWindowStates = CollectionUtils.createList();
        }
        return customWindowStates;
    }
    
    public CustomWindowState addCustomWindowState(String name)
    {
        if (getCustomWindowState(name) != null)
        {
            throw new IllegalArgumentException("Custom WindowState with state name: "+name+" already defined");
        }

        // clear transient cache
        supportedWindowStates = null;
        supportedCustomStates = null;
        mappedCustomStates = null;
        
        CustomWindowStateImpl cws = new CustomWindowStateImpl();
        cws.setWindowState(name);
        getCustomWindowStates().add(cws);
        return cws;        
    }

    public List<PortletMode> getSupportedPortletModes()
    {
        if ( supportedPortletModes == null )
        {
            ArrayList<PortletMode> list = new ArrayList<PortletMode>(JetspeedActions.getStandardPortletModes());
            supportedCustomModes = new HashMap<PortletMode,PortletMode>();
            mappedCustomModes = new HashMap<PortletMode,PortletMode>();
            
            for (CustomPortletMode customMode : getCustomPortletModes())
            {
                if ( !list.contains(customMode.getCustomMode()) && (!customMode.isPortalManaged() || JetspeedActions.getExtendedPortletModes().contains(customMode.getMappedMode())) )
                {
                    list.add(customMode.getCustomMode());
                    supportedCustomModes.put(customMode.getMappedMode(), customMode.getCustomMode());
                    mappedCustomModes.put(customMode.getCustomMode(), customMode.getMappedMode());
                }
            }
            supportedPortletModes = Collections.unmodifiableList(list);
        }
        return supportedPortletModes;
    }
    
    public List<WindowState> getSupportedWindowStates()
    {
        if ( supportedWindowStates == null )
        {
            ArrayList<WindowState> list = new ArrayList<WindowState>(JetspeedActions.getStandardWindowStates());
            supportedCustomStates = new HashMap<WindowState,WindowState>();
            mappedCustomStates = new HashMap<WindowState,WindowState>();
            
            for (CustomWindowState customState : getCustomWindowStates())
            {
                if ( !list.contains(customState.getCustomState()) && JetspeedActions.getExtendedWindowStates().contains(customState.getMappedState()) )
                {
                    list.add(customState.getCustomState());
                    supportedCustomStates.put(customState.getMappedState(),customState.getCustomState());
                    mappedCustomStates.put(customState.getCustomState(),customState.getMappedState());
                }
            }
            supportedWindowStates = Collections.unmodifiableList(list);
        }
        return supportedWindowStates;
    }

    public PortletMode getMappedPortletMode(PortletMode mode)
    {
        if ( JetspeedActions.getStandardPortletModes().contains(mode) )
        {
            return mode;
        }
        else if ( getSupportedPortletModes().contains(mode) )
        {
            return mappedCustomModes.get(mode);
        }
        return null;
    }
    
    public WindowState getMappedWindowState(WindowState state)
    {
        if (JetspeedActions.getStandardWindowStates().contains(state) )
        {
            return state;
        }
        else if ( getSupportedWindowStates().contains(state) )
        {
            return mappedCustomStates.get(state);
        }
        return null;
    }
    
    public UserAttribute getUserAttribute(String name)
    {
        for (UserAttribute ua : getUserAttributes())
        {
            if (ua.getName().equals(name))
            {
                return ua;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<UserAttribute> getUserAttributes()
    {
        if (userAttributes == null)
        {
            userAttributes = CollectionUtils.createList();
        }
        return userAttributes;
    }

    public UserAttribute addUserAttribute(String name)
    {
        if (getUserAttribute(name) != null)
        {
            throw new IllegalArgumentException("User attribute with name: "+name+" already defined");
        }
        UserAttributeImpl ua = new UserAttributeImpl();
        ua.setName(name);
        getUserAttributes().add(ua);
        return ua;        
    }

    public UserAttributeRef getUserAttributeRef(String name)
    {
        for (UserAttributeRef uar : getUserAttributeRefs())
        {
            if (uar.getName().equals(name))
            {
                return uar;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<UserAttributeRef> getUserAttributeRefs()
    {
        if (userAttributeRefs == null)
        {
            userAttributeRefs = CollectionUtils.createList();
        }
        return userAttributeRefs;
    }

    public UserAttributeRef addUserAttributeRef(String name)
    {
        if (getUserAttributeRef(name) != null)
        {
            throw new IllegalArgumentException("User attribute reference with name: "+name+" already defined");
        }
        UserAttributeRefImpl uar = new UserAttributeRefImpl();
        uar.setName(name);
        getUserAttributeRefs().add(uar);
        return uar;        
    }

    @SuppressWarnings("unchecked")
    public List<SecurityConstraint> getSecurityConstraints()
    {
        if (securityConstraints == null)
        {
            securityConstraints = CollectionUtils.createList();
        }
        return securityConstraints;
    }

    private final String[]TRANSPORTS = 
    {
            UserDataConstraint.NONE,
            UserDataConstraint.INTEGRAL,
            UserDataConstraint.CONFIDENTIAL
    };
    
    public SecurityConstraint addSecurityConstraint(String transportGuarantee)
    {
        if (getSecurityConstraint(transportGuarantee) != null)
        {
            throw new IllegalArgumentException("Security Constraint with name: " + transportGuarantee + " already defined");
        }
        boolean isValid = false;
        for (String t : TRANSPORTS)
        {
            if (transportGuarantee.equalsIgnoreCase(t))
            {
                isValid = true;
                break;
            }
        }
        if (!isValid)
        {
            throw new IllegalArgumentException("Security Constraint with name: " + transportGuarantee + " is not a valid transport name");            
        }
        SecurityConstraint sc = new SecurityConstraintImpl();
        sc.getUserDataConstraint().setTransportGuarantee(transportGuarantee);
        getSecurityConstraints().add(sc);
        return sc;        
    }

    public SecurityConstraint getSecurityConstraint(String transport)
    {
        for (SecurityConstraint sc : getSecurityConstraints())
        {
            if (sc.getUserDataConstraint().getTransportGuarantee().equals(transport))
            {
                return sc;
            }
        }
        return null;
    }
    
    public Filter getFilter(String filterName)
    {
        for (Filter f : getFilters())
        {
            if (f.getFilterName().equals(filterName))
            {
                return f;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Filter> getFilters()
    {
        if (filters == null)
        {
            filters = CollectionUtils.createList();
        }
        return filters;
    }

    public Filter addFilter(String filterName)
    {
        if (getFilter(filterName) != null)
        {
            throw new IllegalArgumentException("Filter with name: "+name+" already defined");
        }
        FilterImpl f = new FilterImpl();
        f.setFilterName(filterName);
        getFilters().add(f);
        return f;        
    }

    public FilterMapping getFilterMapping(String filterName)
    {
        for (FilterMapping f : getFilterMappings())
        {
            if (f.getFilterName().equals(filterName))
            {
                return f;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<FilterMapping> getFilterMappings()
    {
        if (filterMappings == null)
        {
            filterMappings = CollectionUtils.createList();
        }
        return filterMappings;
    }

    public FilterMapping addFilterMapping(String filterName)
    {
        if (getFilterMapping(filterName) != null)
        {
            throw new IllegalArgumentException("Filtermapping for filter: "+name+" already defined");
        }
        FilterMappingImpl fm = new FilterMappingImpl();
        fm.setFilterName(filterName);
        getFilterMappings().add(fm);
        return fm;        
    }

    @SuppressWarnings("unchecked")
    public List<Listener> getListeners()
    {
        if (listeners == null)
        {
            listeners = CollectionUtils.createList();
        }
        return listeners;
    }

    public Listener addListener(String listenerClass)
    {
        for (Listener l : getListeners())
        {
            if (l.getListenerClass().equals(listenerClass))
            {
                throw new IllegalArgumentException("Listener of class: "+listenerClass+" already defined");
            }
        }
        ListenerImpl l = new ListenerImpl();
        l.setListenerClass(listenerClass);
        getListeners().add(l);
        return l;        
    }

    public ContainerRuntimeOption getContainerRuntimeOption(String name)
    {
        for (ContainerRuntimeOption cro : getContainerRuntimeOptions())
        {
            if (cro.getName().equals(name))
            {
                return cro;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<ContainerRuntimeOption> getContainerRuntimeOptions()
    {
        if (containerRuntimeOptions == null)
        {
            containerRuntimeOptions = CollectionUtils.createList();
        }
        return containerRuntimeOptions;
    }

    public ContainerRuntimeOption addContainerRuntimeOption(String name)
    {
        if (getContainerRuntimeOption(name) != null)
        {
            throw new IllegalArgumentException("Container runtime option with name: "+name+" already defined");
        }
        ContainerRuntimeOptionImpl cro = new ContainerRuntimeOptionImpl(this, name);
        getContainerRuntimeOptions().add(cro);
        return cro;
    }
    
    public Map<Locale, String> getLocaleEncodingMappings()
    {
        if (localeEncodingMappings == null)
        {
            if (localeEncodingMappingList == null)
            {
                localeEncodingMappings = Collections.emptyMap();
            }
            else
            {
                Map<Locale, String> map = new HashMap<Locale,String>();
                for (LocaleEncodingMapping lem : localeEncodingMappingList)
                {
                    map.put(lem.getLocale(), lem.getEncoding());
                }
                localeEncodingMappings = Collections.unmodifiableMap(map);
            }
        }
        return localeEncodingMappings;
    }
    
    @SuppressWarnings("unchecked")
    public void addLocaleEncodingMapping(Locale locale, String encoding)
    {
        if (localeEncodingMappingList == null)
        {
            localeEncodingMappingList = CollectionUtils.createList();
        }
        localeEncodingMappingList.add(new LocaleEncodingMappingImpl(locale, encoding));
    }
    
    @SuppressWarnings("unchecked")
    public List<JetspeedServiceReference> getJetspeedServices()
    {
        if (services == null)
        {
            this.services = CollectionUtils.createList();
        }
        return services;
    }
    
    public void addJetspeedServiceReference(String name)
    {
        for (JetspeedServiceReference ref : getJetspeedServices())
        {
            if (ref.getName().equals(name))
            {
                throw new IllegalArgumentException("Jetspeed service: "+name+" already defined");
            }
        }
        JetspeedServiceReferenceImpl ref = new JetspeedServiceReferenceImpl();
        ref.setName(name);
        getJetspeedServices().add(ref);
    }

    public boolean isLayoutApplication()
    {
        if (this.getMetadata() != null)
        {
            Collection<LocalizedField> c = this.getMetadata().getFields("layout-app");
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.Support#postLoad(java.lang.Object)
     */
    public void postLoad(Object parameter) throws Exception
    {
        for (PortletDefinition pd : portlets)
        {
            ((Support)pd).postLoad(this);
        }
        for (PortletDefinition clone : clones)
        {
            ((Support)clone).postLoad(this);
        }

    }

    /// PersistenceBrokerAware interface implementation
    public void afterDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void afterInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void afterLookup(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void afterUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        revision++;
    }

    public void beforeUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        revision++;
    }

    public String getLocalContextPath()
    {
        return localContextPath;
    }

    public void setLocalContextPath(String localContextPath)
    {
        this.localContextPath = localContextPath;
    }
}
