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
package org.apache.jetspeed.om.portlet.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDisplayName;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.impl.DescriptionImpl;
import org.apache.jetspeed.om.impl.DescriptionSetImpl;
import org.apache.jetspeed.om.impl.DisplayNameSetImpl;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.impl.LanguageSetImpl;
import org.apache.jetspeed.om.impl.ParameterSetImpl;
import org.apache.jetspeed.om.impl.PortletDescriptionImpl;
import org.apache.jetspeed.om.impl.PortletDisplayNameImpl;
import org.apache.jetspeed.om.impl.PortletParameterSetImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefSetImpl;
import org.apache.jetspeed.om.preference.impl.PrefsPreference;
import org.apache.jetspeed.om.preference.impl.PrefsPreferenceSetImpl;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.servlet.ServletDefinition;
import org.odmg.DList;

/**
 * 
 * PortletDefinitionImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class PortletDefinitionImpl implements PortletDefinitionComposite, Serializable, Support
{
    private static final Log log = LogFactory.getLog(PortletDefinitionImpl.class);
    
    /**
     * This is a static instance of the PortletREgistry that can be used by
     * all instances of the PortletDefinitionImpl to support the 
     * PortletDefintionCtrl.store() method.
     * 
     */
    protected static PortletRegistry registry;
    protected static PortletFactory  portletFactory;
    
    private long id;
    private String className;
    private String name;
    private String portletIdentifier;
    private Collection languageSet = null;
    private LanguageSetImpl langListWrapper = new LanguageSetImpl();
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

    private Collection contentTypes;
    private ContentTypeSetImpl ctListWrapper = new ContentTypeSetImpl();
    protected List portletEntities;

    /** PortletApplicationDefinition this PortletDefinition belongs to */
    private MutablePortletApplication app;

    protected long appId;
    private String expirationCache;

    /** Metadata property */
    private Collection metadataFields = null;
    private PrefsPreferenceSetImpl preferenceSet;

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
            System.out.println("Failed to fully construct Portlet Definition");
            e.printStackTrace();
        }
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getId()
     */
    public ObjectID getId()
    {
        return new JetspeedObjectID(id);
    }

    public long getOID()
    {
        return id;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getClassName()
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getLanguageSet()
     */
    public LanguageSet getLanguageSet()
    {
        if ( languageSet != null )
        {
            langListWrapper.setInnerCollection(languageSet);
        }
        langListWrapper.setClassLoader(getPortletClassLoader());
        
        return langListWrapper;
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
        try
        {
            if (preferenceSet == null)
            {
               
                if(app == null)
                {
                    throw new IllegalStateException("Portlet Application must be defined before preferences can be accessed");
                }
                
                Preferences prefNode = PrefsPreference.createPrefenceNode(this);
                preferenceSet = new PrefsPreferenceSetImpl(prefNode);
            }
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: " + e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }

        return preferenceSet;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPreferenceSet(org.apache.pluto.om.common.PreferenceSet)
     */
    public void setPreferenceSet( PreferenceSet preferences )
    {
        this.preferenceSet = (PrefsPreferenceSetImpl) preferences;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getContentTypeSet()
     */
    public ContentTypeSet getContentTypeSet()
    {
        ctListWrapper.setInnerCollection(contentTypes);
        return ctListWrapper;
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
        if ( portletFactory != null )
        {
            return portletFactory.getPortletApplicationClassLoader(app);
        }
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setId(java.lang.String)
     */
    public void setId( String oid )
    {
        id = JetspeedObjectID.createFromString(oid).longValue();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setClassName(java.lang.String)
     */
    public void setClassName( String className )
    {
        this.className = className;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setName(java.lang.String)
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setPortletClassLoader(java.lang.ClassLoader)
     */
    public void setPortletClassLoader( ClassLoader loader )
    {
      // no-op: ClassLoader is only stored in the PortletFactory
      ;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addLanguage(org.apache.pluto.om.common.Language)
     */
    public void addLanguage( Language lang )
    {
        if (languageSet == null)
        {
            languageSet = new ArrayList();
        }
        langListWrapper.setInnerCollection(languageSet);
        langListWrapper.add(lang);
    }
    
    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addLanguage(java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
     */
    public void addLanguage(String title, String shortTitle, String keywords, Locale locale)
    {
        LanguageImpl lang = new LanguageImpl();
        lang.setTitle(title);
        lang.setShortTitle(shortTitle);
        lang.setKeywords(keywords);
        lang.setLocale(locale);
        
        addLanguage(lang);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setContentTypeSet(org.apache.pluto.om.portlet.ContentTypeSet)
     */
    public void setContentTypeSet( ContentTypeSet contentTypes )
    {
        this.contentTypes = ((ContentTypeSetImpl) contentTypes).getInnerCollection();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitParameterSet(org.apache.pluto.om.common.ParameterSet)
     */
    public void setInitParameterSet( ParameterSet parameters )
    {
        this.parameterSet = ((ParameterSetImpl) parameters).getInnerCollection();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitSecurityRoleRefSet(org.apache.pluto.om.common.SecurityRoleRefSet)
     */
    public void setInitSecurityRoleRefSet( SecurityRoleRefSet securityRefs )
    {
        this.securityRoleRefSet = ((SecurityRoleRefSetImpl) securityRefs).getInnerCollection();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setLanguageSet(org.apache.pluto.om.common.LanguageSet)
     */
    public void setLanguageSet( LanguageSet languages )
    {
        this.languageSet = ((LanguageSetImpl) languages).getInnerCollection();
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
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitParameter(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public ParameterComposite addInitParameter( String name, String value, DescriptionSet description )
    {
        ParameterComposite pc = addInitParameter(name, value);
        pc.setDescriptionSet(description);
        return pc;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addInitParameter(java.lang.String,
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
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitParameter(java.lang.String,
     *      java.lang.String)
     */
    public ParameterComposite addInitParameter( String name, String value )
    {
        paramListWrapper.setInnerCollection(parameterSet);
        return (ParameterComposite) paramListWrapper.add(name, value);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setExpirationCache(java.lang.String)
     */
    public void setExpirationCache( String cache )
    {
        expirationCache = cache;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addContentType(org.apache.pluto.om.portlet.ContentType)
     */
    public void addContentType( ContentType cType )
    {
        ctListWrapper.setInnerCollection(contentTypes);
        ctListWrapper.addContentType(cType);
    }
    
    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addContentType(java.lang.String, java.lang.String[])
     */
    public void addContentType(String contentType, Collection modes)
    {
        ContentTypeImpl ct = new ContentTypeImpl();
        ct.setContentType(contentType);
        ct.setPortletModes(modes);
        
        addContentType(ct);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addPreference(java.lang.String,
     *      java.util.Collection)
     */
    public PreferenceComposite addPreference( String name, String[] values )
    {
        return (PreferenceComposite) ((PrefsPreferenceSetImpl) getPreferenceSet()).add(name, Arrays.asList(values));
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
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPortletApplicationDefinition(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void setPortletApplicationDefinition( PortletApplicationDefinition pad )
    {
        app = (MutablePortletApplication) pad;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            PortletDefinitionImpl pd = (PortletDefinitionImpl) obj;
            boolean sameId = (id != 0 && id == pd.id);
            if (sameId)
            {
                return true;
            }
            boolean sameAppId = (appId == pd.appId);
            boolean sameName = (pd.getName() != null && name != null && pd.getName().equals(name));
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
            hasher.append(getId().toString());
            hasher.append(app.getName());
        }
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getUniqueName()
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
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setDescriptions(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptions( DescriptionSet arg0 )
    {
        this.descriptions = ((DescriptionSetImpl) arg0).getInnerCollection();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setDisplayNames(org.apache.pluto.om.common.DisplayNameSet)
     */
    public void setDisplayNames( DisplayNameSet arg0 )
    {
        this.displayNames = (DList) ((DisplayNameSetImpl) arg0).getInnerCollection();
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
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addDescription(java.util.Locale,
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
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addDisplayName(java.util.Locale,
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
        return DNListWrapper;
    }

    /**
     * <p>
     * store will attempt to perform an atomic persistence call against this
     * portletDefinition.
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#store()
     * @throws java.io.IOException
     */
    public void store() throws IOException
    {
        if(registry != null)
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
        else
        {
            throw new IllegalStateException("The portlet registry for PortletDefinitionImpl has not been set.  "+
                                             "Please invoke PortletDefinitionImpl.setPortletRegistry before invoking the store() method.");
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
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addPreference(org.apache.pluto.om.common.Preference)
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

        Iterator descItr = newPref.getDescriptions();
        while (descItr.hasNext())
        {
            Description desc = (Description) descItr.next();
            newPref.addDescription(desc.getLocale(), desc.getDescription());
        }

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addSecurityRoleRef(org.apache.pluto.om.common.SecurityRoleRef)
     */
    public void addSecurityRoleRef( SecurityRoleRef securityRef )
    {
        secListWrapper.setInnerCollection(securityRoleRefSet);
        secListWrapper.add(securityRef);
    }
    
    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addSecurityRoleRef(java.lang.String, java.lang.String)
     */
    public SecurityRoleRef addSecurityRoleRef(String roleName, String roleLink)
    {
        SecurityRoleRefImpl ref = new SecurityRoleRefImpl();
        ref.setRoleName(name);
        ref.setRoleLink(roleLink);
        
        addSecurityRoleRef(ref);
        
        return ref;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getMetadata()
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
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setMetadata(org.apache.jetspeed.om.common.GenericMetadata)
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
        if (resourceBundle != null)
        {
            langListWrapper.setResources(resourceBundle);
        }
        
        if (parameter instanceof ClassLoader)
        {
            // newly created PD from portlet.xml
            langListWrapper.setClassLoader((ClassLoader) parameter);
            // create supported locale languages and
            // retrieve title, shortTitle and keywords from resourceBundle if defined
            langListWrapper.postLoad(this.supportedLocales);
        }
        else
        {
            // loaded from persistent store
            langListWrapper.setClassLoader(getPortletClassLoader());
        }
    }
    
    public static void setPortletRegistry(PortletRegistry registry)
    {
        PortletDefinitionImpl.registry = registry;
    }

    public static void setPortletFactory(PortletFactory portletFactory)
    {
        PortletDefinitionImpl.portletFactory = portletFactory;
    }
}
