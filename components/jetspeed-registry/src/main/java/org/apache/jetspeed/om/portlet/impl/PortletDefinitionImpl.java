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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.EventDefinitionReference;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.PortletInfo;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.SecurityRoleRef;
import org.apache.jetspeed.om.portlet.SupportedPublicRenderParameter;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.pluto.container.PortletPreference;

/**
 * 
 * PortletDefinitionImpl
 * 
 * @version $Id$
 *  
 */
public class PortletDefinitionImpl implements PortletDefinition, Serializable, Support, PersistenceBrokerAware
{
    private static final long serialVersionUID = 1L;
    private static PortletPreferencesProvider portletPreferencesProvider;

    private PortletApplication app;
    
    private Long id;

    protected String portletName;
    protected String portletClass;
    protected String resourceBundle;
    protected String preferenceValidatorClassname;
    private Integer expirationCache;
    private String cacheScope;
    private String cloneParent = null;
    
    /** Metadata property */    
    private Collection<LocalizedField> metadataFields = null;

    private String jetspeedSecurityConstraint;
    
    private List<Description> descriptions;
    private List<DisplayName> displayNames;
    private List<InitParam> initParams;
    private List<EventDefinitionReference> supportedProcessingEvents;
    private List<EventDefinitionReference> supportedPublishingEvents;
    private List<SecurityRoleRef> securityRoleRefs;
    private List<Supports> supports;
    private List<Language> languages;
    private List<ContainerRuntimeOption> containerRuntimeOptions;    
    private List<SupportedPublicRenderParameter> supportedPublicRenderParameters;
    private Preferences descriptorPreferences = new PreferencesImpl();    
    
    public static void setPortletPreferencesProvider(PortletPreferencesProvider portletPreferencesProvider)
    {
        PortletDefinitionImpl.portletPreferencesProvider = portletPreferencesProvider;
    }

    public PortletDefinitionImpl()
    {
    }

    public PortletDefinitionImpl(String cloneParent)
    {
        this.cloneParent = cloneParent;
    }

    public PortletApplication getApplication()
    {
        return app;
    }
    
    public void setApplication(PortletApplication app)
    {
        this.app = app;
    }

    public String getPortletName()
    {
        return portletName;
    }
    
    public void setPortletName( String name )
    {
        this.portletName = name;
    }

    public String getPortletClass()
    {
        return portletClass;
    }

    public void setPortletClass(String portletClass)
    {
        this.portletClass = portletClass;
    }

    public void setDescriptorPreferences(Preferences descriptorPreferences)
    {
        this.descriptorPreferences = descriptorPreferences;
    }
    
    public Preferences getDescriptorPreferences()
    {
        return this.descriptorPreferences;
    }

    public Preferences getPortletPreferences()
    {
        if (PortletDefinitionImpl.portletPreferencesProvider == null)
        {
            return new PreferencesImpl();            
        }
        Map<String, PortletPreference> prefMap = PortletDefinitionImpl.portletPreferencesProvider.retrieveDefaultPreferences(this);        
        Preferences preferences = new PreferencesImpl();
        List<Preference> list = preferences.getPortletPreferences();
        for (PortletPreference pref : prefMap.values())
        {
            Preference p = preferences.addPreference(pref.getName());
            p.setReadOnly(pref.isReadOnly());
            String [] values = pref.getValues();
            if (values != null) 
            {
                for (String s : values)
                {
                    p.addValue(s);
                }
            }
        }
        return preferences;
    }

    public Preference addDescriptorPreference(String name)
    {
        return descriptorPreferences.addPreference(name);
    }       
    
    public Language getLanguage(Locale locale)
    {
        Language lang = null;
        Language fallback = null;
        Language defaultLanguage = null;
        boolean defaultLocale = locale.equals(JetspeedLocale.getDefaultLocale());
        
        for (Language l : getLanguages())
        {
            if (locale.equals(l.getLocale()))
            {
                lang = l;
                break;
            }
            if (locale.getLanguage().equals(l.getLocale().getLanguage()))
            {
                fallback = l;
            }            
            if (defaultLanguage == null && !defaultLocale && l.getLocale().equals(JetspeedLocale.getDefaultLocale()))
            {
                defaultLanguage = l;
            }
        }        
        if (lang == null)
        {            
            LanguageImpl l = new LanguageImpl();
            l.setLocale(locale);
            if (fallback == null && defaultLanguage != null)
            {
                fallback = defaultLanguage;
            }
            if (fallback != null)
            {
                l.setTitle(fallback.getTitle());
                l.setShortTitle(fallback.getShortTitle());
                l.setKeywords(fallback.getKeywords());
            }
            lang = l;
        }
        return lang;
    }
    
    @SuppressWarnings("unchecked")
    public List<Language> getLanguages()
    {
        if ( languages == null )
        {
            languages = CollectionUtils.createList();
        }
        return languages;
    }
    
    @SuppressWarnings("unchecked")
    public Language addLanguage(Locale locale)
    {
        // ensure languages exist
        if ( languages == null )
        {
            languages = CollectionUtils.createList();
        }
        
        for (Language l : languages)
        {
            if (locale.equals(l.getLocale()))
            {
                return l;
            }
        }
        LanguageImpl l = new LanguageImpl();
        l.setLocale(locale);
        languages.add(l);
        return l;
    }
    
    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#getUniqueName()
     */
    public String getUniqueName()
    {
        if (app != null && app.getName() != null && portletName != null)
        {
            return app.getName() + "::" + portletName;
        }
        else
        {
            throw new IllegalStateException(
                    "Cannot generate a unique portlet name until the application and portlet name have been set");
        }
    }

    /**
     * Returns localized text of this PortletDefinitions description.
     * 
     * @param locale
     *            Locale to get the description for
     * @return Localized text string of the display name or <code>null</code>
     *         if no Description exists for this locale
     */
    public String getDescriptionText( Locale locale )
    {
        Description desc = getDescription(locale);
        return desc != null ? desc.getDescription() : null;
    }
    
    public String getDisplayNameText(Locale locale)
    {
        DisplayName dn = getDisplayName(locale);
        return dn != null ? dn.getDisplayName() : null;
    }

    public String getPreferenceValidatorClassname()
    {
        return preferenceValidatorClassname;
    }

    public void setPreferenceValidatorClassname( String string )
    {
        preferenceValidatorClassname = string;
    }

    @SuppressWarnings("unchecked")
    public GenericMetadata getMetadata()
    {
        if (metadataFields == null)
        {
            metadataFields = CollectionUtils.createCollection();
        }

        GenericMetadata metadata = new PortletDefinitionMetadataImpl();
        metadata.setFields(metadataFields);

        return metadata;
    }

    /**
     * @return
     */
    public String getResourceBundle()
    {
        return resourceBundle;
    }

    /**
     * @param string
     */
    public void setResourceBundle(String string)
    {
        resourceBundle = string;
    }
    
    public String getJetspeedSecurityConstraint()
    {
        return this.jetspeedSecurityConstraint;
    }

    public void setJetspeedSecurityConstraint(String constraint)
    {
        this.jetspeedSecurityConstraint = constraint;
    }
    
    public boolean isSameIdentity(PortletDefinition other)
    {
        Long otherId = null;
        if (other != null && other instanceof PortletDefinitionImpl)
        {
            otherId = ((PortletDefinitionImpl)other).id;
        }
        return id != null && otherId != null && id.equals(otherId);
    }

    public boolean equals( Object obj )
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            PortletDefinitionImpl pd = (PortletDefinitionImpl) obj;
            boolean sameId = id != null && pd.id != null && id.equals(pd.id);
            if (sameId)
            {
                return true;
            }
            String otherAppName = pd.getApplication() != null ? pd.getApplication().getName() : null;
            boolean sameAppName = (app != null && app.getName() != null && otherAppName != null && app.getName().equals(otherAppName));
            return sameAppName && (pd.getPortletName() != null && portletName != null && pd.getPortletName().equals(portletName));
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(1, 3);
        hasher.append(portletName);
        if ( id != null )
        {
          hasher.append(id.toString());
        }
        if (app != null)
        {
            hasher.append(app.getName());
        }
        return hasher.toHashCode();
    }

    public void storeChildren()
    {
// TODO        
//        if (preferenceSet != null)
//        {
//            portletPreferencesProvider.savePreferenceSet(this, preferenceSet);
//        }
    }

    public void postLoad(Object parameter) throws Exception
    {
    }
    
    //
    /// PersistenceBrokerAware interface implementation
    public void afterDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void afterInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        storeChildren();
    }

    public void afterLookup(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void afterUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        storeChildren();
    }

    public void beforeDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
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
        containerRuntimeOptions.add(cro);
        return cro;        
    }

    public SecurityRoleRef getSecurityRoleRef(String roleName)
    {
        for (SecurityRoleRef ref : getSecurityRoleRefs())
        {
            if (ref.getRoleName().equals(roleName))
            {
                return ref;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<SecurityRoleRef> getSecurityRoleRefs()
    {
        if (securityRoleRefs == null)
        {
            securityRoleRefs = CollectionUtils.createList();
        }
        return securityRoleRefs;
    }
    
    public SecurityRoleRef addSecurityRoleRef(String roleName)
    {
        if (getSecurityRoleRef(roleName) != null)
        {
            throw new IllegalArgumentException("Security role reference for role: "+roleName+" already defined");
        }
        SecurityRoleRefImpl srr = new SecurityRoleRefImpl();
        srr.setRoleName(roleName);
        securityRoleRefs.add(srr);
        return srr;        
    }
    
    public PortletInfo getPortletInfo()
    {
        return getLanguage(JetspeedLocale.getDefaultLocale());
    }

    public Supports getSupports(String mimeType)
    {
        for (Supports s : getSupports())
        {
            if (s.getMimeType().equals(mimeType))
            {
                return s;
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<Supports> getSupports()
    {
        if (supports == null)
        {
            supports = CollectionUtils.createList();
        }
        return supports;
    }
    
    public Supports addSupports(String mimeType)
    {
        if (getSupports(mimeType) != null)
        {
            throw new IllegalArgumentException("Supports for mime type: "+mimeType+" already defined");
        }
        SupportsImpl s = new SupportsImpl();
        s.setMimeType(mimeType);
        supports.add(s);
        return s;        
    }
    
    public List<String> getSupportedLocales()
    {
        List<Language> languages = getLanguages();
        List<String> locales = new ArrayList<String>();
        for (Language l : languages)
        {
            if (l.isSupportedLocale())
            {
                locales.add(l.getLocale().toString());
            }
        }
        return locales;
    }
    
    public void addSupportedLocale(String lang)
    {
        // noop: use addLanguage(Locale) instead
    }

    @SuppressWarnings("unchecked")
    public List<String> getSupportedPublicRenderParameters()
    {
        if (supportedPublicRenderParameters == null)
        {
            supportedPublicRenderParameters = CollectionUtils.createList();
        }
        List<String> params = new ArrayList<String>();
        for (SupportedPublicRenderParameter param : this.supportedPublicRenderParameters)
        {
            params.add(param.toString());
        }
        return params;
    }
    
    @SuppressWarnings("unchecked")
    public void addSupportedPublicRenderParameter(String identifier)
    {
        if (supportedPublicRenderParameters == null)
        {
            supportedPublicRenderParameters = CollectionUtils.createList();
        }
        for (SupportedPublicRenderParameter param : this.supportedPublicRenderParameters)
        {
            if (param.equals(identifier))
            {
                throw new IllegalArgumentException("Support for public render parameter with identifier: "+identifier+" already defined");
            }
        }
        supportedPublicRenderParameters.add(new SupportedPublicRenderParameterImpl(identifier));        
    }


    /**
     * Caching scope, allowed values are "private" indicating that the content should not be shared across users and
     * "public" indicating that the content may be shared across users. The default value if not present is "private".
     */
    public String getCacheScope()
    {
        return cacheScope != null ? cacheScope : "private";
    }

    public void setCacheScope(String cacheScope)
    {
        this.cacheScope = cacheScope;
    }

    public int getExpirationCache()
    {
        return expirationCache != null ? expirationCache.intValue() : 0;
    }

    public void setExpirationCache(int value)
    {
        expirationCache = new Integer(value);
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
        getDescriptions();
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

    public InitParam getInitParam(String name)
    {
        for (InitParam param : getInitParams())
        {
            if (param.getParamName().equals(name))
            {
                return param;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<InitParam> getInitParams()
    {
        if (initParams == null)
        {
            initParams = CollectionUtils.createList();
        }
        return initParams;
    }
    
    public InitParam addInitParam(String paramName)
    {
        if (getInitParam(paramName) != null)
        {
            throw new IllegalArgumentException("Init parameter: "+paramName+" already defined");
        }
        InitParamImpl param = new InitParamImpl(this, paramName);
        getInitParams();
        initParams.add(param);
        return param;
    }
    
    @SuppressWarnings("unchecked")
    public List<EventDefinitionReference> getSupportedProcessingEvents()
    {
        if (supportedProcessingEvents == null)
        {
            supportedProcessingEvents = CollectionUtils.createList();            
        }
        return supportedProcessingEvents;
    }

    public EventDefinitionReference addSupportedProcessingEvent(QName qname)
    {
        List<EventDefinitionReference> refs = getSupportedProcessingEvents();
        for (EventDefinitionReference ref : refs)
        {
            if (ref.getQName().equals(qname))
            {
                return ref;
            }
        }
        ProcessingEventReferenceImpl edr = new ProcessingEventReferenceImpl(qname);
        supportedProcessingEvents.add(edr);
        return edr;
    }
    
    public EventDefinitionReference addSupportedProcessingEvent(String name)
    {
        QName qname = new QName(getApplication().getDefaultNamespace(), name);
        return this.addSupportedProcessingEvent(qname);
    }
         
    @SuppressWarnings("unchecked")
    public List<EventDefinitionReference> getSupportedPublishingEvents()
    {
        if (supportedPublishingEvents == null)
        {
            supportedPublishingEvents = CollectionUtils.createList();            
        }
        return supportedPublishingEvents;
    }    

    public EventDefinitionReference addSupportedPublishingEvent(QName qname)
    {
        List<EventDefinitionReference> refs = getSupportedPublishingEvents();
        for (EventDefinitionReference ref : refs)
        {
            if (ref.getQName().equals(qname))
            {
                return ref;
            }
        }
        EventDefinitionReferenceImpl edr = new PublishingEventReferenceImpl(qname);
        supportedPublishingEvents.add(edr);
        return edr;
    }
    
    public EventDefinitionReference addSupportedPublishingEvent(String name)
    {
        QName qname = new QName(getApplication().getDefaultNamespace(), name);
        return this.addSupportedPublishingEvent(qname);
    }

    public boolean isClone()
    {
        return (cloneParent != null);
    }

    public String getCloneParent()
    {
        return this.cloneParent;
    }
}
