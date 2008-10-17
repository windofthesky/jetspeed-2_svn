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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.portlet.PreferencesValidator;

import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDisplayName;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.common.preference.PreferencesValidatorFactory;
import org.apache.jetspeed.om.impl.DescriptionImpl;
import org.apache.jetspeed.om.impl.DescriptionSetImpl;
import org.apache.jetspeed.om.impl.DisplayNameSetImpl;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.impl.ParameterSetImpl;
import org.apache.jetspeed.om.impl.PortletDescriptionImpl;
import org.apache.jetspeed.om.impl.PortletDisplayNameImpl;
import org.apache.jetspeed.om.impl.PortletParameterSetImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefSetImpl;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.JetspeedLongObjectID;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.pluto.om.ElementFactoryList;
import org.apache.pluto.om.portlet.Parameter;
import org.apache.pluto.om.portlet.ParameterSet;
import org.apache.pluto.om.portlet.Preference;
import org.apache.pluto.om.portlet.PreferenceSet;
import org.apache.pluto.om.portlet.Description;
import org.apache.pluto.om.portlet.DescriptionSet;
import org.apache.pluto.om.portlet.DisplayName;
import org.apache.pluto.om.portlet.DisplayNameSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.SecurityRoleRef;
import org.apache.pluto.om.portlet.SecurityRoleRefSet;
import org.apache.pluto.om.portlet.Supports;
import org.apache.pluto.om.servlet.ServletDefinition;

/**
 * 
 * PortletDefinitionImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class PortletDefinitionImpl implements PortletDefinition, PreferencesValidatorFactory, Serializable, Support, PersistenceBrokerAware
{
    private static PortletRegistry registry;
    private static PortletFactory  portletFactory;
    private static PortletPreferencesProvider portletPreferencesProvider;
    
    private Long id;
    private JetspeedLongObjectID oid;
    private String className;
    private String name;
    private String portletIdentifier;
    private List<Language> languages = null;
    private Collection parameterSet;
    private ParameterSetImpl paramListWrapper = new PortletParameterSetImpl();
    private Collection securityRoleRefSet;
    private SecurityRoleRefSetImpl secListWrapper = new SecurityRoleRefSetImpl();
    /** User attribute set. * */
    //private Collection userAttributeSet;
    /** User attribute ref set. * */
    //private Collection userAttributeRefSet;
    private String preferenceValidatorClassname;
    private Collection displayNames;
    private DisplayNameSetImpl DNListWrapper = new DisplayNameSetImpl();
    private Collection descriptions;
    private DescriptionSetImpl descListWrapper = new DescriptionSetImpl(DescriptionImpl.TYPE_PORTLET);
    private String resourceBundle;
    private ArrayList supportedLocales;

    private List<Supports> supports;
    protected List portletEntities;

    /** PortletApplicationDefinition this PortletDefinition belongs to */
    private PortletApplication app;

    protected long appId;
    private String expirationCache;

    /** Metadata property */
    private Collection metadataFields = null;
    private PreferenceSetComposite preferenceSet;

    private String jetspeedSecurityConstraint = null;
    
    public PortletDefinitionImpl()
    {
        super();
        try
        {
            parameterSet = new ArrayList();
            securityRoleRefSet = new ArrayList();
            //userAttributeSet = new ArrayList();
            //userAttributeRefSet = new ArrayList();
            contentTypes = new ArrayList();
            supportedLocales= new ArrayList();
        }
        catch (RuntimeException e)
        {
//            System.out.println("Failed to fully construct Portlet Definition");
            e.printStackTrace();
        }
    }

    protected ResourceBundle loadResourceBundle( Locale locale )
    {
        ResourceBundle resourceBundle = null;
        try
        {
            if (getResourceBundle() != null)
            {
                if (getPortletClassLoader() != null)
                {
                    resourceBundle = ResourceBundle.getBundle(getResourceBundle(), locale, getPortletClassLoader());
                }
                else
                {
                    resourceBundle = ResourceBundle.getBundle(getResourceBundle(), locale, Thread.currentThread()
                            .getContextClassLoader());
                }
            }
        }
        catch (MissingResourceException x)
        {
            return null;
        }
        return resourceBundle;
    }
    
    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletClass()
     */
    public String getPortletClass()
    {
        return className;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletName()
     */
    public String getPortletName()
    {
        return name;
    }
    
    public Language getLanguage(Locale locale)
    {
        LanguageImpl fallback = null;
        // ensure languages list is available;
        getLanguages();
        synchronized (languages)
        {
            for (Language l : languages)
            {
                LanguageImpl lang = (LanguageImpl)l;
                if (lang.getLocale().equals(locale))
                {
                    if (getResourceBundle() != null && lang.getParentResourceBundle() == null)
                    {
                        lang.setResourceBundle(loadResourceBundle(lang.getLocale()));
                    }
                    return lang;
                }
                else if (lang.getLocale().getLanguage().equals(locale.getLanguage()))
                {
                    fallback = lang;
                }
            }
            if ( fallback == null )
            {
                if ( JetspeedLocale.getDefaultLocale().equals(locale) )
                {
                    // no default language stored yet
                    LanguageImpl defaultLanguage = new LanguageImpl();
                    defaultLanguage.setLocale(locale);
                    
                    if ( getResourceBundle() != null )
                    {
                        defaultLanguage.setResourceBundle(loadResourceBundle(locale));
                        defaultLanguage.loadDefaults();
                        languages.add(defaultLanguage);
                        return defaultLanguage;
                    }
                }
                else
                {
                    return getLanguage(JetspeedLocale.getDefaultLocale());
                }
            }
            
            LanguageImpl language = new LanguageImpl();
            language.setLocale(locale);
            language.setTitle(fallback.getTitle());
            language.setShortTitle(fallback.getShortTitle());
            language.setKeywords(fallback.getKeywords());
            if ( getResourceBundle() != null )
            {
              language.setResourceBundle(loadResourceBundle(locale));
            }
            language.loadDefaults();
            languages.add(language);
            return language;
        }
    }
    
    public List<Language> getLanguages()
    {
        if ( languages == null )
        {
            languages = new ArrayList<Language>();
        }
        return Collections.unmodifiableList(languages);
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addLanguage(java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
     */
    public void addLanguage(String title, String shortTitle, String keywords, Locale locale)
    {
        // ensure list is available        
        getLanguages();
        synchronized(languages)
        {
            if (locale == null)
            {
                locale = JetspeedLocale.getDefaultLocale();
            }
            LanguageImpl lang = (LanguageImpl)getLanguage(locale);
            boolean newLang = lang == null;
            if (newLang)
            {
                lang = new LanguageImpl();
                lang.setLocale(locale);
            }
            lang.setTitle(title);
            lang.setShortTitle(shortTitle);
            lang.setKeywords(keywords);
            if (newLang)
            {
                if ( getResourceBundle() != null )
                {
                    lang.setResourceBundle(loadResourceBundle(locale));
                    lang.loadDefaults();
                    languages.add(lang);
                }
            }
        }
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getInitParameterSet()
     */
    public ParameterSet getInitParameterSet()
    {
        paramListWrapper.setInnerCollection(parameterSet);
        return paramListWrapper;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getInitSecurityRoleRefSet()
     */
    public SecurityRoleRefSet getInitSecurityRoleRefSet()
    {
        secListWrapper.setInnerCollection(securityRoleRefSet);
        return secListWrapper;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPreferenceSet()
     */
    public PreferenceSet getPreferenceSet()
    {
        if (preferenceSet == null)
        {
           
            if(app == null)
            {
                throw new IllegalStateException("Portlet Application must be defined before preferences can be accessed");
            }
            preferenceSet = portletPreferencesProvider.getPreferenceSet(this);
        }
        return preferenceSet;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinition#setPreferenceSet(org.apache.pluto.om.common.PreferenceSet)
     */
    public void setPreferenceSet( PreferenceSet preferences )
    {
        this.preferenceSet = (PreferenceSetComposite) preferences;
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
    
    public ElementFactoryList<Supports> getSupports()
    {
        if (supports == null || !(supports instanceof ElementFactoryList))
        {
            ElementFactoryList<Supports> lf = 
                new ElementFactoryList<Supports>( new ElementFactoryList.Factory<Supports>()
                {
                    public Class<? extends Supports> getElementClass()
                    {
                        return SupportsImpl.class;
                    }

                    public Supports newElement()
                    {
                        return new SupportsImpl();
                    }
                }); 
            if (supports != null)
            {
                lf.addAll(supports);
            }
            supports = lf;
        }
        return (ElementFactoryList<Supports>)supports;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletApplicationDefinition()
     */
    public PortletApplicationDefinition getPortletApplicationDefinition()
    {
        return app;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getServletDefinition()
     */
    public ServletDefinition getServletDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getExpirationCache()
     */
    public String getExpirationCache()
    {
        return expirationCache;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletClassLoader()
     */
    public ClassLoader getPortletClassLoader()
    {
        return portletFactory.getPortletApplicationClassLoader(app);
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#setId(java.lang.String)
     */
    public void setId( String oid )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#setPortletClass(java.lang.String)
     */
    public void setPortletClass( String className )
    {
        this.className = className;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#setPortletName(java.lang.String)
     */
    public void setPortletName( String name )
    {
        this.name = name;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#setPortletClassLoader(java.lang.ClassLoader)
     */
    public void setPortletClassLoader( ClassLoader loader )
    {
      // no-op: ClassLoader is only stored in the PortletFactory
      ;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinition#setInitParameterSet(org.apache.pluto.om.common.ParameterSet)
     */
    public void setInitParameterSet( ParameterSet parameters )
    {
        this.parameterSet = ((ParameterSetImpl) parameters).getInnerCollection();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinition#setInitSecurityRoleRefSet(org.apache.pluto.om.common.SecurityRoleRefSet)
     */
    public void setInitSecurityRoleRefSet( SecurityRoleRefSet securityRefs )
    {
        this.securityRoleRefSet = ((SecurityRoleRefSetImpl) securityRefs).getInnerCollection();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addUserAttribute(org.apache.jetspeed.om.common.UserAttribute)
     */
    /*
     * public void addUserAttribute(UserAttribute userAttribute) {
     * this.userAttributeSet.add(userAttribute); }
     *  
     *//**
        * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setUserAttributeSet(java.util.Collection)
        */
    /*
     * public void setUserAttributeSet(Collection userAttributeSet) {
     * this.userAttributeSet = userAttributeSet; }
     *  
     *//**
        * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getUserAttributeSet()
        */
    /*
     * public Collection getUserAttributeSet() { return this.userAttributeSet; }
     *  
     *//**
        * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addUserAttributeRef(org.apache.jetspeed.om.common.UserAttributeRef)
        */
    /*
     * public void addUserAttributeRef(UserAttributeRef userAttributeRef) {
     * System.out.println("_______IN addUserAttributeRef");
     * this.userAttributeRefSet.add(userAttributeRef); }
     *  
     *//**
        * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setUserAttributeSet(java.util.Collection)
        */
    /*
     * public void setUserAttributeRefSet(Collection userAttributeRefSet) {
     * this.userAttributeRefSet = userAttributeRefSet; }
     *  
     *//**
        * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getUserAttributeRefSet()
        */
    /*
     * public Collection getUserAttributeRefSet() { return
     * this.userAttributeRefSet; }
     */
    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinition#setInitParameter(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public ParameterComposite addInitParameter( String name, String value, DescriptionSet description )
    {
        ParameterComposite pc = addInitParameter(name, value);
        pc.setDescriptionSet(description);
        return pc;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addInitParameter(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Locale)
     */
    public ParameterComposite addInitParameter( String name, String value, String description, Locale locale )
    {
        ParameterComposite param = addInitParameter(name, value);
        param.addDescription(locale, description);
        return param;
    }

    public void addInitParameter( Parameter parameter )
    {
        parameterSet.add(parameter);
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#setInitParameter(java.lang.String,
     *      java.lang.String)
     */
    public ParameterComposite addInitParameter( String name, String value )
    {
        paramListWrapper.setInnerCollection(parameterSet);
        return (ParameterComposite) paramListWrapper.add(name, value);
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#setExpirationCache(java.lang.String)
     */
    public void setExpirationCache( String cache )
    {
        expirationCache = cache;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addPreference(java.lang.String,
     *      java.util.Collection)
     */
    public PreferenceComposite addPreference( String name, String[] values )
    {
        return (PreferenceComposite) ((PreferenceSetComposite) getPreferenceSet()).add(name, Arrays.asList(values));
    }

    public void setPortletIdentifier( String portletIdentifier )
    {
        this.portletIdentifier = portletIdentifier;
    }

    public String getPortletIdentifier()
    {
        return this.portletIdentifier;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#setPortletApplicationDefinition(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void setPortletApplicationDefinition( PortletApplicationDefinition pad )
    {
        app = (PortletApplication) pad;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            PortletDefinitionImpl pd = (PortletDefinitionImpl) obj;
            boolean sameId = (id != null && pd.id != null && id.equals(pd.id));
            if (sameId)
            {
                return true;
            }
            boolean sameAppId = (appId == pd.appId);
            boolean sameName = (pd.getPortletName() != null && name != null && pd.getPortletName().equals(name));
            return sameName && sameAppId;
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(1, 3);
        hasher.append(name);
        if (app != null)
        {
            if ( getId() != null )
            {
              hasher.append(getId().toString());
            }
            hasher.append(app.getName());
        }
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#getUniqueName()
     */
    public String getUniqueName()
    {
        if (app != null && name != null)
        {
            return app.getName() + "::" + name;
        }
        else
        {
            throw new IllegalStateException(
                    "Cannot generate a unique portlet name until the application and portlet name have been set");
        }
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getDescription(java.util.Locale)
     */
    public Description getDescription( Locale arg0 )
    {
        if (descriptions != null)
        {
            descListWrapper.setInnerCollection(descriptions);
            return descListWrapper.get(arg0);
        }
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getDisplayName(java.util.Locale)
     */
    public DisplayName getDisplayName( Locale arg0 )
    {
        if (displayNames != null)
        {
            DNListWrapper.setInnerCollection(displayNames);
            return DNListWrapper.get(arg0);
        }
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#setDescriptions(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptions( DescriptionSet arg0 )
    {
        this.descriptions = ((DescriptionSetImpl) arg0).getInnerCollection();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#setDisplayNames(org.apache.pluto.om.common.DisplayNameSet)
     */
    public void setDisplayNames( DisplayNameSet arg0 )
    {
        this.displayNames = ((DisplayNameSetImpl) arg0).getInnerCollection();
    }

    /**
     * Returns localized text of this PortletDefinitions display name.
     * 
     * @param locale
     *            Locale to get the display name for
     * @return Localized text string of the display name or <code>null</code>
     *         if no DisplayName exists for this locale
     */
    public String getDisplayNameText( Locale locale )
    {
        DisplayName dn = getDisplayName(locale);
        if (dn != null)
        {
            return dn.getDisplayName();
        }
        return null;
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
        if (desc != null)
        {
            return desc.getDescription();
        }
        return null;
    }
    
    public DescriptionSet getDescriptionSet()
    {
        return this.descListWrapper;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addDescription(java.util.Locale,
     *      java.lang.String)
     */
    public void addDescription( Locale locale, String description )
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList();
        }
        descListWrapper.setInnerCollection(descriptions);
        MutableDescription descObj = new PortletDescriptionImpl();
        descObj.setLocale(locale);
        descObj.setDescription(description);
        descListWrapper.addDescription(descObj);
    }

    public void addDescription( Description description )
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList();
        }
        descListWrapper.setInnerCollection(descriptions);
        descListWrapper.addDescription(description);
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addDisplayName(java.util.Locale,
     *      java.lang.String)
     */
    public void addDisplayName( Locale locale, String displayName )
    {
        if (displayNames == null)
        {
            displayNames = new ArrayList();
        }
        DNListWrapper.setInnerCollection(displayNames);
        MutableDisplayName dn = new PortletDisplayNameImpl();
        dn.setLocale(locale);
        dn.setDisplayName(displayName);
        DNListWrapper.addDisplayName(dn);
    }

    public void addDisplayName( DisplayName displayName )
    {
        if (displayNames == null)
        {
            displayNames = new ArrayList();
        }
        DNListWrapper.setInnerCollection(displayNames);
        DNListWrapper.addDisplayName(displayName);
    }
    
    public DisplayNameSet getDisplayNameSet()
    {
        if ( displayNames != null )
        {
            DNListWrapper.setInnerCollection(displayNames);
        }
        return DNListWrapper;
    }
    
    /**
     * <p>
     * store will attempt to perform an atomic persistence call against this
     * portletDefinition.
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#store()
     * @throws java.io.IOException
     */
    public void store() throws IOException
    {
        try
        {
            registry.savePortletDefinition(this);
        }
        catch (RegistryException e)
        {
            IOException ioe = new IOException("Failed to store portlet definition: "+e.getMessage());
            ioe.initCause(e);
        }
    }

    public void storeChildren()
    {
        if (preferenceSet != null)
        {
            portletPreferencesProvider.savePreferenceSet(this, preferenceSet);
        }
    }

    /**
     * <p>
     * getPreferenceValidatorClassname
     * </p>
     * 
     * @return
     */
    public String getPreferenceValidatorClassname()
    {
        return preferenceValidatorClassname;
    }

    /**
     * <p>
     * setPreferenceValidatorClassname
     * </p>
     * 
     * @param string
     *  
     */
    public void setPreferenceValidatorClassname( String string )
    {
        preferenceValidatorClassname = string;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addPreference(org.apache.pluto.om.common.Preference)
     * @param preference
     */
    public void addPreference( Preference preference )
    {
        Iterator valueItr = preference.getValues();
        ArrayList list = new ArrayList();
        while (valueItr.hasNext())
        {
            list.add(valueItr.next());
        }

        PreferenceComposite newPref = (PreferenceComposite) ((PreferenceSetComposite) getPreferenceSet()).add(
                preference.getName(), list);
        newPref.setReadOnly(Boolean.toString(preference.isReadOnly()));

        // TODO: remove? (not really used/implemented in Jetspeed)
        Iterator descItr = newPref.getDescriptions();
        while (descItr.hasNext())
        {
            Description desc = (Description) descItr.next();
            newPref.addDescription(desc.getLocale(), desc.getDescription());
        }

    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addSecurityRoleRef(org.apache.pluto.om.portlet.SecurityRoleRef)
     */
    public void addSecurityRoleRef( SecurityRoleRef securityRef )
    {
        secListWrapper.setInnerCollection(securityRoleRefSet);
        secListWrapper.add(securityRef);
    }
    
    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#addSecurityRoleRef(java.lang.String, java.lang.String)
     */
    public SecurityRoleRef addSecurityRoleRef(String roleName, String roleLink)
    {
        SecurityRoleRefImpl ref = new SecurityRoleRefImpl();
        ref.setRoleName(roleName);
        ref.setRoleLink(roleLink);
        
        addSecurityRoleRef(ref);
        
        return ref;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        if (metadataFields == null)
        {
            metadataFields = new ArrayList();
        }

        GenericMetadata metadata = new PortletDefinitionMetadataImpl();
        metadata.setFields(metadataFields);

        return metadata;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#setMetadata(org.apache.jetspeed.om.common.GenericMetadata)
     */
    public void setMetadata( GenericMetadata metadata )
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
    protected void setMetadataFields( Collection metadataFields )
    {
        this.metadataFields = metadataFields;
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
    
    public Collection getSupportedLocales()
    {
        return supportedLocales;
    }

    public void addSupportedLocale(String locale)
    {
        // parse locale String
        StringTokenizer tokenizer = new StringTokenizer(locale, "_");
        String[] localeDef = new String[3];
        for (int i = 0; i < 3; i++)
        {
            if (tokenizer.hasMoreTokens())
            {
                localeDef[i] = tokenizer.nextToken();
            }
            else
            {
                localeDef[i] = "";
            }
        }
        supportedLocales.add(new Locale(localeDef[0], localeDef[1], localeDef[2]));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.Support#postLoad(java.lang.Object)
     */
    public void postLoad(Object parameter) throws Exception
    {
    }
    
    public PreferencesValidator getPreferencesValidator()
    {
        return portletFactory.getPreferencesValidator(this);
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
    

    public static void setPortletRegistry(PortletRegistry registry)
    {
        PortletDefinitionImpl.registry = registry;
    }

    public static void setPortletFactory(PortletFactory portletFactory)
    {
        PortletDefinitionImpl.portletFactory = portletFactory;
    }

    public static void setPortletPreferencesProvider(PortletPreferencesProvider portletPreferencesProvider)
    {
        PortletDefinitionImpl.portletPreferencesProvider = portletPreferencesProvider;
    }
}
