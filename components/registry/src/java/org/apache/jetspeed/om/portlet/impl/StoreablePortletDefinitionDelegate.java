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
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.servlet.ServletDefinition;

/**
 * <p>
 * StoreablePortletDefinitionDelegate
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class StoreablePortletDefinitionDelegate implements PortletDefinitionComposite
{
	private PortletDefinitionComposite portlet;
	private PersistenceStore store;
	
	public StoreablePortletDefinitionDelegate(PortletDefinitionComposite portlet, PersistenceStore store)
	{
		this.portlet = portlet;
		this.store = store;
	}

    /** 
     * <p>
     * addLanguage
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addLanguage(org.apache.pluto.om.common.Language)
     * @param lang
     */
    public void addLanguage(Language lang)
    {
        portlet.addLanguage(lang);

    }
    
    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addLanguage(java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
     */
    public void addLanguage(String title, String shortTitle, String keywords, Locale locale)
    {
        portlet.addLanguage(title, shortTitle, keywords, locale);
    }

    /** 
     * <p>
     * addContentType
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addContentType(org.apache.pluto.om.portlet.ContentType)
     * @param cType
     */
    public void addContentType(ContentType cType)
    {
        portlet.addContentType(cType);

    }
    
    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addContentType(java.lang.String, java.lang.String[])
     */
    public void addContentType(String contentType, Collection modes)
    {
        portlet.addContentType(contentType, modes);
    }

    /** 
     * <p>
     * setLanguageSet
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setLanguageSet(org.apache.pluto.om.common.LanguageSet)
     * @param languages
     */
    public void setLanguageSet(LanguageSet languages)
    {
		portlet.setLanguageSet(languages);

    }

    /** 
     * <p>
     * setPreferenceSet
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPreferenceSet(org.apache.pluto.om.common.PreferenceSet)
     * @param preferences
     */
    public void setPreferenceSet(PreferenceSet preferences)
    {
		portlet.setPreferenceSet(preferences);

    }

    /** 
     * <p>
     * setInitParameterSet
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitParameterSet(org.apache.pluto.om.common.ParameterSet)
     * @param parameters
     */
    public void setInitParameterSet(ParameterSet parameters)
    {
		portlet.setInitParameterSet(parameters);

    }

    /** 
     * <p>
     * setContentTypeSet
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setContentTypeSet(org.apache.pluto.om.portlet.ContentTypeSet)
     * @param contentTypes
     */
    public void setContentTypeSet(ContentTypeSet contentTypes)
    {
		portlet.setContentTypeSet(contentTypes);

    }

    /** 
     * <p>
     * setInitSecurityRoleRefSet
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitSecurityRoleRefSet(org.apache.pluto.om.common.SecurityRoleRefSet)
     * @param securityRefs
     */
    public void setInitSecurityRoleRefSet(SecurityRoleRefSet securityRefs)
    {
		portlet.setInitSecurityRoleRefSet(securityRefs);

    }

    /** 
     * <p>
     * addInitParameter
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addInitParameter(java.lang.String, java.lang.String)
     * @param name
     * @param value
     * @return
     */
    public ParameterComposite addInitParameter(String name, String value)
    {        
        return portlet.addInitParameter(name, value);
    }

    /** 
     * <p>
     * addInitParameter
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addInitParameter(java.lang.String, java.lang.String, org.apache.pluto.om.common.DescriptionSet)
     * @param name
     * @param value
     * @param description
     * @return
     */
    public ParameterComposite addInitParameter(String name, String value, DescriptionSet description)
    {        
        return portlet.addInitParameter(name, value, description);
    }

    /** 
     * <p>
     * addInitParameter
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addInitParameter(java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
     * @param name
     * @param value
     * @param description
     * @param locale
     * @return
     */
    public ParameterComposite addInitParameter(String name, String value, String description, Locale locale)
    {
        
        return portlet.addInitParameter(name, value, description, locale);
    }

    /** 
     * <p>
     * setExpirationCache
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setExpirationCache(java.lang.String)
     * @param cache
     */
    public void setExpirationCache(String cache)
    {
		portlet.setExpirationCache(cache);

    }

    /** 
     * <p>
     * setPortletApplicationDefinition
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPortletApplicationDefinition(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     * @param pad
     */
    public void setPortletApplicationDefinition(PortletApplicationDefinition pad)
    {
		portlet.setPortletApplicationDefinition(pad);

    }

    /** 
     * <p>
     * addPreference
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addPreference(java.lang.String, java.lang.String[])
     * @param name
     * @param values
     * @return
     */
    public PreferenceComposite addPreference(String name, String[] values)
    {        
        return portlet.addPreference(name, values);
    }

    /** 
     * <p>
     * addPreference
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addPreference(org.apache.pluto.om.common.Preference)
     * @param preference
     */
    public void addPreference(Preference preference)
    {
		portlet.addPreference(preference);

    }

    /** 
     * <p>
     * setPortletIdentifier
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPortletIdentifier(java.lang.String)
     * @param portletIndentifier
     */
    public void setPortletIdentifier(String portletIndentifier)
    {
		portlet.setPortletIdentifier(portletIndentifier);

    }

    /** 
     * <p>
     * getPortletIdentifier
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getPortletIdentifier()
     * @return
     */
    public String getPortletIdentifier()
    {        
        return portlet.getPortletIdentifier();
    }

    /** 
     * <p>
     * getUniqueName
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getUniqueName()
     * @return
     */
    public String getUniqueName()
    {        
        return portlet.getUniqueName();
    }

    /** 
     * <p>
     * getDisplayNameText
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getDisplayNameText(java.util.Locale)
     * @param locale
     * @return
     */
    public String getDisplayNameText(Locale locale)
    {        
        return portlet.getDisplayNameText(locale);
    }

    /** 
     * <p>
     * getDescriptionText
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getDescriptionText(java.util.Locale)
     * @param locale
     * @return
     */
    public String getDescriptionText(Locale locale)
    {        
        return portlet.getDescriptionText(locale);
    }

    /** 
     * <p>
     * addDescription
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addDescription(java.util.Locale, java.lang.String)
     * @param locale
     * @param description
     */
    public void addDescription(Locale locale, String description)
    {
		portlet.addDescription(locale, description);

    }
    
    public DescriptionSet getDescriptionSet()
    {
        return portlet.getDescriptionSet();
    }

    /** 
     * <p>
     * addDisplayName
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addDisplayName(java.util.Locale, java.lang.String)
     * @param locale
     * @param displayName
     */
    public void addDisplayName(Locale locale, String displayName)
    {
		portlet.addDisplayName(locale, displayName);

    }
    
    public DisplayNameSet getDisplayNameSet()
    {
        return portlet.getDisplayNameSet();
    }

    /** 
     * <p>
     * addDisplayName
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addDisplayName(org.apache.pluto.om.common.DisplayName)
     * @param displayName
     */
    public void addDisplayName(DisplayName displayName)
    {
		portlet.addDisplayName(displayName);

    }

    /** 
     * <p>
     * getPreferenceValidatorClassname
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getPreferenceValidatorClassname()
     * @return
     */
    public String getPreferenceValidatorClassname()
    {        
        return portlet.getPreferenceValidatorClassname();
    }

    /** 
     * <p>
     * setPreferenceValidatorClassname
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPreferenceValidatorClassname(java.lang.String)
     * @param classname
     */
    public void setPreferenceValidatorClassname(String classname)
    {
		portlet.setPreferenceValidatorClassname(classname);

    }

    /** 
     * <p>
     * addSecurityRoleRef
     * </p>
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addSecurityRoleRef(org.apache.pluto.om.common.SecurityRoleRef)
     * @param securityRef
     */
    public void addSecurityRoleRef(SecurityRoleRef securityRef)
    {
		portlet.addSecurityRoleRef(securityRef);

    }
    
    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addSecurityRoleRef(java.lang.String, java.lang.String)
     */
    public SecurityRoleRef addSecurityRoleRef(String roleName, String roleLink)
    {
        return portlet.addSecurityRoleRef(roleName, roleLink);
    }

    /** 
     * <p>
     * getId
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getId()
     * @return
     */
    public ObjectID getId()
    {        
        return portlet.getId();
    }

    /** 
     * <p>
     * getClassName
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getClassName()
     * @return
     */
    public String getClassName()
    {        
        return portlet.getClassName();
    }

    /** 
     * <p>
     * getName
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getName()
     * @return
     */
    public String getName()
    {        
        return portlet.getName();
    }

    /** 
     * <p>
     * getDescription
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getDescription(java.util.Locale)
     * @param locale
     * @return
     */
    public Description getDescription(Locale locale)
    {        
        return portlet.getDescription(locale);
    }

    /** 
     * <p>
     * getLanguageSet
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getLanguageSet()
     * @return
     */
    public LanguageSet getLanguageSet()
    {        
        return portlet.getLanguageSet();
    }

    /** 
     * <p>
     * getInitParameterSet
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getInitParameterSet()
     * @return
     */
    public ParameterSet getInitParameterSet()
    {        
        return portlet.getInitParameterSet();
    }

    /** 
     * <p>
     * getInitSecurityRoleRefSet
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getInitSecurityRoleRefSet()
     * @return
     */
    public SecurityRoleRefSet getInitSecurityRoleRefSet()
    {        
        return portlet.getInitSecurityRoleRefSet();
    }

    /** 
     * <p>
     * getPreferenceSet
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPreferenceSet()
     * @return
     */
    public PreferenceSet getPreferenceSet()
    {
        
        return portlet.getPreferenceSet();
    }

    /** 
     * <p>
     * getContentTypeSet
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getContentTypeSet()
     * @return
     */
    public ContentTypeSet getContentTypeSet()
    {        
        return portlet.getContentTypeSet();
    }

    /** 
     * <p>
     * getPortletApplicationDefinition
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletApplicationDefinition()
     * @return
     */
    public PortletApplicationDefinition getPortletApplicationDefinition()
    {        
        return portlet.getPortletApplicationDefinition();
    }

    /** 
     * <p>
     * getServletDefinition
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getServletDefinition()
     * @return
     */
    public ServletDefinition getServletDefinition()
    {        
        return portlet.getServletDefinition();
    }

    /** 
     * <p>
     * getDisplayName
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getDisplayName(java.util.Locale)
     * @param locale
     * @return
     */
    public DisplayName getDisplayName(Locale locale)
    {
        
        return portlet.getDisplayName(locale);
    }

    /** 
     * <p>
     * getExpirationCache
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getExpirationCache()
     * @return
     */
    public String getExpirationCache()
    {        
        return portlet.getExpirationCache();
    }

    /** 
     * <p>
     * getPortletClassLoader
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletClassLoader()
     * @return
     */
    public ClassLoader getPortletClassLoader()
    {        
        return portlet.getPortletClassLoader();
    }

    /** 
     * <p>
     * setId
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setId(java.lang.String)
     * @param id
     */
    public void setId(String id)
    {
		portlet.setId(id);

    }

    /** 
     * <p>
     * setClassName
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setClassName(java.lang.String)
     * @param className
     */
    public void setClassName(String className)
    {
		portlet.setClassName(className);
    }

    /** 
     * <p>
     * setName
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setName(java.lang.String)
     * @param name
     */
    public void setName(String name)
    {
		portlet.setName(name);
    }

    /** 
     * <p>
     * setDescriptions
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setDescriptions(org.apache.pluto.om.common.DescriptionSet)
     * @param descriptions
     */
    public void setDescriptions(DescriptionSet descriptions)
    {
		portlet.setDescriptions(descriptions);

    }

    /** 
     * <p>
     * setDisplayNames
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setDisplayNames(org.apache.pluto.om.common.DisplayNameSet)
     * @param displayNames
     */
    public void setDisplayNames(DisplayNameSet displayNames)
    {
		portlet.setDisplayNames(displayNames);

    }

    /** 
     * <p>
     * setPortletClassLoader
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setPortletClassLoader(java.lang.ClassLoader)
     * @param loader
     */
    public void setPortletClassLoader(ClassLoader loader)
    {
		portlet.setPortletClassLoader(loader);

    }

    /** 
     * <p>
     * store
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#store()
     * @throws java.io.IOException
     */
    public void store() throws IOException
    {
        Transaction tx = store.getTransaction();
        if(!tx.isOpen())
        {
        	tx.begin();
        }
        try
        {
            store.lockForWrite(this);
            store.getTransaction().commit();
        }
        catch (LockFailedException e)
        {
            throw new IllegalStateException(e.toString());
        }

    }

    /**
     * @return Returns the portlet.
     */
    public PortletDefinitionComposite getPortlet()
    {
        return portlet;
    }
    
    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getMetadata()
     */
    public GenericMetadata getMetadata() {
        return portlet.getMetadata();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setMetadata(org.apache.jetspeed.om.common.GenericMetadata)
     */
    public void setMetadata(GenericMetadata metadata) {
        portlet.setMetadata(metadata);  
    }
}
